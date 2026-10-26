package lulu.entities.comp.unitcomps;

import arc.graphics.g2d.*;
import lulu.ctype.packet.*;
import lulu.entities.*;
import lulu.entities.gen.*;
import lulu.entities.types.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import lulu.graphics.*;
import lulu.lib.singularity.graphic.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

@Annotations.EntityComponent
abstract class PacketUnitComp implements PacketTransporterc, Unitc, MDTXc{
  @Annotations.Import
  Seq<PacketEntry> packets;
  @Annotations.Import
  float x, y;
  @Annotations.Import
  UnitType type;

  public static Rand rand = new Rand();

  @Override
  public void update(){
    updateEachPacket();
  }

  public PacketUnitType typeAs(){
    return (PacketUnitType)type;
  }

  @Annotations.Replace
  @Override
  public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
    return true;
  }

  @Override
  public void draw(){
    Draw.draw(Layer.effect, () -> {
      Draw.color(Pal.accent);
      float maxDst = (type.speed * 10 + 32) * 1.5f;
      rand.setSeed(id());

      for(PacketEntry entry : packets){
        float ux = entry.getX();
        float uy = entry.getY();

        MathRenderer.setThreshold(0.03f, 0.03f);
        OvulamMathRenderers.drawParabola(x, y, ux, uy,
          Mathf.sin(rand.nextFloat() * 20 + 20, Math.max(rand.nextFloat() * Mathf.lerp(0.5f, 0f, entry.dst(this) / maxDst) + 0.1f, 0.06f)),
          Mathf.sin(rand.nextFloat() * 60 + 40, rand.nextFloat() * 20f + 10f));
      }
    });
  }

  @Annotations.Replace
  @Override
  public void movePacket(PacketEntry packetEntry){
    Packetc packet = packetEntry.packet;

    Vec2 toVel = Tmp.v1.set((Position)self()).sub(packet);
    float len = toVel.len();

    toVel.setLength(Math.max((len - 32) / 40, 0));

    if(len > (type.speed * 10 + 32) * 1.5f){
      //可以做成轨道炮(?
      throwPacket(packetEntry);
      toVel.scl(-0.7f);
    }

    packet.vel().set(toVel);
  }

  @Annotations.Replace
  @Override
  public float speed(){
    float strafePenalty = isGrounded() || !isPlayer() ? 1.0F : Mathf.lerp(1.0F, type.strafePenalty, Angles.angleDist(vel().angle(), rotation()) / 180.0F);
    float boost = Mathf.lerp(1.0F, type.canBoost ? type.boostMultiplier : 1.0F, elevation());

    float totalUnitBindDrag = 0;
    for(PacketEntry packet : packets){
      Vec2 toVel = Tmp.v1.set((Position)self()).sub(packet);
      float len = Math.max((toVel.len() - 32) / 40, 0);

      totalUnitBindDrag += packet.packet.packetType().unitBindDrag * len;
    }

    float packetDrag = Mathf.pow(Mathf.E, -totalUnitBindDrag / typeAs().packetDragResist);

    return type.speed * strafePenalty * boost * floorSpeedMultiplier() * packetDrag;
  }
}
