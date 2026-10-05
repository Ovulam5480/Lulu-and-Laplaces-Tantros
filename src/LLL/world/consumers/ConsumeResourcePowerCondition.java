package LLL.world.consumers;

import arc.func.*;
import mindustry.gen.*;

public class ConsumeResourcePowerCondition extends ConsumeResourcePower{
  private final Boolf<Building> consume;

  public ConsumeResourcePowerCondition(float usage, float capacity, Boolf<Building> consume){
    super(usage, capacity);
    this.consume = consume;
  }

  @Override
  public float requestedPower(Building entity){
    return consume.get(entity) ? usage : 0f;
  }
}
