package LLL.world.neoplasm;

import LLL.entities.gen.*;
import LLL.world.components.blockComps.*;
import LLL.world.components.buildComps.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmOrgan extends Block implements NeoplasmOrganBlockComp<ProducerBuildBuilding>{
  public NeoplasmOrgan(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmOrganBuild extends ProducerBuildBuilding implements NeoplasmOrganComp<ProducerBuildBuilding>{

  }
}
