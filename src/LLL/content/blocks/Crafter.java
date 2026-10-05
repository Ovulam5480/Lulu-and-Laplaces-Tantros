package LLL.content.blocks;

import LLL.content.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.consumers.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import universecore.world.consumers.*;
import universecore.world.producers.*;

public class Crafter{
  public static void load(){
    OvulamBlocks.hydrothermalVat = new FloorCrafter("hydrothermal-vat"){{
      size = 3;
      researchCost = requirements = ItemStack.with(OvulamItems.ferroManganese, 20, OvulamItems.amethyst, 20);
      category = Category.crafting;
      buildVisibility = BuildVisibility.shown;

      itemCapacity = 30;

      BaseConsume<FloorCrafterBuild> floor = new SglConsumeFloor<>(LLL.content.extensions.OvulamAttributes.hydrothermal, 1){{
        baseEfficiency = -8;
      }};

      newConsume();
      consume.item(OvulamItems.gasHydrate, 1);
      consume.time(30);
      newProduce();
      produce.liquid(OvulamLiquids.gas, 0.02f);

      newConsume();
      consume.items(ItemStack.with(OvulamItems.manganeseNodule, 3, OvulamItems.gasHydrate, 3));
      consume.time(120);
      newProduce();
      produce.item(OvulamItems.ferroManganese, 1);

      newConsume();
      consume.items(ItemStack.with(OvulamItems.manganeseNodule, 3));
      consume.liquid(OvulamLiquids.gas, 0.01f);
      consume.time(90);
      newProduce();
      produce.item(OvulamItems.ferroManganese, 1);

      newConsume();
      consume.items(ItemStack.with(OvulamItems.cobaltNodule, 8, OvulamItems.gasHydrate, 8));
      consume.time(240);
      newProduce();
      produce.item(OvulamItems.cobalt, 1);

      newConsume();
      consume.items(ItemStack.with(OvulamItems.cobaltNodule, 8));
      consume.liquid(OvulamLiquids.gas, 0.02f);
      consume.time(180);
      newProduce();
      produce.item(OvulamItems.cobalt, 1);

      consumers.each(bcs -> bcs.add(floor));
    }};

    OvulamBlocks.assemblingMachine = new PayloadCrafter("assembling-machine"){{
      size = 2;

      requirements(Category.crafting, ItemStack.with(OvulamItems.cobalt, 15, OvulamItems.amethyst, 15));
      researchCost = ItemStack.with(OvulamItems.cobalt, 30, OvulamItems.amethyst, 30);

      newConsume();
      consume.items(ItemStack.with(OvulamItems.ferroManganese, 10, OvulamItems.amethyst, 5));
      consume.liquid(OvulamLiquids.gas, 0.02f);
      consume.time(300);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.manganeseTorpedo, 1), (b, c) -> true));

      newConsume();
      consume.items(ItemStack.with(OvulamItems.cobalt, 10, OvulamItems.amethyst, 15));
      consume.liquid(OvulamLiquids.gas, 0.04f);
      consume.time(300);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.cobaltTorpedo, 1), (b, c) -> true));
    }};
  }
}
