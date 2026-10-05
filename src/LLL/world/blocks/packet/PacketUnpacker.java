package LLL.world.blocks.packet;

import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;

public class PacketUnpacker extends UnpackerBlock{
  public float excavateTime = 60f;

  public PacketUnpacker(String name){
    super(name);
  }

  public class PacketUnpackerBuild extends UnpackerBuild<Packetc>{

    @Override
    public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
      if(filterType != null && packetEntry.packet.packetType() != filterType){
        return false;
      }

      if(!filter.contains(packetEntry.packet.packetType().resourceStackClass)){
        return false;
      }
      return super.acceptPacket(packetEntry, source);
    }

    @Override
    public void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
      toUnpacks.add(packetEntry.packet);
      super.handlePacket(packetEntry, source);

      packetEntry.targetPos.set(this);
    }

    @Override
    public void updateTile(){
      if(stack == null){
        setStack();
      }

      if(efficiency > 0){
        progress += edelta() / excavateTime;
      }

      super.updateTile();
    }
  }
}
