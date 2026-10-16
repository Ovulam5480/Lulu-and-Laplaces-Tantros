package lulu.world.blocks.crafter;

import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class FloorCrafter extends Block implements FloorCrafterBlockComp<FloorCrafterBuildBuilding>{
  public FloorCrafter(String name){
    super(name);
  }

  public class FloorCrafterBuild extends FloorCrafterBuildBuilding{

  }
}
