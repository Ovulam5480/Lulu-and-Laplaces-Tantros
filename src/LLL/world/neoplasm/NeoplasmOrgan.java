package LLL.world.neoplasm;

import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
import LLL.world.buildComp.module.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmOrgan extends Block implements NeoplasmBlockComp, ProducerBlockComp<ProducerBuildBuilding>{

  public NeoplasmOrgan(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmOrganBuild extends ProducerBuildBuilding implements NeoplasmOrganComp<ProducerBuildBuilding>{

  }
}
