package lulu.world.neoplasm;

import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
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
