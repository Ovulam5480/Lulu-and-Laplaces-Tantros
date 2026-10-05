package LLL.world.modules;

import LLL.world.*;
import LLL.world.blocks.module.*;
import mindustry.world.modules.*;

public class ResourcePowerModule extends PowerModule{
  public ResourceBuildModule building;

  {
    graph = new OvulamPowerGraph();
  }

  public ResourcePowerModule(ResourceBuildModule building){
    this.building = building;
  }
}
