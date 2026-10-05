package LLL.toMigration.Ovulam.world.move;

import arc.math.geom.*;
import mindustry.gen.*;
import mindustry.world.*;

public class Moved8 extends MovePayload{
  public float distance;

  public Moved8(){
    this(16);
  }

  public Moved8(float distance){
    this.distance = distance;
  }

  @Override
  public int maxCapacity(Block block){
    return 8;
  }

  @Override
  public Vec2 setTargetPosition(Building build, int index){
    return new Vec2(Geometry.d8(index).x * distance, Geometry.d8(index).y * distance);
  }

}
