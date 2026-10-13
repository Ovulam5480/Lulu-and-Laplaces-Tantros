package LLL.world.blocks.crafter;

import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
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
