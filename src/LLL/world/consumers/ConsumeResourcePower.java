package LLL.world.consumers;

import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class ConsumeResourcePower extends ConsumePower{
  public ConsumeResourcePower(float usage, float capacity){
    this.usage = usage;//使用功率
    this.capacity = capacity;//容量
  }

  @Override
  public void display(Stats stats){
    if(capacity > 0){
      stats.add(Stat.powerCapacity, capacity, StatUnit.none);
    }
    if(usage > 0){
      stats.add(Stat.powerUse, usage * 60f, StatUnit.powerSecond);
    }
  }
}
