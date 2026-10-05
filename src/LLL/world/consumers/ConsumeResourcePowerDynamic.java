package LLL.world.consumers;

import arc.func.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public class ConsumeResourcePowerDynamic extends ConsumeResourcePower{
  private final Floatf<Building> usage;
  private float displayedPowerUsage;

  public ConsumeResourcePowerDynamic(Floatf<Building> usage, float capacity){
    super(0, capacity);
    this.usage = usage;
  }

  public ConsumeResourcePowerDynamic(float displayed, Floatf<Building> usage, float capacity){
    super(0, capacity);
    this.displayedPowerUsage = displayed;
    this.usage = usage;
  }

  @Override
  public float requestedPower(Building entity){
    return usage.get(entity);
  }

  @Override
  public void display(Stats stats){
    if(displayedPowerUsage != 0f){
      stats.add(Stat.powerUse, displayedPowerUsage * 60f, StatUnit.powerSecond);
    }
  }
}
