package LLL.type.resourceStacks;

import LLL.world.blocks.packet.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.*;

public class LiquidResourceStack extends ResourceStack<Liquid>{
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
    return 1;
  }

  @Override
  public void register(){
    ResourceStackManager.register(Liquid.class, LiquidResourceStack.class, LiquidResourceStack::new, new LiquidResourceStack(), id());
  }

  @Override
  public void init(){
    ResourceStackManager.init(Liquid.class, Vars.content.liquids());
  }

  @Override
  public void applyBlock(Block block){
    block.hasLiquids = true;
  }

  @Override
  public Liquid findAvailableResource(Entityc entityc, float amount){
    if(entityc instanceof Building b){
      for(Liquid liquid : Vars.content.liquids()){
        if(b.liquids.get(liquid) >= amount){
          return liquid;
        }
      }
    }
    return null;
  }

  @Override
  public void applyPack(Entityc entityc, Object object, float amount){
    if(entityc instanceof Building b && b.block.hasLiquids){
      b.liquids.remove(item, amount);
    }
  }

  @Override
  public boolean unpack(Entityc entityc, float amount){
    float had = Math.min(amount, this.amount);

    float moved;
    if(entityc instanceof Building b && b.block.hasLiquids){
      moved = Math.min(b.block.liquidCapacity - b.liquids.get(item), had);
      b.liquids.add(item, moved);
    }else return false;

    this.amount -= moved;
    return this.amount <= moved;
  }

  @Override
  public boolean canUnpack(Entityc entityc){
    if(entityc instanceof Building b && b.block.hasLiquids){
      return b.block.liquidCapacity - b.liquids.get(item) > 0;
    }else return false;
  }

  @Override
  public void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building){
    building.dumpLiquid(building.liquids.current());
  }

  @Override
  public void write(Writes writes){
    TypeIO.writeLiquid(writes, item);
    writes.f(amount);
  }

  @Override
  public ResourceStack<Liquid> read(Reads reads){
    return ResourceStackManager.getResourceInstance(TypeIO.readLiquid(reads), reads.f());
  }

}
