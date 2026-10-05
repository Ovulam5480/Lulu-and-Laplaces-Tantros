package LLL.content;

import LLL.content.extensions.*;
import LLL.content.tantros.*;
import LLL.entities.gen.*;
import LLL.lib.multiblock.*;

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
    FactoryRecipes.load();
    OvulamBlocks.load();
    OvulamLoadouts.load();

    OvulamBiomes.load();
    LaplacesTantros.load();

    MultiBlockLib.loadBlock();
  }
}
