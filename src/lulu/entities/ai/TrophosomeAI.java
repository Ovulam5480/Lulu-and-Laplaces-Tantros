package lulu.entities.ai;

import arc.util.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.components.buildComps.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class TrophosomeAI extends AIController{
  TrophosomeTargetBuildComp target;
  int lastPriority = -999;

  public void set(TrophosomeTargetBuildComp target, int priority){
    this.target = target;
    lastPriority = priority;
  }

  @Override
  public void updateMovement(){
    Building building = target.getBuilding();
    if(target == null || !building.isAdded()){
      lastPriority = -999;
      findTarget();
    }else if(unit.within(target, target.getBlock().size * 3f) && target.<PayloadBuildc>getBuild().acceptUnitPayload(unit)){
      building.handleUnitPayload(unit, p -> {
        building.handlePayload(building, p);
        target.removeTrophosomeRequire(unit);
      });
    }else{
      if(unit.isFlying()){
        moveTo(target, 0, 0);
      }else{
        ControlPathfinder.PathfindResult result = controlPath.getPathPosition(unit, Tmp.v1.set(target));

        if(result.move){
          moveTo(result.dest, 0, 0);
        }else{
          findTarget();
        }
      }
    }
  }

  public void findTarget(){
    for(Building building : Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.trophosomeTarget)){
      if(building instanceof TrophosomeTargetBuildComp build
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
      if(building instanceof TrophosomeTargetBuildComp build
        && build.acceptTrophosome(unit.type)){
        return true;
      }
    }

    return false;
  }
}
