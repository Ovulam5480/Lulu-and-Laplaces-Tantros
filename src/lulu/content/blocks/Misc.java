package lulu.content.blocks;

import lulu.content.*;
import lulu.type.resourceStacks.*;
import lulu.world.blocks.*;
import lulu.world.blocks.effect.*;
import lulu.world.blocks.entity.*;
import lulu.world.blocks.packet.*;
import lulu.world.blocks.special.tjTool.*;
import lulu.world.blocks.storage.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

public class Misc{
  public static void load(){
    OvulamBlocks.blastCylinder = new BlastCylinder("blast-cylinder");
    OvulamBlocks.ferroManganeseRodGroup = new ThrowPacketWall("ferromanganese-rod-group", null){{
      size = 2;
      resources.add(new ItemResourceStack().set(OvulamItems.ferroManganese, 5));
      researchCost = requirements = ItemStack.with(OvulamItems.ferroManganese, 24);
      buildTime = 60 * 8;
    }};

    OvulamBlocks.frameBlock = new FrameBlock("frame-block"){{
      size = 2;

      requirements(Category.crafting, ItemStack.with());
    }};

    OvulamBlocks.amethystCrystalCore = new CrystalCoreBlock("amethyst-crystal-core"){{
      size = 2;

      spawnTime = 600f;

      packetType = OvulamPacketTypes.smallItemPacket;
      resources.add(ResourceStackManager.getResourceInstance(OvulamItems.amethyst, 3));
    }};

    OvulamBlocks.manganeseNoduleProp = new Prop("manganese-nodule-prop"){{
      size = 1;

      instantDeconstruct = false;

      category = Category.effect;
      buildVisibility = BuildVisibility.sandboxOnly;

      requirements = ItemStack.with(OvulamItems.manganeseNodule, 16);
      buildTime = 60f;
    }};

    OvulamBlocks.coreCausality = new CoreCausality("core-causality");
    OvulamBlocks.coreBackflow = new CoreBackflow("core-backflow");

    new Beacon("beacon"){{
      requirements(Category.effect, ItemStack.with());
    }};
  }
}
