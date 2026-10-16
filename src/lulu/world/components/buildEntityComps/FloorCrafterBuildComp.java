package lulu.world.components.buildEntityComps;

import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

@Annotations.EntityComponent
abstract class FloorCrafterBuildComp<T extends Building & FloorCrafterBuildc<T>> implements ProducerBuildc<T>{
  public ObjectIntMap<Floor> floorCount = new ObjectIntMap<>();

  @Annotations.Import
  Block block;
  @Annotations.Import
  Tile tile;

  public void onProximityUpdate(){
    floorCount.clear();
    for(ObjectIntMap.Entry<Floor> floor : FloorCrafterBlockComp.getFloors(tile, block)){
      floorCount.put(floor.key, floor.value);
    }
  }
}
