package LLL.entities.comp.unitcomps;

import LLL.world.blocks.*;
import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class FrameBuilderComp implements Builderc{
  @Annotations.Import
  float x, y, rotation, buildSpeedMultiplier;
  @Annotations.Import
  UnitType type;
  @Annotations.Import
  Team team;
  @Annotations.Import
  float buildCounter;
  @Annotations.Import
  BuildPlan lastActive;
  @Annotations.Import
  int lastSize;
  @Annotations.Import
  float buildAlpha;
  @Annotations.Import
  boolean updateBuilding;
  @Annotations.Import
  Queue<BuildPlan> plans;

  @Annotations.Replace
  @Override
  public void updateBuildLogic(){
    if(type.buildSpeed <= 0f) return;

    if(!headless){
      //visual activity update
      if(lastActive != null && buildAlpha <= 0.01f){
        lastActive = null;
      }

      buildAlpha = Mathf.lerpDelta(buildAlpha, activelyBuilding() ? 1f : 0f, 0.15f);
    }

    validatePlans();

    if(!updateBuilding || !canBuild()){
      return;
    }

    float finalPlaceDst = state.rules.infiniteResources ? Float.MAX_VALUE : type.buildRange;
    boolean infinite = state.rules.infiniteResources || team().rules().infiniteResources;

    buildCounter += Time.delta;
    if(Float.isNaN(buildCounter) || Float.isInfinite(buildCounter)) buildCounter = 0f;
    buildCounter = Math.min(buildCounter, 10f);

    boolean instant = state.rules.instantBuild && state.rules.infiniteResources;

    //random attempt to fix a freeze that only occurs on Android
    int maxPerFrame = instant ? plans.size : 10, count = 0;

    var core = core();

    if((core == null && !infinite)) return;

    while((buildCounter >= 1 || instant) && count++ < maxPerFrame && plans.size > 0){
      buildCounter -= 1f;

      //find the next build plan
      if(plans.size > 1){
        int total = 0;
        int size = plans.size;
        float bestDst = Float.MAX_VALUE;
        boolean foundAny = false;
        int bestIndex = -1;
        while(total < size){
          var plan = buildPlan();

          float dst = plan.dst2(this);
          boolean within = dst <= finalPlaceDst * finalPlaceDst;
          //if it's a valid plan within range, break out of the loop
          if(within && !shouldSkip(plan, core)){
            foundAny = true;
            break;
          }else if(within && dst < bestDst){ //it's still bad, but at least it's within build radius
            bestIndex = total;
            bestDst = dst;
          }

          plans.removeFirst();
          plans.addLast(plan);
          total++;
        }

        //all the plans were useless, and the current one can't be reached. skip to the closest one, if applicable
        if(!foundAny && bestIndex > 0 && !within(buildPlan(), finalPlaceDst)){
          //this is slow, but should be rare in practice
          for(int i = 0; i < bestIndex; i++){
            plans.addLast(plans.removeFirst());
          }
        }
      }

      BuildPlan current = buildPlan();
      Tile tile = current.tile();

      lastActive = current;
      buildAlpha = 1f;
      if(current.breaking) lastSize = tile.block().size;

      if(!within(tile, finalPlaceDst)) continue;

      if(!headless){
        Vars.control.sound.loop(Sounds.loopBuild, tile, 1.3f);
      }

      boolean allowBuildCurrent = current.block != null && (state.isEditor() || (state.rules.waves && team == state.rules.waveTeam && current.block.isVisible()) || (current.block.unlockedNowHost() && current.block.environmentBuildable() && current.block.isPlaceable()));

      if(!(tile.build instanceof ConstructBlock.ConstructBuild cb && !(plans.first().block instanceof FrameBlock))){
        if(!current.initialized && !current.breaking && Build.validPlaceIgnoreUnits(current.block, team, current.x, current.y, current.rotation, true, true) && allowBuildCurrent){
          if(Build.checkNoUnitOverlap(current.block, current.x, current.y)){
            boolean hasAll = infinite || current.isRotation(team) ||
              //derelict repair
              (tile.team() == Team.derelict && tile.block() == current.block && tile.build != null && tile.block().allowDerelictRepair && state.rules.derelictRepair) ||
              //make sure there's at least 1 item of each type first
              !Structs.contains(current.block.requirements, i -> !core.items.has(i.item, Math.min(Mathf.round(i.amount * state.rules.buildCostMultiplier), 1)));

            if(hasAll){
              Call.beginPlace(self(), current.block, team, current.x, current.y, current.rotation, current.block.instantBuild ? current.config : null);

              if(!net.client() && current.block.instantBuild){
                if(plans.size > 0){
                  plans.removeFirst();
                }
                continue;
              }
            }else{
              current.stuck = true;
            }
          }else{
            //there's a unit blocking the plan, skip it
            plans.removeFirst();
            plans.addLast(current);
            continue;
          }
        }else if(!current.initialized && current.breaking && Build.validBreak(team, current.x, current.y)){
          Call.beginBreak(self(), team, current.x, current.y);
        }else{
          plans.removeFirst();
          continue;
        }
      }else if((tile.team() != team && tile.team() != Team.derelict) || (!current.breaking && (cb.current != current.block || cb.tile != current.tile()))){
        plans.removeFirst();
        continue;
      }

      if(tile.build instanceof ConstructBlock.ConstructBuild && !current.initialized){
        Events.fire(new EventType.BuildSelectEvent(tile, team, self(), current.breaking));
        current.initialized = true;
      }

      //if there is no core to build with or no build entity, stop building!
      if(!(tile.build instanceof ConstructBlock.ConstructBuild entity)){
        continue;
      }

      float bs = 1f / entity.buildCost * type.buildSpeed * buildSpeedMultiplier * state.rules.buildSpeed(team);

      //otherwise, update it.
      if(current.breaking){
        entity.deconstruct(self(), core, bs);
      }else if(allowBuildCurrent){ //only allow building unlocked blocks
        entity.construct(self(), core, bs, current.config);
      }

      current.stuck = Mathf.equal(current.progress, entity.progress);
      current.progress = entity.progress;
    }
  }
}
