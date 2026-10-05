package LLL.content.blocks;

import LLL.content.*;
import LLL.content.extensions.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.consumers.*;
import LLL.world.blocks.packet.*;
import LLL.world.blocks.production.*;
import LLL.world.consumers.*;
import mindustry.type.*;

public class UnderWater{
  public static void load(){
    OvulamBlocks.packetCollector = new PacketCollector("packet-collector"){{
      category = Category.production;
      consumeItem(OvulamItems.gasHydrate);
      researchCost = requirements = ItemStack.with(OvulamItems.ferroManganese, 20);
      size = 2;
    }};

    OvulamBlocks.lodeExcavator = new LodeExcavator("lode-excavator"){{
      researchCost = requirements = ItemStack.with(OvulamItems.ferroManganese, 30, OvulamItems.amethyst, 30);
      size = 4;

      liquidCapacity = 60f;
      consume(new ConsumeLiquidFilterCapacity(l -> l.gas, 0f).boost());
    }};

    OvulamBlocks.gasHydrateCollector = new FloorCrafter("gas-hydrate-collector"){{
      size = 2;
      requirements(Category.crafting, ItemStack.with(OvulamItems.ferroManganese, 10));
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 10);

      newConsume();
      consume.add(new SglConsumeFloor<FloorCrafterBuild>(OvulamAttributes.gasHydrate, 1f / (size * size)){{
        baseEfficiency = 0;
      }});
      consume.time(180);
      newProduce();
      produce.item(OvulamItems.gasHydrate, 1);
    }};
  }
}
