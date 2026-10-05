package LLL.world.modules;

import LLL.world.blocks.module.*;
import mindustry.type.*;
import mindustry.world.modules.*;

public class ResourceLiquidModule extends LiquidModule{
  public ResourceBuildModule building;

  public ResourceLiquidModule(ResourceBuildModule building){
    this.building = building;
  }

  public void reset(Liquid liquid, float amount){
    super.reset(liquid, amount);
    triggerChanged();
  }

  public void set(Liquid liquid, float amount){
    super.set(liquid, amount);
    triggerChanged();
  }

  public void clear(){
    super.clear();
    triggerChanged();
  }

  public void add(Liquid liquid, float amount){
    boolean empty = get(liquid) == 0;
    super.add(liquid, amount);
    if(empty) triggerChanged();
  }

  public void remove(Liquid liquid, float amount){
    super.remove(liquid, amount);
    if(get(liquid) == 0) triggerChanged();
  }

  public void triggerChanged(){
    building.triggerResourceChanged();
  }
}
