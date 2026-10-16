package lulu.content;

import lulu.content.extensions.*;
import lulu.content.tantros.*;
import lulu.entities.gen.*;
import lulu.lib.multiblock.*;

public class OvulamContents{
  public static void load(){
    OvulamSounds.load();
    OvulamContentType.load();
    OvulamBlockFlags.load();
    OvulamCategory.load();

    EntityRegistry.register();

    OvulamAttributes.load();
    OvulamWeathers.load();
    OvulamItems.load();
    OvulamLiquids.load();
    OvulamResource.load();
    OvulamPacketTypes.load();
    OvulamUnitTypes.load();
    EntityRegistry.registerUnits();
    FactoryRecipes.load();
    OvulamBlocks.load();
    OvulamLoadouts.load();

    OvulamBiomes.load();
    LaplacesTantros.load();

    MultiBlockLib.loadBlock();

    EntityRegistry.registerUnits();
  }
}
