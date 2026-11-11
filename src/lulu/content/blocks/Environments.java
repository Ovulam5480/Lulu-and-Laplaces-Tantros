package lulu.content.blocks;

import lulu.content.*;
import lulu.ctype.*;
import mindustry.content.*;
import mindustry.world.blocks.environment.*;

public class Environments{
  public static void load(){
    OvulamBlocks.hydrothermalVent = new SteamVent("hydrothermal-vent"){{
      parent = Blocks.ferricStone;
      size = 2;
      attributes.set(OvulamAttributes.hydrothermal, 1);
    }};

    OvulamBlocks.gasHydrateFloor = new Floor("gas-hydrate-floor", 0){{
      attributes.set(OvulamAttributes.gasHydrate, 1);

      itemDrop = OvulamItems.gasHydrate;
    }};
  }
}
