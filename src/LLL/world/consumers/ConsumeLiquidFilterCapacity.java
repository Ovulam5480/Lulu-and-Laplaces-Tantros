package LLL.world.consumers;

import arc.func.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.consumers.*;

public class ConsumeLiquidFilterCapacity extends ConsumeLiquidFilter{
  public ConsumeLiquidFilterCapacity(Boolf<Liquid> liquid, float amount){
    super(liquid, amount);
  }

  @Override
  public float efficiency(Building build){
    var liq = getConsumed(build);
    if(build.edelta() <= 0.00000001f) return 0f;
    return liq != null ? Math.min(build.liquids.get(liq) / build.block.liquidCapacity, 1f) : 0f;
  }
}
