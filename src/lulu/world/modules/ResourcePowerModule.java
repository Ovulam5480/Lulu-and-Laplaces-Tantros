package lulu.world.modules;

import lulu.world.*;
import lulu.world.components.buildComps.*;
import mindustry.world.modules.*;

public class ResourcePowerModule extends PowerModule{
  public ResourceBuildComp building;

  {
    graph = new OvulamPowerGraph();
  }

  public ResourcePowerModule(ResourceBuildComp building){
    this.building = building;
  }
}
