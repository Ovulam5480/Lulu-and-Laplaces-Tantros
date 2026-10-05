package LLL.world.blocks.module;

import arc.math.geom.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.world.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

@SuppressWarnings("unused")
public interface MovableBuildModule extends BuildCompBase{
  @Annotations.BindField("unitSelf")
  default Unit unitSelf(){
    return null;
  }

  @Annotations.BindField("unitSelf")
  default void unitSelf(Unit unit){
  }


  @Annotations.MethodEntry(entryMethod = "onCommand", paramTypes = {"arc.math.geom.Vec2 -> target"})
  default boolean onCommandModule(Vec2 target){
    Tile tile = getTile();
    tile.remove();
    boolean canPlace = Vars.control.input.validPlace(World.toTile(target.x), World.toTile(target.y), building().block, building().rotation, null, false);

    if(canPlace){
      Unit unit = createBlockUnit();

      Call.commandUnits(Vars.player, new int[]{unit.id}, null, null, target, false, false);

      //todo 产生一个占位用的临时性方块
      //todo 临时性方块可能在PVP中影响其他队伍?
    }else{
      tile.setBlock(getBlock(), getBuilding().team, getBuilding().rotation, this::getBuilding);
    }

    return canPlace;
  }

  default Unit createBlockUnit(){
    BlockUnitc unit = (BlockUnitc)(getBlock(MovableBlockModule.class).movableBlockType().create(building().team));
    unit.tile(building());
    unit.add();
    unit.elevation(0.02f);

    return (Unit)unit;
  }

  default Building building(){
    return (Building)this;
  }
}
