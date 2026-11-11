package lulu.graphics.renderer;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import lulu.*;
import lulu.entities.gen.*;
import mindustry.game.EventType.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class StaticMeshRenderer{
  public static final int chunkSize = 30;
  public static final int maxSpritesPerCacheTile = 6;

  private static final byte stateDirty = 0, stateCached = 1, stateEmpty = 2;

  private final Seq<StaticDrawc> entities = new Seq<>(false);
  private final Seq<StaticDrawc> tmpEntities = new Seq<>(false);

  /**
   * 每个静态绘制层共用一个 SpriteCache 池；多个 chunk 通过不同 cacheId 复用同一个 Mesh。
   */
  private Seq<SpriteCache>[] pools;
  private int[][][] cacheIds;
  private int[][][] poolIndex;
  private byte[][][] state;
  private IntSeq[][] buckets;
  private int chunksX, chunksY;

  private final CacheBatch cbatch = new CacheBatch(null);

  public StaticMeshRenderer(){
    Events.on(WorldLoadEvent.class, e -> reset());
    Events.run(Trigger.draw, this::draw);
  }

  public static void register(StaticDrawc e){
    LuluMod.staticMeshRenderer.entities.add(e);
    LuluMod.staticMeshRenderer.markDirty(e);
  }

  public static void unregister(StaticDrawc e){
    LuluMod.staticMeshRenderer.entities.remove(e, true);
    LuluMod.staticMeshRenderer.markDirty(e);
  }

  public static void recache(StaticDrawc e){
    LuluMod.staticMeshRenderer.markDirty(e);
  }

  private int chunkIndex(float world){
    return Mathf.floor(world / (chunkSize * tilesize));
  }

  private void markDirty(StaticDrawc e){
    markDirty(e.staticLayer(), chunkIndex(e.x()), chunkIndex(e.y()));
  }

  private void markDirty(int l, int cx, int cy){
    // 空分块被标脏后需重新检查，否则新注册的实体不会被录制
    if(state == null) return;
    if(l >= 0 && l < StaticDrawLayer.amount && cx >= 0 && cy >= 0 && cx < chunksX && cy < chunksY)
      state[l][cx][cy] = stateDirty;
  }

  @SuppressWarnings("unchecked")
  public void reset(){
    if(pools != null){
      for(Seq<SpriteCache> pool : pools){
        for(SpriteCache cache : pool) cache.dispose();
        pool.clear();
      }
    }
    chunksX = Mathf.ceil((float)world.width() / chunkSize);
    chunksY = Mathf.ceil((float)world.height() / chunkSize);

    pools = new Seq[StaticDrawLayer.amount];
    for(int l = 0; l < StaticDrawLayer.amount; l++) pools[l] = new Seq<>(4);

    cacheIds = new int[StaticDrawLayer.amount][chunksX][chunksY];
    poolIndex = new int[StaticDrawLayer.amount][chunksX][chunksY];
    state = new byte[StaticDrawLayer.amount][chunksX][chunksY];
    for(int l = 0; l < StaticDrawLayer.amount; l++){
      for(int[] row : cacheIds[l]) java.util.Arrays.fill(row, -1);
      for(int[] row : poolIndex[l]) java.util.Arrays.fill(row, -1);
      // byte 默认 0 == stateDirty
    }

    buckets = new IntSeq[StaticDrawLayer.amount][];
    for(int l = 0; l < StaticDrawLayer.amount; l++){
      buckets[l] = new IntSeq[4];
      for(int i = 0; i < buckets[l].length; i++) buckets[l][i] = new IntSeq();
    }
  }

  /**
   * 重建某个分块在某个缓存层上的静态 mesh。
   * <p>
   * 与原版 BlockRenderer.cacheChunk 一致：按 layer 从池里取一个能容纳 required 的 SpriteCache，
   * 多个 chunk 以不同 cacheId 复用同一个 Mesh。空分块不创建任何 cache，仅记为 stateEmpty。
   */
  private void rebuild(int layer, int cx, int cy){
    if(layer < 0 || layer >= StaticDrawLayer.amount || cx < 0 || cy < 0 || cx >= chunksX || cy >= chunksY) return;

    float x1 = cx * chunkSize * tilesize, y1 = cy * chunkSize * tilesize;
    float x2 = x1 + chunkSize * tilesize, y2 = y1 + chunkSize * tilesize;

    tmpEntities.clear();
    for(StaticDrawc e : entities){
      if(e.staticLayer() != layer || !e.shouldDrawStatic()) continue;
      float ex = e.x(), ey = e.y();
      if(ex >= x1 && ex < x2 && ey >= y1 && ey < y2) tmpEntities.add(e);
    }

    if(tmpEntities.isEmpty()){
      state[layer][cx][cy] = stateEmpty;
      cacheIds[layer][cx][cy] = -1;
      poolIndex[layer][cx][cy] = -1;
    }else{
      int required = chunkSize * chunkSize * maxSpritesPerCacheTile;
      Seq<SpriteCache> pool = pools[layer];
      if(pool.isEmpty() || pool.peek().getSpritesUsed() + required > pool.peek().getSpriteCapacity()){
        // 只有当前 Mesh 装不下时才追加新实例；这与原版一致，Mesh 数量与内容量绑定而非 chunk 数
        pool.add(new SpriteCache(16382 - (16382 % required), true));
      }

      int index = pool.size - 1;
      SpriteCache cache = pool.get(index);
      // state 此时必为 stateDirty（rebuild 的触发条件），不能据此判断；
      // 用之前记录的有效 cacheId 与所属池索引判断是否可原地重定义，避免每次标脏都废弃旧 id
      int previousId = cacheIds[layer][cx][cy];
      boolean redefine = previousId >= 0 && poolIndex[layer][cx][cy] == index;
      if(redefine){
        cache.beginCache(previousId);
      }else{
        cache.beginCache();
      }

      Batch last = Core.batch;
      Draw.flush();
      cbatch.cache = cache;
      Core.batch = cbatch;
      for(StaticDrawc e : tmpEntities) e.drawCached();
      Draw.flush();
      Core.batch = last;

      cache.reserve(required);
      cacheIds[layer][cx][cy] = cache.endCache();
      poolIndex[layer][cx][cy] = index;
      state[layer][cx][cy] = stateCached;
    }
  }

  /**
   * 在世界渲染阶段调用：按 StaticDrawLayer 顺序绘制可见分块的静态 mesh 缓存（脏分块先重建）。
   */
  public void draw(){
    if(pools == null) return;

    float left = camera.position.x - camera.width / 2f;
    float bottom = camera.position.y - camera.height / 2f;
    int cx0 = Math.max(0, chunkIndex(left));
    int cy0 = Math.max(0, chunkIndex(bottom));
    int cx1 = Math.min(chunksX - 1, chunkIndex(left + camera.width));
    int cy1 = Math.min(chunksY - 1, chunkIndex(bottom + camera.height));

    StaticDrawLayer[] layers = StaticDrawLayer.all;
    for(int l = 0; l < layers.length; l++){
      final int layer = l;
      Draw.draw(layers[l].z, () -> {
        Draw.flush();
        SpriteCache.getDefaultShader().bind();
        SpriteCache.getDefaultShader().setUniformMatrix4("u_projectionViewMatrix", camera.mat);

        for(int cx = cx0; cx <= cx1; cx++){
          for(int cy = cy0; cy <= cy1; cy++){
            if(state[layer][cx][cy] == stateDirty) rebuild(layer, cx, cy);
          }
        }

        Seq<SpriteCache> pool = pools[layer];
        int size = pool.size;
        if(buckets[layer].length < size){
          int old = buckets[layer].length;
          buckets[layer] = java.util.Arrays.copyOf(buckets[layer], Math.max(size, old * 2));
          for(int i = old; i < buckets[layer].length; i++) buckets[layer][i] = new IntSeq();
        }
        for(int i = 0; i < size; i++) buckets[layer][i].clear();

        for(int cx = cx0; cx <= cx1; cx++){
          for(int cy = cy0; cy <= cy1; cy++){
            if(state[layer][cx][cy] != stateCached) continue;
            int index = poolIndex[layer][cx][cy];
            int id = cacheIds[layer][cx][cy];
            if(index >= 0 && index < size && id >= 0) buckets[layer][index].add(id);
          }
        }

        for(int i = 0; i < size; i++){
          IntSeq ids = buckets[layer][i];
          if(ids.isEmpty()) continue;
          SpriteCache cache = pool.get(i);
          cache.begin(false);
          for(int k = 0; k < ids.size; k++) cache.draw(ids.get(k));
          cache.end();
        }
      });
    }
  }
}
