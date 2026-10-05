package LLL.entities.comp.packetcomps;

import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class PacketTransporterComp implements Teamc, Healthc, Syncc, SerializationEntrySeqc{
  @Annotations.Import
  boolean dead, added;
  @Annotations.Import
  float x, y;

  @Annotations.Import
  transient Seq<PacketEntry> packets = new Seq<>();

  float deltaTime(){
    return Time.delta;
  }

  boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
    return false;
  }

  void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
    packets.add(packetEntry);
  }

  void transferPacket(PacketEntry packetEntry, PacketTransportercProv to){
    PacketTransporterc eto = to.get();

    packetEntry.packet.owner(eto);
    eto.handlePacket(packetEntry, this::self);

    packets.remove(packetEntry);
  }

  void updateEachPacket(){
    packets.each(pe -> {
      handlePacketBoundary(pe);
      updatePacketEntry(pe);
    });
  }

  void updatePacketEntry(PacketEntry packetEntry){
    movePacket(packetEntry);
  }

  void movePacket(PacketEntry packetEntry){
    Packetc packet = packetEntry.packet;

    Vec2 toVel = Tmp.v1.set(packetEntry.targetPos).sub(packet).setLength(Math.min(Tmp.v1.len(), packetSpeed(packetEntry)));
    packet.vel().lerp(toVel, packetAccel() * deltaTime());
  }

  public int overrideCollisionLayer(){
    return 7;
  }

  public float maxPacketSpeed(){
    return 0.1f;
  }

  public float packetSpeed(PacketEntry packetEntry){
    return maxPacketSpeed() * packetEntry.packet.packetType().speedMulti;
  }

  //todo
  public float packetAccel(){
    return 0.002f;
  }

  public void handlePacketBoundary(PacketEntry packetEntry){
  }

  PacketEntry createPacketEntry(PacketType packetType){
    return createPacketEntry(packetType, x, y);
  }

  PacketEntry createPacketEntry(PacketType packetType, float x, float y){
    return PacketEntry.create(packetType.create(this, x, y));
  }

  void throwAllPackets(float speed){
    for(PacketEntry entry : packets){
      throwPacket(entry, speed);
    }
  }

  void throwPacket(PacketEntry packetEntry, float speed){
    if(!packets.contains(packetEntry, true)) return;

    packets.remove(packetEntry);

    packetEntry.packet.owner(null);
    if(speed > 0) packetEntry.packet.vel().set(Tmp.v1.setLength(speed).rotate(Mathf.random(360)));

    Pools.free(packetEntry);
  }

  void throwPacket(PacketEntry packetEntry){
    throwPacket(packetEntry, 0);
  }

  void removePacketEntry(PacketEntry packetEntry){
    Packetc packetc = packetEntry.packet;
    Pools.free(packetEntry);
    packetc.owner(null);
    packetc.remove();
  }

  //移除时, 封包四散移动开
  @Override
  public void remove(){
    throwAllPackets(dead ? 3 : 20);
  }
}
