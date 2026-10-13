package LLL.entities.comp.packetcomps;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
@SuppressWarnings("unused")
abstract class PacketComp implements Hitboxc, Velc, Rotc, MDTXc{
  @Annotations.Import
  float hitSize;
  PacketType packetType;
  Seq<ResourceStack<?>> resources = new Seq<>();
  Teamc owner;

  private Object typeCache;

  void setPacketType(PacketType packetType){
    this.packetType = packetType;
    this.hitSize = packetType.size * 8;
  }

  @Override
  public void update(){
    packetType.updatePacket(self());
    resources.each(ResourceStack::update);
  }

  public void copyResourceStacks(Seq<ResourceStack<?>> stacks){
    resources.clear();
    for(ResourceStack<?> resource : stacks){
      resources.add(resource.copy());
    }
  }

  public void copyResourceStacks(Seq<ResourceStack<?>> stacks, float multi){
    resources.clear();
    for(ResourceStack<?> resource : stacks){
      ResourceStack<?> copy = resource.copy();
      copy.amount *= multi;

      resources.add(copy);
    }
  }

  void despawn(){
    remove();
  }

  float maxAccept(Object item, float amount){
    return packetType.maxAccept(self(), item, amount);
  }

  void handle(@Nullable Entityc entityc, Object item, float amount){
    packetType.handle(self(), entityc, amount, item);
  }

  void unpack(@Nullable Entityc entityc, float amount){
    packetType.unpack(self(), entityc, amount);
  }

  boolean unpack(@Nullable Entityc entityc, ResourceStack<?> item, float amount){
    if(resources.contains(item)){
      return packetType.unpack(self(), entityc, amount, item);
    }
    return false;
  }

  float capacity(){
    return packetType.capacity;
  }

  float amount(){
    return resources.sumf(r -> r.amount);
  }

  float remainSpace(){
    return packetType.capacity - resources.sumf(r -> r.amount);
  }

  void handle(Object item, Entityc entityc){
    handle(entityc, item, 1);
  }

  @Override
  public void add(){
    LuluMod.oIndexer.all.add((Packetc)self());
  }

  @Override
  public void remove(){
    LuluMod.oIndexer.all.remove((Packetc)self());
  }

  @Override
  public void afterRead(){
    hitSize = packetType.size * 8;
  }
}