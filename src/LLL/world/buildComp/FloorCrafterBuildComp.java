package LLL.world.buildComp;

import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

@Annotations.EntityComponent
abstract class FloorCrafterBuildComp implements ProducerBuildc{
  ObjectIntMap<Floor> floorCount = new ObjectIntMap<>();

  @Annotations.Import
  Block block;
  @Annotations.Import
  Tile tile;

  static ObjectIntMap<Floor> count = new ObjectIntMap<>();

  public void onProximityUpdate(){
    floorCount.clear();
    for(ObjectIntMap.Entry<Floor> floor : FloorCrafterBlockComp.getFloors(tile, block)){
      floorCount.put(floor.key, floor.value);
    }
  }
}
