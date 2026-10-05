package LLL.world.modules;

import LLL.world.blocks.module.*;
import mindustry.type.*;
import mindustry.world.modules.*;

public class ResourceItemModule extends ItemModule{
  public ResourceBuildModule building;

  public ResourceItemModule(ResourceBuildModule building){
    this.building = building;
  }

  @Override
  public void set(ItemModule other){
    super.set(other);
    triggerChanged();
  }

  @Override
  public void set(Item item, int amount){
    super.set(item, amount);
    triggerChanged();
  }

  public void add(ItemModule items){
    items.each(this::add);
  }

  public void add(Item item, int amount){
    boolean has = has(item);
    super.add(item, amount);

    if(!has) triggerChanged();
  }

  @Override
  public void remove(Item item, int amount){
    super.remove(item, amount);

    if(!has(item)) triggerChanged();
  }

  @Override
  public void clear(){
    super.clear();
    triggerChanged();
  }

  public void triggerChanged(){
    building.triggerResourceChanged();
  }
}
