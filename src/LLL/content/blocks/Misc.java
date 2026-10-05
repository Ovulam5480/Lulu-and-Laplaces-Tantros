package LLL.content.blocks;

import LLL.content.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.*;
import LLL.world.blocks.effect.*;
import LLL.world.blocks.entity.*;
import LLL.world.blocks.packet.*;
import LLL.world.blocks.special.tjTool.*;
import LLL.world.blocks.storage.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

public class Misc{
  public static void load(){
    OvulamBlocks.blastCylinder = new BlastCylinder("blast-cylinder");
    OvulamBlocks.ferroManganeseRodGroup = new ThrowPacketWall("ferromanganese-rod-group"){{
      size = 2;
      resources.add(new ItemResourceStack().set(OvulamItems.ferroManganese, 5));
      researchCost = requirements = ItemStack.with(OvulamItems.ferroManganese, 24);
      buildTime = 60 * 8;
    }};

    new FrameBlock("frame-block"){{
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

    OvulamBlocks.coreBackflow = new CoreBackflow("core-backflow");
    OvulamBlocks.coreCausality = new CoreCausality("core-causality");

    new Beacon("beacon"){{
      requirements(Category.effect, ItemStack.with());
    }};
  }
}
