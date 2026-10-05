package LLL.world.blocks.distribution;

import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.world.blocks.packet.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

import static mindustry.Vars.*;

//只负责与建筑相同方向的加速
public class PacketConveyor extends PacketBlock{
  public TextureRegion topRegion, iconRegion;
  public float animalTime = -1;

  protected final Vec2 start = new Vec2(-1, 1).scl(size / 2f * 8);
  protected static final Vec2 startDir = new Vec2(1, 0);

  public PacketConveyor(String name){
    super(name);

    maxPacketSpeed = 0.2f;
    packetAccel = 0.04f;
    dumpEdge = true;
    rotate = true;
    forceOutput = true;
  }

  @Override
  public void init(){
    super.init();
    if(animalTime < 0){
      animalTime = 8 * size / maxPacketSpeed;
    }
  }

  @Override
  public void load(){
    super.load();

    topRegion = loadBlockRegion("-top");
    iconRegion = loadBlockRegion("-icon");
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{iconRegion};
  }

  public class PacketConveyorBuild extends PacketBlockBuild{
    @Override
    public void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
      super.handlePacket(packetEntry, source);
      setPacketTarget(packetEntry, packetEntry.targetPos);
    }

    @Override
    public void movePacket(PacketEntry packetEntry){
      float angle = Tmp.v1.set(packetEntry.packet).sub(packetEntry.targetPos).rotate(-rotation * 90).angle();

      if(Math.abs(angle - 180) > 50){
        setPacketTarget(packetEntry, packetEntry.targetPos);
      }

      super.movePacket(packetEntry);
    }

    public void setPacketTarget(PacketEntry packetEntry, Vec2 targetToSet){
      int direction = getDirection(packetEntry);

      packetTarget(packetEntry, targetToSet, direction % 2 == 0, direction / 2 == 1);
    }

    public void packetTarget(PacketEntry packetEntry, Vec2 targetToSet, boolean canPass, boolean isRight){
      float blockRadius = size / 2f * 8;

      if(canPass){//直行
        if(rotation % 2 == 0){
          targetToSet.x = x + blockRadius * Geometry.d4x(rotation);
          targetToSet.y = packetEntry.getY();
        }else{
          targetToSet.x = packetEntry.getX();
          targetToSet.y = y + blockRadius * Geometry.d4y(rotation);
        }
      }else{//转弯
        Vec2 packet = Tmp.v1.set(packetEntry.packet).sub(this).rotate((1 - rotation) * 90);
        Vec2 packetDir = Tmp.v2.trns(isRight ? 135 : 45, 1);
        float scl = Intersector.intersectRayRay(packet, packetDir, start, startDir);

        targetToSet.set(packetDir).scl(scl).add(packet).rotate((rotation - 1) * 90).add(this);
      }
    }

    @Override
    public void draw(){
      Draw.rect(bottomRegion, x, y);
      drawEdge();

      float dst = 0.8f;

      float glow = Math.max((dst - (Math.abs(fract() - 0.5f) * 2)) / dst, 0);
      Draw.mixcol(team.color, glow);

      float s = tilesize * size;
      float trnext = s * fract(), trprev = s * (fract() - 1), rot = rotdeg();

      //next
      TextureRegion clipped = clipRegion(tile.getHitbox(Tmp.r1), tile.getHitbox(Tmp.r2).move(trnext, 0), topRegion);
      float widthNext = (s - clipped.width * clipped.scl()) * 0.5f;
      float heightNext = (s - clipped.height * clipped.scl()) * 0.5f;
      Tmp.v1.set(widthNext, heightNext).rotate(rot);
      Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, rot);

      //prev
      clipped = clipRegion(tile.getHitbox(Tmp.r1), tile.getHitbox(Tmp.r2).move(trprev, 0), topRegion);
      float widthPrev = (clipped.width * clipped.scl() - s) * 0.5f;
      float heightPrev = (clipped.height * clipped.scl() - s) * 0.5f;
      Tmp.v1.set(widthPrev, heightPrev).rotate(rot);
      Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, rot);

      for(int i = 0; i < 4; i++){
        int index = Mathf.mod((i + rotation), 4);
        if(hasBlends[i] && i != 0){
          Draw.alpha(1f - Interp.pow5In.apply(fract()));
          //prev from back
          Tmp.v1.set(widthPrev, heightPrev).rotate(index * 90 + 180);
          Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, index * 90 + 180);
        }
      }

      Draw.reset();

      drawEdge();
    }

    public float fract(){
      return Time.time % animalTime / animalTime;
    }

    protected TextureRegion clipRegion(Rect bounds, Rect sprite, TextureRegion region){
      Rect over = Tmp.r3;

      boolean overlaps = Intersector.intersectRectangles(bounds, sprite, over);

      TextureRegion out = Tmp.tr1;
      out.set(region.texture);
      out.scale = region.scale;

      if(overlaps){
        float w = region.u2 - region.u;
        float h = region.v2 - region.v;
        float x = region.u, y = region.v;
        float newX = (over.x - sprite.x) / sprite.width * w + x;
        float newY = (over.y - sprite.y) / sprite.height * h + y;
        float newW = (over.width / sprite.width) * w, newH = (over.height / sprite.height) * h;

        out.set(newX, newY, newX + newW, newY + newH);
      }else{
        out.set(0f, 0f, 0f, 0f);
      }

      return out;
    }
  }
}
