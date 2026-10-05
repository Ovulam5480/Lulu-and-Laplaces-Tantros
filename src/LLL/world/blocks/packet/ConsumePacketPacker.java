package LLL.world.blocks.packet;

import LLL.content.*;
import LLL.content.resourceTypes.*;
import mindustry.gen.*;
import mindustry.type.*;

//非封包消耗器封装机, 消耗器负责提供封装
public class ConsumePacketPacker extends PackerBlock{
  public Power powerType = OvulamResource.power;
  public float craftTime = 60f;

  public ConsumePacketPacker(String name){
    super(name);
  }

  public class PacketPackerBuild extends PackerBuild{
    public float progress;

    @Override
    public boolean acceptItem(Building source, Item item){
      return items.get(item) < getMaximumAccepted(item);
    }

    @Override
    public boolean acceptLiquid(Building source, Liquid liquid){
      return liquids.get(liquid) < liquidCapacity + (block.consumesLiquid(liquid) ? packetType.capacity : 0);
    }

    @Override
    public int getMaximumAccepted(Item item){
      return super.getMaximumAccepted(item) + (block.consumesItem(item) ? (int)packetType.capacity : 0);
    }

    @Override
    public void updateTile(){
      if(!prepared){
        progress += getProgressIncrease(craftTime);

        if(progress >= 1){
          consume();
          progress %= 1;
          prepared = true;
        }
      }

      super.updateTile();
    }
  }
}
