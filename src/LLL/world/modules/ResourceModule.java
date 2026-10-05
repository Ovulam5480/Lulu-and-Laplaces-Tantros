package LLL.world.modules;

import LLL.type.resourceStacks.*;
import LLL.world.blocks.module.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.modules.*;

public class ResourceModule extends BlockModule{
  protected Seq[] resourceTypeMap;
  public ObjectMap<Object, ResourceStack<?>> resourceStacks = new ObjectMap<>();

  public ItemModule itemModule;
  public LiquidModule liquidModule;
  public PowerModule powerModule;

  protected final ResourceBuildModule building;

  public ResourceModule(Building building){
    this.building = building instanceof ResourceBuildModule rm ? rm : null;

    resourceTypeMap = new Seq[ResourceStackManager.resourceClasses.size - 2];
    for(int i = 0; i < ResourceStackManager.resourceClasses.size - 2; i++){
      resourceTypeMap[i] = new Seq<>();
    }

    if(building.items != null){
      building.items = itemModule = new ResourceItemModule(this.building);
    }
    if(building.liquids != null){
      building.liquids = liquidModule = new ResourceLiquidModule(this.building);
    }
    if(building.power != null){
      powerModule = building.power = new ResourcePowerModule(this.building);
      powerModule.graph.add(building);
    }
  }

  public void add(Object object, float amount){
    int id = getResourceInstanceID(object);

    switch(id){
      case 0 -> {
        if(itemModule != null) itemModule.add((Item)object, (int)amount);
      }
      case 1 -> {
        if(liquidModule != null) liquidModule.add((Liquid)object, amount);
      }
      default -> {
        ResourceStack<?> rs = resourceStacks.get(object);
        if(rs == null){
          resourceStacks.put(object, ResourceStackManager.getResourceInstanceByType(object, amount));
          getResourceInstanceSeqID(id).add(object);

          if(building != null){
            building.triggerResourceChanged();
          }
        }else{
          rs.amount += amount;
        }
      }
    }
  }

  public void add(ResourceStack<?> resourceStack){
    Object object = resourceStack.item;
    float amount = resourceStack.amount;
    int id = resourceStack.id();

    switch(id){
      case 0 -> {
        if(itemModule != null) itemModule.add((Item)object, (int)amount);
      }
      case 1 -> {
        if(liquidModule != null) liquidModule.add((Liquid)object, amount);
      }
      default -> {
        ResourceStack<?> rs = resourceStacks.get(object);
        if(rs == null){
          resourceStacks.put(object, resourceStack);
          getResourceInstanceSeqID(id).add(object);

          if(building != null){
            building.triggerResourceChanged();
          }
        }else{
          rs.amount += amount;
        }
      }
    }
  }

  public void add(ResourceModule other){
    itemModule.add(other.itemModule);
    other.liquidModule.each((l, a) -> liquidModule.add(l, a));

    for(ResourceStack<?> value : other.resourceStacks.values()){
      add(value);
    }
  }

  public void remove(Object object, float amount){
    int id = getResourceInstanceID(object);

    switch(id){
      case 0 -> {
        itemModule.remove((Item)object, (int)amount);
      }
      case 1 -> {
        liquidModule.remove((Liquid)object, amount);
      }
      default -> {
        ResourceStack<?> rs = resourceStacks.get(object);
        if(rs != null){
          rs.amount -= amount;
          if(rs.amount <= 0){
            resourceStacks.remove(object);
            getResourceInstanceSeqID(id).remove(object);

            if(building != null){
              building.triggerResourceChanged();
            }
          }
        }
      }
    }
  }

  public boolean has(Object object){
    int id = getResourceInstanceID(object);

    switch(id){
      case 0 -> {
        return itemModule.has((Item)object);
      }
      case 1 -> {
        return liquidModule.get((Liquid)object) > 0;
      }
      default -> {
        return resourceStacks.containsKey(object);
      }
    }
  }

  public boolean has(Object object, float amount){
    int id = getResourceInstanceID(object);

    switch(id){
      case 0 -> {
        return itemModule.has((Item)object, (int)amount);
      }
      case 1 -> {
        return liquidModule.get((Liquid)object) >= amount;
      }
      default -> {
        return resourceStacks.containsKey(object);
      }
    }
  }

  public boolean has(ResourceStack<?> resourceStack){
    Object object = resourceStack.item;
    float amount = resourceStack.amount;
    int id = getResourceInstanceID(object);

    switch(id){
      case 0 -> {
        return itemModule.has((Item)object, (int)amount);
      }
      case 1 -> {
        return liquidModule.get((Liquid)object) >= amount;
      }
      default -> {
        return resourceStacks.containsKey(object) && resourceStacks.get(object).amount >= amount;
      }
    }
  }

  public boolean has(ResourceStack<?>[] stackArray){
    for(ResourceStack<?> resourceStack : stackArray){
      Object object = resourceStack.item;
      float amount = resourceStack.amount;
      int id = getResourceInstanceID(object);

      switch(id){
        case 0 -> {
          if(!itemModule.has((Item)object, (int)amount)) return false;
        }
        case 1 -> {
          if(liquidModule.get((Liquid)object) < amount) return false;
        }
        default -> {
          if(!resourceStacks.containsKey(object) && resourceStacks.get(object).amount < amount) return false;
        }
      }
    }
    return true;
  }

  public boolean has(Seq<ResourceStack<?>> stackSeq){
    for(ResourceStack<?> resourceStack : stackSeq){
      Object object = resourceStack.item;
      float amount = resourceStack.amount;
      int id = getResourceInstanceID(object);

      switch(id){
        case 0 -> {
          if(!itemModule.has((Item)object, (int)amount)) return false;
        }
        case 1 -> {
          if(liquidModule.get((Liquid)object) < amount) return false;
        }
        default -> {
          if(!resourceStacks.containsKey(object) && resourceStacks.get(object).amount < amount) return false;
        }
      }
    }
    return true;
  }

  public void remove(ResourceStack<?> resourceStack){
    Object object = resourceStack.item;
    float amount = resourceStack.amount;
    int id = resourceStack.id();

    switch(id){
      case 0 -> itemModule.remove((Item)object, (int)amount);
      case 1 -> liquidModule.remove((Liquid)object, amount);
      default -> {
        //todo remove(resourceStack)?
        ResourceStack<?> rs = resourceStacks.get(object);
        if(rs != null){
          rs.amount -= amount;
          if(rs.amount <= 0){
            resourceStacks.remove(object);
            getResourceInstanceSeqID(id).remove(object);

            if(building != null){
              building.triggerResourceChanged();
            }
          }
        }
      }
    }
  }

  public float sum(){
    float sum = (itemModule == null ? 0 : itemModule.sum((item, amount) -> amount))
      + (liquidModule == null ? 0 : liquidModule.sum((liquid, amount) -> amount));
    for(ResourceStack<?> rs : resourceStacks.values()){
      sum += rs.amount;
    }
    return sum;
  }

  public float sum(ResourceCalculator calculator){
    float sum = (itemModule == null ? 0 : itemModule.sum(calculator::get))
      + (liquidModule == null ? 0 : liquidModule.sum(calculator::get));
    for(ResourceStack<?> rs : resourceStacks.values()){
      sum += calculator.get(rs.item, rs.amount);
    }
    return sum;
  }

  public Seq<Object> getResourceInstanceSeqID(int id){
    return resourceTypeMap[id - 2];
  }

  public Seq<Object> getResourceInstanceSeq(Object object){
    return resourceTypeMap[getResourceInstanceID(object) - 2];
  }

  public int getResourceInstanceID(Object object){
    return ResourceStackManager.getResourceInstanceID(object);
  }

  @Override
  public void write(Writes write){
    ResourceStackManager.writeResourceStacks(write, resourceStacks.values().toSeq());
  }

  @Override
  public void read(Reads read, boolean legacy){
    for(ResourceStack<?> stack : ResourceStackManager.readResourceStacks(read)){
      resourceStacks.put(stack.item, stack);

      getResourceInstanceSeq(stack.item).add(stack.item);
    }
  }

  public interface ResourceCalculator{
    float get(Object item, float amount);
  }
}
