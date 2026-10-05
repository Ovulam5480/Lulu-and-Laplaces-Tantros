package LLL.content.extensions;

import LLL.*;
import mindustry.type.*;
import universecore.*;

public class OvulamCategory{
  public static Category packet, tileEntity, neoplastic;

  public static void load(){
    packet = UncCore.categories.add("packet", 10, LuluMod.modName + "packet64-3像素外发光");
    tileEntity = UncCore.categories.add("tileEntity", 11, LuluMod.modName + "tileEntity");
    neoplastic = UncCore.categories.add("neoplastic", 12, LuluMod.modName + "neoplastic");
  }
}
