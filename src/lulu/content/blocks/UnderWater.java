package lulu.content.blocks;

import lulu.content.*;
import lulu.content.extensions.*;
import lulu.toMigration.Ovulam.world.consumers.*;
import lulu.world.blocks.crafter.*;
import lulu.world.blocks.packet.*;
import lulu.world.blocks.production.*;
import lulu.world.recipe.consumers.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

public class UnderWater{
  public static void load(){
    OvulamBlocks.undertowDrill = new Drill("undertow-drill"){{
      size = 2;
      requirements(Category.production, ItemStack.with(OvulamItems.ferroManganese, 10));
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 10);

      drillTime = 400;

      liquidCapacity = 30f;
      consume(new ConsumeLiquidFilterCapacity(l -> l.gas, 0f).boost());
    }};

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
  }
}
