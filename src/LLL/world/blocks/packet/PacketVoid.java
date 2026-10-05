package LLL.world.blocks.packet;

import LLL.ctype.packet.*;
import LLL.entities.*;
import mindustry.content.*;

public class PacketVoid extends PacketBlock{
  public PacketVoid(String name){
    super(name);
    rotate = false;
  }

  public class PacketVoidBuild extends PacketBlockBuild{
    @Override
    public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
      return true;
    }

    @Override
    public void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
      Fx.explosion.at(packetEntry.getX(), packetEntry.getY());
      removePacketEntry(packetEntry);
    }
  }
}
