package LLL.content.blocks;

import LLL.content.*;
import LLL.world.blocks.distribution.*;
import mindustry.type.*;

public class Distribution{
  public static void load(){

    OvulamBlocks.packetConveyor = new PacketConveyor("packet-conveyor"){{
      size = 2;
      requirements = ItemStack.with(OvulamItems.ferroManganese, 2);
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 10);
    }};

    OvulamBlocks.packetConveyorRouter = new PacketConveyorRouter("packet-conveyor-router"){{
      size = 2;
    }};

    OvulamBlocks.packetConveyorSorter = new PacketConveyorSorter("packet-conveyor-sorter"){{
      size = 2;
      requirements = ItemStack.with(OvulamItems.ferroManganese, 10, OvulamItems.amethyst, 10);
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 25, OvulamItems.amethyst, 25);
    }};

    OvulamBlocks.packetConveyorDiverter = new PacketConveyorDiverter("packet-conveyor-diverter"){{
      size = 2;
      requirements = ItemStack.with(OvulamItems.ferroManganese, 10, OvulamItems.amethyst, 10);
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 25, OvulamItems.amethyst, 25);
    }};

    OvulamBlocks.packetCompositeConveyor = new PacketCompositeConveyor("packet-composite-conveyor"){{
      size = 4;
    }};
  }
}
