package LLL.entities.ai;

import arc.func.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class ConditionalPathGroundAI extends GroundAI{
  public Func2<Unit, Vec2, Vec2> target;
  public Boolf<Unit> condition;

  public ConditionalPathGroundAI(Func2<Unit, Vec2, Vec2> target, Boolf<Unit> condition){
    this.target = target;
    this.condition = condition;
  }

  @Override
  public void updateMovement(){
    if(!condition.get(unit)){
      if(controlPath.getPathPosition(unit, target.get(unit, Tmp.v1), Tmp.v1, Tmp.v2, null)){
        moveTo(Tmp.v2, 4, 0);
      }
    }else super.updateMovement();
  }
}
