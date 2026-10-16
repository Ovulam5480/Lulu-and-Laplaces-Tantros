package lulu.entities.ai;

import lulu.entities.gen.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class FlyingBuildUnitAI extends CommandAI{
  @Override
  public void commandPosition(Vec2 pos, boolean stopWhenInRange){
    float offset = ((MovableBuildUnitc)unit).tile().block.offset;
    pos.set(World.toTile(pos.x - offset) * 8, World.toTile(pos.y - offset) * 8).add(offset, offset);

    super.commandPosition(pos, stopWhenInRange);
  }

  @Override
  public void defaultBehavior(){
    if(targetPos == null){
      targetPos = new Vec2(unit.x, unit.y);
    }

    boolean alwaysArrive = false;

    float engageRange = unit.type.range - 10f;

    boolean move = true, isFinalPoint = commandQueue.size == 0;
    moveTarget.set(targetPos);
    offsetedDestination.set(targetPos);

    //the enter payload command requires an exact position
    if(group != null && group.valid && groupIndex < group.units.size && command != UnitCommand.enterPayloadCommand){
      offsetedDestination.add(group.positions[groupIndex * 2], group.positions[groupIndex * 2 + 1]);
    }

    if(unit.isGrounded()){
      //TODO: blocking enable or disable?
      if(timer.get(timerTarget3, avoidInterval)){
        Vec2 dstPos = Tmp.v1.trns(unit.rotation, unit.hitSize / 2f);
        float max = unit.hitSize / 2f;
        float radius = Math.max(7f, max);
        float margin = 4f;
        blockingUnit = Units.nearbyCheck(unit.x + dstPos.x - radius / 2f, unit.y + dstPos.y - radius / 2f, radius, radius,
          u -> u != unit && u.within(unit, u.hitSize / 2f + unit.hitSize / 2f + margin) && u.controller() instanceof CommandAI ai && ai.targetPos != null &&
            //stop for other unit only if it's closer to the target
            (ai.targetPos.equals(targetPos) && u.dst2(targetPos) < unit.dst2(targetPos)) &&
            //don't stop if they're facing the same way
            !Angles.within(unit.rotation, u.rotation, 15f) &&
            //must be near an obstacle, stopping in open ground is pointless
            ControlPathfinder.isNearObstacle(unit, unit.tileX(), unit.tileY(), u.tileX(), u.tileY()));
      }

      float maxBlockTime = 60f * 5f;

      if(blockingUnit){
        timeSpentBlocked += Time.delta;

        if(timeSpentBlocked >= maxBlockTime * 2f){
          timeSpentBlocked = 0f;
        }
      }else{
        timeSpentBlocked = 0f;
      }

      move = //controlPath.getPathPosition(unit, offsetedDestination, targetPos, moveTarget, noFound) &&
        (!blockingUnit || timeSpentBlocked > maxBlockTime);

      alwaysArrive = moveTarget.epsilonEquals(unit.tileX() * tilesize, unit.tileY() * tilesize);
      //we've reached the final point if the returned coordinate is equal to the supplied input
      isFinalPoint &= offsetedDestination.epsilonEquals(moveTarget, 4.1f);

      //if the path is invalid, stop trying and record the end as unreachable
      if(unit.team.isAI() && unit.isPathImpassable(World.toTile(offsetedDestination.x), World.toTile(offsetedDestination.y))){
        if(attackTarget instanceof Building build){
          unreachableBuildings.addUnique(build.pos());
        }
        attackTarget = null;
        ((MovableBuildUnitc)unit).tile().set(targetPos);//todo ?
        return;
      }
    }else{
      moveTarget.set(offsetedDestination);
    }

    if(unit.type.canBoost){
      unit.updateBoosting(!unit.within(offsetedDestination, 1f));
      move &= unit.elevation >= 1;
    }

    if(move){
      moveTo(moveTarget,
        unit.isGrounded() ? 0f :
          attackTarget != null ? engageRange : 0f,
        unit.isFlying() ? 40f : 100f, false, null, isFinalPoint || alwaysArrive);
    }

    if(unit.within(offsetedDestination, 1f)){
      if(commandQueue.isEmpty()){
        if(unit.elevation <= 0.01f) ((MovableBuildUnitc)unit).tile().set(targetPos);
      }else{
        Vec2 prev = targetPos;
        var next = commandQueue.remove(0);
        if(next instanceof Teamc target){
          commandTarget(target, this.stopAtTarget);
        }else if(next instanceof Vec2 position){
          commandPosition(position);
        }

        if(prev != null && (hasStance(UnitStance.patrol) || command == UnitCommand.loopPayloadCommand)){
          commandQueue.add(prev.cpy());
        }

        //make sure spot in formation is reachable
        if(group != null){
          group.updateRaycast(groupIndex, next instanceof Vec2 position ? position : Tmp.v3.set(next));
        }
      }
    }
  }

  @Override
  public void updateUnit(){
    if(command == UnitCommand.mineCommand && !hasStance(UnitStance.mineAuto) && !ItemUnitStance.all().contains(this::hasStance)){
      setStance(UnitStance.mineAuto);
    }

    //pursue the target if relevant
    if(hasStance(UnitStance.pursueTarget) && !hasStance(UnitStance.patrol) && target != null && attackTarget == null && targetPos == null){
      commandTarget(target, false);
    }

    //pursue the target for patrol, keeping the current position
    if(hasStance(UnitStance.patrol) && hasStance(UnitStance.pursueTarget) && target != null && attackTarget == null){
      //commanding a target overwrites targetPos, so add it to the queue
      if(targetPos != null){
        commandQueue.add(targetPos.cpy());
      }
      commandTarget(target, false);
    }

    //remove invalid targets
    if(commandQueue.any()){
      commandQueue.removeAll(e -> e instanceof Healthc h && !h.isValid());
    }

    //assign defaults
    if(command == null && unit.type.commands.size > 0){
      command = unit.type.defaultCommand == null ? unit.type.commands.first() : unit.type.defaultCommand;
    }

    //update command controller based on index.
    var curCommand = command;
    if(lastCommand != curCommand){
      lastCommand = curCommand;
      commandController = (curCommand == null ? null : curCommand.controller.get(unit));
    }

    //use the command controller if it is provided, and bail out.
    if(commandController != null){
      if(commandController.unit() != unit) commandController.unit(unit);
      commandController.updateUnit();
    }else{
      defaultBehavior();
      //boosting control is not supported, so just don't.
      //unit.updateBoosting(false);
    }
  }
}
