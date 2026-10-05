package LLL.entities.ai;

import LLL.content.extensions.*;
import LLL.world.blocks.module.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class TrophosomeAI extends AIController{
  TrophosomeTargetBuildModule target;
  int lastPriority = -999;

  public void set(TrophosomeTargetBuildModule target, int priority){
    this.target = target;
    lastPriority = priority;
  }

  @Override
  public void updateMovement(){
    if(target == null || !target.getBuilding().isAdded()){
      lastPriority = -999;
      findTarget();
    }else if(unit.within(target, target.getBlock().size * 3f) && target.acceptUnitPayload(unit)){
      target.getBuilding().handleUnitPayload(unit, p -> {
        target.handlePayload(target.getBuilding(), p);
        target.removeTrophosomeRequire(unit);
      });
    }else{
      if(unit.isFlying()){
        moveTo(target, 0, 0);
      }else{
        if(controlPath.getPathPosition(unit, Tmp.v1.set(target), Tmp.v1, Tmp.v2, null)){
          moveTo(Tmp.v2, 0, 0);
        }else{
          findTarget();
        }
      }
    }
  }

  public void findTarget(){
    for(Building building : Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.trophosomeTarget)){
      if(building instanceof TrophosomeTargetBuildModule build
        && build.acceptTrophosome(unit.type)
        && build.blockAsTarget().priority() > lastPriority){
        target = build;
        lastPriority = build.blockAsTarget().priority();
      }
    }

    if(target != null){
      target.handleTrophosomeRequire(unit);
    }
  }

  public static boolean hasTarget(Unit unit){
    if(Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.trophosomeTarget) == null) return false;

    for(Building building : Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.trophosomeTarget)){
      if(building instanceof TrophosomeTargetBuildModule build
        && build.acceptTrophosome(unit.type)){
        return true;
      }
    }

    return false;
  }
}
