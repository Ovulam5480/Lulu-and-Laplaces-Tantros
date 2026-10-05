package LLL.ctype.packet;

import LLL.entities.gen.*;
import arc.math.geom.*;
import arc.util.pooling.*;

public class PacketEntry implements Pool.Poolable, Position{
  public Packetc packet;
  public Vec2 targetPos = new Vec2();

  private PacketEntry(){
  }

  public void set(Packetc packet){
    this.packet = packet;
    this.targetPos.set(packet);
  }

  public void setPacket(Packetc packet){
    this.packet = packet;
  }

  public void setTargetPos(Position pos){
    this.targetPos.set(pos);
  }

  public boolean arrived(){
    return packet.within(targetPos, 0.01f);
  }

  @Override
  public void reset(){
    packet = null;
    targetPos.setZero();
  }

  public static PacketEntry create(){
    return Pools.obtain(PacketEntry.class, PacketEntry::new);
  }

  public static PacketEntry create(Packetc packet){
    PacketEntry entry = create();
    entry.set(packet);

    return entry;
  }

  public static PacketEntry create(Packetc packet, Position pos){
    PacketEntry entry = create();

    entry.setPacket(packet);
    entry.setTargetPos(pos);
    return entry;
  }

  @Override
  public float getX(){
    return packet.x();
  }

  @Override
  public float getY(){
    return packet.y();
  }
}
