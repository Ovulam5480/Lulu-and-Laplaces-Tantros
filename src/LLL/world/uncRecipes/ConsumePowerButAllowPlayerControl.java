package LLL.world.uncRecipes;

import mindustry.gen.*;
import mindustry.world.blocks.*;
import universecore.components.blockcomp.*;
import universecore.world.consumers.*;

public class ConsumePowerButAllowPlayerControl<T extends Building & ConsumerBuildComp & ControlBlock> extends ConsumePower<T>{
  public ConsumePowerButAllowPlayerControl(float usage, float capacity){
    super(usage, capacity);
  }

  @Override
  public float efficiency(T entity){
    if(entity.isControlled()) return 1;
    return super.efficiency(entity);
  }
}
