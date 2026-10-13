package LLL.world.blocks.packet;

import LLL.*;
import LLL.content.*;
import LLL.content.extensions.*;
import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.util.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import java.util.*;

//普通封包方块, 有方向, 拥有封包队列, 封包会移动至目标位置
public class PacketBlock extends Block{
  //封包最大移动速度
  public float maxPacketSpeed = 0.13f;
  //封包加速度
  public float packetAccel = 0.02f;
  //在边缘不存在阻拦时, 封包距离离开边缘的限制距离
  public float boundary = 0.01f;
  //旋转该建筑时, 是否同时旋转该建筑所有封包的目标位置
  public boolean rotationTarget = true;
  //有方向的建筑是否优先使用水平移动封包
  public boolean preferHorizontalTransport = true;
  //封包在边缘 并且 封包移动趋向离开该建筑时, 是否输出该封包至附近的建筑, 无论封包在不在建筑内
  public boolean dumpEdge = true;
  //在封包输出位置无可接收的封包建筑时, 是否直接释放该封包
  public boolean forceOutput = false;
  //是否能接收封包
  public boolean acceptPacket = true;
  //输出时是否只输出对面, 仅用于边界
  public boolean outputTop = true;
  //是否能制造封包项
  public boolean producePacketEntry = false;

  protected boolean has2 = false;

  public TextureRegion edgeRegion;
  public TextureRegion bottomRegion;
  public TextureRegion region2;

  protected Point2[] edges;
  protected Point2[] edgeInside;

  private EdgeMode[] edgeModes;

  public PacketBlock(String name){
    super(name);
    update = true;
    sync = true;

    size = 2;

    rotate = true;

    requirements(OvulamCategory.packet, BuildVisibility.shown, ItemStack.with(OvulamItems.manganeseNodule, 1));
  }

  @Override
  public void init(){
    super.init();
    edges = Edges.getEdges(size);
    edgeInside = Edges.getInsideEdges(size);

    edgeModes = setEdgeMode();
  }

  @Override
  public void load(){
    super.load();
    if(Core.atlas.has(name + "-2")){
      has2 = true;
      region2 = Core.atlas.find(name + "-2");
    }
    edgeRegion = Core.atlas.find(LuluMod.modName + "packet-edge");
    bottomRegion = Core.atlas.find(Core.atlas.has(name + "-bottom") ?
      (name + "-bottom") : ("lulu-bottom-" + size));
  }

  public TextureRegion loadBlockRegion(String suffix){
    return Core.atlas.find(name + suffix);
  }

  @Override
  public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
    if(has2) drawPlanRegionDir(plan, list);
    else super.drawPlanRegion(plan, list);
  }

  public void drawPlanRegionDir(BuildPlan plan, Eachable<BuildPlan> list){
    if(plan.rotation % 2 == 1) Draw.scl(1, -1);
    Draw.rect(plan.rotation < 2 ? region : region2, plan.drawx(), plan.drawy(), plan.rotation * 90);
    Draw.scl();
  }

  public EdgeMode[] setEdgeMode(){
    if(!rotate && acceptPacket){
      return new EdgeMode[]{EdgeMode.IN, EdgeMode.IN, EdgeMode.IN, EdgeMode.IN};
    }else if(outputTop){
      return new EdgeMode[]{EdgeMode.OUT, EdgeMode.IN, EdgeMode.IN, EdgeMode.IN};
    }else{
      return new EdgeMode[]{EdgeMode.OUT, EdgeMode.OUT, EdgeMode.IN, EdgeMode.OUT};
    }
  }

  public EdgeMode getEdgeMode(int dir){
    return edgeModes[Mathf.mod(dir, 4)];
  }

  public enum EdgeMode{
    IN,
    OUT,
    NONE
  }

  @Annotations.ImplEntries
  public class PacketBlockBuild extends PacketTransporterBuilding{
    int preRotation = rotation;
    public Building[] nexts = new Building[size];
    public boolean[] hasBlends = {false, false, false, false};
    public boolean[] hadEdge = new boolean[size * 4];

    //todo payload packet
    @Override
    public boolean canControlSelect(Unit unit){
      return !unit.spawnedByCore && unit.tileOn() != null && unit.tileOn().build == this;
    }

    @Override
    public void onControlSelect(Unit player){
      if(player instanceof Packetc p){
        Fx.spawn.at(player);
        if(player.isPlayer()){
          player.getPlayer().clearUnit();
        }
        Fx.unitDrop.at(player);

        packets.add(PacketEntry.create(p, this));
      }
    }

    @Override
    public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
      Packetc toAccept = packetEntry.packet;
      for(PacketEntry entry : packets){
        if(entry.within(packetEntry, (toAccept.hitSize() + entry.packet.hitSize()) / 2f)){
          return false;
        }
      }
      return toAccept.packetType().size <= size;
    }

    @Override
    public void onProximityUpdate(){
      super.onProximityUpdate();

      if(rotationTarget && rotation != preRotation){
        packets.each(pe -> pe.targetPos.rotateAround(Tmp.v1.set(this), (rotation - preRotation) * 90));
        preRotation = rotation;
      }

      Arrays.fill(hasBlends, false);
      proximity.each(other -> {
        int dir = getDirection(other);

        if(!hasBlends[dir] && hasEdgeFrom(other)){
          hasBlends[dir] = true;
        }
      });

      setEdges();
    }

    public void setEdges(){
      Arrays.fill(hadEdge, true);

      for(int i = 0; i < edges.length; i++){
        Point2 edge = edges[i];
        Tile t = Vars.world.tile(tile.x + edge.x, tile.y + edge.y);

        if(t != null && hasEdgeFrom(t.build) || (forceOutput && rotation == i / size)){
          hadEdge[i] = false;
        }
      }
    }

    public boolean hasEdgeFrom(Building building){
      if(building != null && building.block instanceof PacketBlock pb){
        EdgeMode mode = getEdgeMode(getDirection(building));
        EdgeMode pbMode = pb.getEdgeMode(((PacketBlockBuild)building).getDirection(this));

        return (mode == EdgeMode.OUT && pbMode == EdgeMode.IN)
          || (mode == EdgeMode.IN && pbMode == EdgeMode.OUT);
      }else{
        return false;
      }
    }

    public @Nullable PacketEntry getOutputPacket(){
      return null;
    }

    @Override
    public void updateTile(){
      updateEachPacket();
    }

    @Override
    public float deltaTime(){
      return edelta();
    }

    @Override
    public void draw(){
      Draw.rect(bottomRegion, x, y, size * 8, size * 8);

      drawEdge();

      Draw.rect(region, x, y);
    }

    public void drawDirRegion(){
      if(rotation % 2 == 1) Draw.scl(1, -1);
      Draw.rect(rotation < 2 ? region : region2, x, y, rotdeg());
      Draw.scl();
    }

    public void drawEdge(){
      int rot = 0;
      int start = getInsideEdges()[0].x;

      for(int i = 0; i < size * 4; i++){
        int edgeIndex = Mathf.mod(i - (size == 2 ? 0 : 1), size * 4);

        if(hadEdge[edgeIndex]){
          float ex = (tileX() + edgeInside[edgeIndex].x) * 8;
          float ey = (tileY() + edgeInside[edgeIndex].y) * 8;

          Draw.rect(edgeRegion, ex, ey, rot * 90);
        }

        if((edgeIndex - start) % size == 0){
          rot++;
        }
      }
    }

    @Override
    public void handlePacketBoundary(PacketEntry packetEntry){
      Packetc packet = packetEntry.packet;
      float hitSize = packet.hitSize();

      Rect bound = Tmp.r1;
      hitbox(bound);
      bound.grow(-(boundary + hitSize));

      if(bound.contains(packet.x(), packet.y())){
        return;
      }

      int rightIndex = Mathf.clamp(Mathf.floor((size - 1) * 0.5f) + packet.tileY() - tileY(), 0, size - 1);
      int leftIndex = size * 3 - 1 - rightIndex;
      int topIndex = Mathf.clamp(Mathf.floor(size * 1.5f) + (packet.tileX() - tileX()), size, size * 2 - 1);
      int bottomIndex = size * 5 - 1 - topIndex;

      float boundRadius = size * 8 / 2f - boundary;

      float right = x + boundRadius - (hadEdge[rightIndex] ? hitSize / 2f : 0);
      float left = x - boundRadius + (hadEdge[leftIndex] ? hitSize / 2f : 0);
      float top = y + boundRadius - (hadEdge[topIndex] ? hitSize / 2f : 0);
      float bottom = y - boundRadius + (hadEdge[bottomIndex] ? hitSize / 2f : 0);

      packet.x(Mathf.clamp(packet.x(), left, right));
      packet.y(Mathf.clamp(packet.y(), bottom, top));
    }

    @Override
    public void updatePacketEntry(PacketEntry packetEntry){
      movePacket(packetEntry);

      if(rotate && preferHorizontalTransport){
        Packetc packet = packetEntry.packet;

        float len = Tmp.v1.len();

        if(rotation % 2 == 0) Tmp.v1.scl(1, 2);
        else Tmp.v1.scl(2, 1);

        packet.vel().lerp(Tmp.v1.setLength(len), packetAccel() * deltaTime());
      }

      if(dumpEdge) outputEdgePackets(packetEntry);
    }

    public boolean outputEdgePackets(PacketEntry packetEntry){
      Packetc packet = packetEntry.packet;

      Vec2 vec2 = Tmp.v1.set(packet).sub(this);
      vec2.setLength(vec2.len() + 0.2f);

      float boundary = size * 8 / 2f - 0.1f;
      float absX = Math.abs(packet.x() - x) - boundary, absY = Math.abs(packet.y() - y) - boundary;

      if(vec2.dot(packet.vel()) < 0
        || (outputTop ? ((rotation % 2 == 0 ? absX : absY) < 0) : (Math.max(absX, absY) < 0))){
        return false;
      }

      Building build = Vars.world.buildWorld(x + vec2.x, y + vec2.y);

      if(build instanceof PacketTransporterc pt){
        if(pt.acceptPacket(packetEntry, () -> this)){
          transferPacket(packetEntry, () -> pt);
          return true;
        }
        return false;
      }

      if(forceOutput){
        packetEntry.packet.owner(null);
        Pools.free(packetEntry);
        packets.remove(packetEntry);
        return true;
      }

      return false;
    }

    @Override
    public float maxPacketSpeed(){
      return maxPacketSpeed;
    }

    @Override
    public float packetSpeed(PacketEntry packetEntry){
      return super.packetSpeed(packetEntry) * timeScale();
    }

    @Override
    public float packetAccel(){
      return packetAccel;
    }

    public boolean inTop(Position p2){
      return DirectionUtils.inTop(this, p2, rotdeg());
    }

    public int getDirection(Position p2){
      return DirectionUtils.getDirection(this, p2, rotdeg());
    }

    public int getQuadrant(Position p2){
      return DirectionUtils.getQuadrant(this, p2, rotdeg());
    }
  }
}
