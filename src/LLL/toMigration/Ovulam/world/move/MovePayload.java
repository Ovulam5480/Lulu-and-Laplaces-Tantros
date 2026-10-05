package LLL.toMigration.Ovulam.world.move;

import arc.math.geom.*;
import mindustry.gen.*;
import mindustry.world.*;

//默认载荷2边长
public abstract class MovePayload{
  public abstract int maxCapacity(Block block);

  public abstract Vec2 setTargetPosition(Building build, int index);

}
