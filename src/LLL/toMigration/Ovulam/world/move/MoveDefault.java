package LLL.toMigration.Ovulam.world.move;

import arc.math.geom.*;
import mindustry.gen.*;
import mindustry.world.*;

//todo 存在BUG, 暂时不清楚什么原因导致的
public class MoveDefault extends MovePayload{
  public MoveDefault(){

  }

  @Override
  public int maxCapacity(Block block){
    return 9999;
  }

  @Override
  public Vec2 setTargetPosition(Building build, int index){
    return Vec2.ZERO;
  }

}
