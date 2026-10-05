package LLL.lib.singularity.world.components;

import arc.struct.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

public interface FloorCrafterBuildComp extends BuildCompBase{
  ObjectIntMap<Floor> count = new ObjectIntMap<>();

  static ObjectIntMap<Floor> getFloors(Tile tile, Block block){
    count.clear();
    tile.getLinkedTilesAs(block, t -> {
      Floor f;
      if((f = t.floor()) != null){
        count.increment(f, 0, 1);
      }
    });
    return count;
  }

  @Annotations.BindField(value = "floorCount", initialize = "new arc.struct.ObjectIntMap<>()")
  default ObjectIntMap<Floor> floorCount(){
    return null;
  }

  @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  default void updateFloors(){
    floorCount().clear();
    for(ObjectIntMap.Entry<Floor> floor : getFloors(getTile(), getBlock())){
      floorCount().put(floor.key, floor.value);
    }
  }
}
