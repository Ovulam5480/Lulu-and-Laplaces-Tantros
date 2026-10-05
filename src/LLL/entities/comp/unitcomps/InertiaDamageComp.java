package LLL.entities.comp.unitcomps;

import LLL.entities.*;
import arc.math.geom.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;

@Annotations.EntityComponent
abstract class InertiaDamageComp implements BlockUnitc{
  boolean canDrop = true;

  public Vec2 lastVel = new Vec2();
  @Annotations.Import
  Vec2 vel;

  @Annotations.BreakAll
  @Annotations.MethodPriority(-2)
  @Override
  public void update(){
    if(tile().dead){
      remove();
      return;
    }

    if(lastVel.isZero()){
      lastVel.set(vel);
    }

    if(!isFlying()){
      float deltaSpeed = Tmp.v1.set(vel).sub(lastVel).len();

      damage(deltaSpeed * deltaSpeed * Momentum.kineticDamageMulti, false);
      //Log.info(deltaSpeed * deltaSpeed * Momentum.kineticDamageMulti);
      lastVel.set(vel);
    }

    if(canDrop && vel.isZero(0.01f)){
      Building building = tile();
      Block block = building.block;

      Tile tile = Vars.world.tileWorld(x() - block.offset, y() - block.offset);

      if(tile != null && Build.validPlaceIgnoreUnits(block, team(), tile.x, tile.y, building.rotation, false, false)){
        tile.setBlock(tile().block, tile().team, tile().rotation, this::tile);
        remove();
        return;
      }
    }
  }

  @Annotations.Replace(1)
  @Override
  public EntityCollisions.SolidPred solidity(){
    return isFlying() ? null : EntityCollisions::solid;
  }
}
