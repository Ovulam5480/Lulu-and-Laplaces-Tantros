package LLL.ctype.packet.packetTypes;

import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.util.*;
import mindustry.gen.*;

public class MultiSlotPacketType extends PacketType{
  public int slotCount;

  public MultiSlotPacketType(String name){
    super(name);
  }

  @Override
  public float maxAccept(Packetc packet, Object item, float amount){
    if(!resourceClass.isInstance(item)) return 0;

    ResourceStack<?> stack = packet.resources().find(r -> r.item == item);
    if(stack != null){
      return Math.min(capacity - stack.amount, amount);
    }

    return packet.resources().size < slotCount ? Math.min(capacity, amount) : 0;
  }

  @Override
  public void handle(Packetc packet, @Nullable Entityc entityc, float amount, Object item){
    ResourceStack<?> stack = packet.resources().find(r -> r.item == item);

    if(stack != null){
      stack.amount += amount;
    }else if(packet.resources().size < slotCount){
      packet.resources().add(ResourceStackManager.getResourceInstanceByClass(item, resourceClass, amount));
    }
  }
}
