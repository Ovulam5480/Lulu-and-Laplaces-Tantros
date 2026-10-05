package LLL.content.extensions;

import LLL.content.resourceTypes.*;
import LLL.type.biomeplanet.*;
import mindustry.ctype.*;
import universecore.util.*;

public class OvulamContentType extends UncContentType{
  public static OvulamContentType power;
  public static OvulamContentType biome;

  public OvulamContentType(String name, Class<? extends Content> contentClass){
    super(name, contentClass);
  }

  public OvulamContentType(String name, int ordinal, Class<? extends Content> contentClass){
    super(name, ordinal, contentClass);
  }

  public OvulamContentType(String name, int ordinal, Class<? extends Content> contentClass, boolean display){
    super(name, ordinal, contentClass, display);
  }

  public static void load(){
    power = new OvulamContentType("power", Power.class);
    biome = new OvulamContentType("biome", Biome.class);
  }
}
