package LLL.type.resourceStacks;

import LLL.world.blocks.packet.*;
import arc.math.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.*;

public class ItemResourceStack extends ResourceStack<Item>{
  @Override
  public String name(){
    return item.name;
  }

  @Override
  public boolean discrete(){
    return true;
  }

  @Override
  public int id(){
    return 0;
  }

  @Override
  public void register(){
    ResourceStackManager.register(Item.class, ItemResourceStack.class, ItemResourceStack::new, new ItemResourceStack(), id());
  }

  @Override
  public void init(){
    ResourceStackManager.init(Item.class, Vars.content.items());
  }

  @Override
  public void applyBlock(Block block){
    block.hasItems = true;
  }

  @Override
  public Item findAvailableResource(Entityc entityc, float amount){
    int amountInt = Mathf.floor(amount);

    if(entityc instanceof Building b && b.block.hasItems){
      for(Item item : Vars.content.items()){
        if(b.items.has(item, amountInt)){
          return item;
        }
      }
    }else if(entityc instanceof Itemsc i){
      if(i.stack().amount >= amountInt){
        return i.stack().item;
      }
    }
    return null;
  }

  @Override
  public void applyPack(Entityc entityc, Object object, float amount){
    int amountInt = Mathf.floor(amount);

    if(entityc instanceof Building b && b.block.hasItems){
      b.removeStack(item, amountInt);
    }else if(entityc instanceof Itemsc i){
      i.stack().amount = Math.min(0, i.stack().amount - amountInt);
    }
  }

  @Override
  public boolean unpack(Entityc entityc, float amount){
    int had = Math.min((int)amount, (int)this.amount);

    int moved;
    if(entityc instanceof Building b && b.block.hasItems){
      moved = Math.min(b.getMaximumAccepted(item) - (b.block.separateItemCapacity ? b.items.get(item) : b.items.total()), had);
      b.handleStack(item, moved, null);
      b.produced(item, moved);
    }else if(entityc instanceof Itemsc i){
      moved = Math.min(i.maxAccepted(item), had);
      i.addItem(item, moved);
    }else return false;

    this.amount -= moved;
    return this.amount < 1;
  }

  @Override
  public boolean canUnpack(Entityc entityc){
    if(entityc instanceof Building b && b.block.hasItems){
      return b.getMaximumAccepted(item) - (b.block.separateItemCapacity ? b.items.get(item) : b.items.total()) > 0;
    }else if(entityc instanceof Itemsc i){
      return i.maxAccepted(item) > 0;
    }else return false;
  }

  @Override
  public void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building){
    building.dump();
  }

  @Override
  public void write(Writes writes){
    TypeIO.writeItem(writes, item);
    writes.f(amount);
  }

  @Override
  public ResourceStack<Item> read(Reads reads){
    return ResourceStackManager.getResourceInstance(TypeIO.readItem(reads), reads.f());
  }
}
