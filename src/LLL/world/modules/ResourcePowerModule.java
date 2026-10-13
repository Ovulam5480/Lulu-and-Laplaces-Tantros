package LLL.world.modules;

import LLL.world.*;
import LLL.world.buildComp.module.*;
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
