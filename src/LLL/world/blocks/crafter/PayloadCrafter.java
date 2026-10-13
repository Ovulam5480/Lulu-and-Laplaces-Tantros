package LLL.world.blocks.crafter;

import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class PayloadCrafter extends Block implements PayloadCrafterBlockComp<PayloadCrafterBuildBuilding>{
  public PayloadCrafter(String name){
    super(name);
  }

  public class PayloadCrafterBuild extends PayloadCrafterBuildBuilding{

  }
}