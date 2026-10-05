package LLL.entities.comp.unitcomps;

import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.entities.types.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
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
    packets.each(p -> Drawf.line(Color.acid, p.getX(), p.getY(), x, y));
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
      totalUnitBindDrag += packet.packet.packetType().unitBindDrag;
    }

    float packetDrag = Mathf.pow(Mathf.E, -totalUnitBindDrag / typeAs().packetDragResist);

    return type.speed * strafePenalty * boost * floorSpeedMultiplier() * packetDrag;
  }
}
