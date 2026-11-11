package lulu.content;

import arc.struct.*;
import lulu.content.tantros.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.lib.multiblock.*;
import mindustry.*;

public class OvulamContents{
  public static Seq<Power> powers(){
    return Vars.content.getBy(OvulamContentType.power.value);
  }

  public static Power power(int id){
    return Vars.content.getByID(OvulamContentType.power.value, id);
  }

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
    OvulamBlocks.load();
    OvulamLoadouts.load();

    OvulamBiomes.load();
    LaplacesTantros.load();

    MultiBlockLib.loadBlock();

    EntityRegistry.registerUnits();
  }
}
