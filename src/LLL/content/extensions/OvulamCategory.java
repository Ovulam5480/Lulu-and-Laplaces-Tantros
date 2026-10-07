package LLL.content.extensions;

import LLL.*;
import mindustry.type.*;

public class OvulamCategory{
  public static Category packet, tileEntity, neoplastic;

  public static void load(){
    packet = LuluMod.categories.add("packet", 10, LuluMod.modName + "packet64-3像素外发光");
    tileEntity = LuluMod.categories.add("tileEntity", 11, LuluMod.modName + "tileEntity");
    neoplastic = LuluMod.categories.add("neoplastic", 12, LuluMod.modName + "neoplastic");
  }
}
