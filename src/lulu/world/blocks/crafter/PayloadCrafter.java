package lulu.world.blocks.crafter;

import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
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