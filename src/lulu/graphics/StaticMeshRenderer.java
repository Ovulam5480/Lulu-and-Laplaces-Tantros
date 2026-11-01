package lulu.graphics;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.Events;
import lulu.*;
import lulu.entities.gen.StaticDrawc;
import mindustry.game.EventType.*;

import static arc.Core.camera;
import static mindustry.Vars.*;

public class StaticMeshRenderer{
  public static final int chunkSize = 30;
  public static final int maxSpritesPerCacheTile = 6;

  private final Seq<StaticDrawc> entities = new Seq<>(false);

  private SpriteCache[][][] caches;
  private int[][][] cacheIds;
  private boolean[][][] dirty;
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
    if(l >= 0 && l < StaticDrawLayer.amount && cx >= 0 && cy >= 0 && cx < chunksX && cy < chunksY) dirty[l][cx][cy] = true;
  }

  // 世界加载/尺寸变化时重置并标记全部分块为脏。
  public void reset(){
    if(caches != null){
      for(int l = 0; l < StaticDrawLayer.amount; l++){
        for(int x = 0; x < chunksX; x++){
          for(int y = 0; y < chunksY; y++){
            if(caches[l][x][y] != null) caches[l][x][y].dispose();
          }
        }
      }
    }
    chunksX = Mathf.ceil((float)world.width() / chunkSize);
    chunksY = Mathf.ceil((float)world.height() / chunkSize);
    caches = new SpriteCache[StaticDrawLayer.amount][chunksX][chunksY];
    cacheIds = new int[StaticDrawLayer.amount][chunksX][chunksY];
    dirty = new boolean[StaticDrawLayer.amount][chunksX][chunksY];
    for(int l = 0; l < StaticDrawLayer.amount; l++){
      for(boolean[] row : dirty[l]) java.util.Arrays.fill(row, true);
    }
  }

  // 重建某个分块在某个缓存层上的静态 mesh：录制所有位于该分块、且 staticLayer()==layer 的实体的 drawCached() 进 SpriteCache。
  // 首次/更新判定对齐原版 BlockRenderer.cacheChunk（cache == null 时新建，否则 beginCache(id) 重定义）。
  private void rebuild(int layer, int cx, int cy){
    if(layer < 0 || layer >= StaticDrawLayer.amount || cx < 0 || cy < 0 || cx >= chunksX || cy >= chunksY) return;

    int required = chunkSize * chunkSize * maxSpritesPerCacheTile;
    SpriteCache cache = caches[layer][cx][cy];
    if(cache == null){
      cache = caches[layer][cx][cy] = new SpriteCache(16382 - (16382 % required), true);
      cache.beginCache();
    }else{
      cache.beginCache(cacheIds[layer][cx][cy]);
    }

    // 录制：把绘制重定向到 cbatch -> cache，使实体的 Draw.* 调用被录进 SpriteCache。
    Batch last = Core.batch;
    Draw.flush();
    cbatch.cache = cache;
    Core.batch = cbatch;

    float x1 = cx * chunkSize * tilesize, y1 = cy * chunkSize * tilesize;
    float x2 = x1 + chunkSize * tilesize, y2 = y1 + chunkSize * tilesize;
    for(StaticDrawc e : entities){
      if(e.staticLayer() != layer || !e.shouldDrawStatic()) continue;
      float ex = e.x(), ey = e.y();
      if(ex >= x1 && ex < x2 && ey >= y1 && ey < y2){
        e.drawCached();
      }
    }

    Draw.flush();
    Core.batch = last;
    cache.reserve(required);
    cacheIds[layer][cx][cy] = cache.endCache();
    dirty[layer][cx][cy] = false;
  }

  // 在世界渲染阶段调用：按 StaticDrawLayer 顺序绘制所有可见分块的静态 mesh 缓存（脏分块先重建）。
  // 每层以各自的 z 通过 Draw.draw(z, ...) 合成，从而实现跨层遮挡。
  public void draw(){
    if(caches == null) return;

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
            if(dirty[layer][cx][cy]) rebuild(layer, cx, cy);
            SpriteCache cache = caches[layer][cx][cy];
            int id = cacheIds[layer][cx][cy];
            if(cache != null && id >= 0){
              cache.begin(false);
              cache.draw(id);
              cache.end();
            }
          }
        }
      });
    }
  }
}
