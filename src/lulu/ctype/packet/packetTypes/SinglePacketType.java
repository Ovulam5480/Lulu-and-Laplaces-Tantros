package lulu.ctype.packet.packetTypes;

import lulu.ctype.packet.*;
import lulu.entities.gen.*;
import lulu.type.resourceStacks.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

//只允许储存一种封包资源
public class SinglePacketType extends PacketType{
  public SinglePacketType(String name){
    super(name);
  }

  @Override
  public float maxAccept(Packetc packet, Object item, float amount){
    if(!resourceClass.isInstance(item)) return 0;

    return packet.resources().isEmpty() || packet.resources().first().item == item ? packet.remainSpace() : 0;
  }

  @Override
  public void handle(Packetc packet, @Nullable Entityc entityc, float amount, Object item){
    Seq<ResourceStack<?>> resources = packet.resources();
    ResourceStack<?> resourceStack;

    if(resources.isEmpty()){
      resourceStack = ResourceStackManager.getResourceInstanceByClass(item, resourceClass, amount);
      resources.add(resourceStack);
    }else{
      resourceStack = resources.first();
      resourceStack.amount += amount;
    }

    if(entityc != null) resourceStack.applyPack(entityc, item, amount);
  }
}
