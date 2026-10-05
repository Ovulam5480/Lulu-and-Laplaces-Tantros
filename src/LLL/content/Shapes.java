package LLL.content;

import LLL.*;
import arc.*;
import arc.graphics.g2d.*;

public class Shapes{
  public static TextureRegion sparkle, crescent;

  public static void load(){
    sparkle = loadShape("sparkle");
    crescent = loadShape("crescent");
  }

  public static TextureRegion loadShape(String name){
    return Core.atlas.find(LuluMod.modName + name);
  }
}
