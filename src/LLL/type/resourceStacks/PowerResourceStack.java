package LLL.type.resourceStacks;

import LLL.content.*;
import LLL.content.resourceTypes.*;
import LLL.world.blocks.packet.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;

public class PowerResourceStack extends ResourceStack<Power>{
  public static Seq<Power> powers = new Seq<>();

  @Override
  public String name(){
    return item.name;
  }

  @Override
  public boolean discrete(){
    return false;
  }

  @Override
  public int id(){
    return 2;
  }

  @Override
  public void register(){
    ResourceStackManager.register(Power.class, PowerResourceStack.class, PowerResourceStack::new, new PowerResourceStack(), id());
  }

  @Override
  public void init(){
    ResourceStackManager.init(Power.class, powers);
  }

  @Override
  public void applyBlock(Block block){
    block.hasPower = true;
  }

  @Override
  public Power findAvailableResource(Entityc entityc, float amount){
    if(entityc instanceof Building b){
      if(b.block instanceof ConsumePacketPacker pp){
        return pp.powerType;
      }else{
        return OvulamResource.power;
      }
    }
    return null;
  }

  @Override
  public void applyPack(Entityc entityc, Object object, float amount){
    if(entityc instanceof Building b && b.block.hasPower){
      b.power.graph.useBatteries(amount);
    }
  }

  //todo 解包建筑在拥有电池容量时会直接充满电的问题
  @Override
  public boolean unpack(Entityc entityc, float amount){
    if(entityc instanceof Building b && b.block.hasPower){
      this.amount -= b.power.graph.chargeBatteries(Math.min(amount, this.amount));
      return this.amount <= 0;
    }
    return false;
  }

  @Override
  public boolean canUnpack(Entityc entityc){
    if(entityc instanceof Building b && b.block.hasPower){
      return b.power.graph.getBatteryCapacity() >= amount;
    }
    return false;
  }

  @Override
  public void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building){
//        building.proximity.each(b -> {
//
//        });
  }

  @Override
  public void write(Writes writes){
    writes.s(item.id);
    writes.f(amount);
  }

  @Override
  public ResourceStack<Power> read(Reads reads){
    return ResourceStackManager.getResourceInstance(powers.get(reads.s()), reads.f());
  }
}
