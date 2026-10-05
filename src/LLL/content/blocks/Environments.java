package LLL.content.blocks;

import LLL.content.*;
import LLL.content.extensions.*;
import mindustry.content.*;
import mindustry.world.blocks.environment.*;

public class Environments{
  public static void load(){
    OvulamBlocks.hydrothermalVent = new SteamVent("hydrothermal-vent"){{
      parent = blendGroup = Blocks.bluemat;
      size = 2;
      attributes.set(OvulamAttributes.hydrothermal, 1);
    }};

    OvulamBlocks.gasHydrateFloor = new Floor("gas-hydrate-floor", 0){{
      attributes.set(OvulamAttributes.gasHydrate, 1);
    }};
  }
}
