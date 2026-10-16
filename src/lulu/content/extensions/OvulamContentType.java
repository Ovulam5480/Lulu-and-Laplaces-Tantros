package lulu.content.extensions;

import lulu.content.*;
import lulu.content.resourceTypes.*;
import lulu.ctype.packet.*;
import lulu.type.biomeplanet.*;
import mindustry.ctype.*;

public class OvulamContentType extends UncContentType{
  public static OvulamContentType power;
  public static OvulamContentType biome;
  public static OvulamContentType packetType;

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
    packetType = new OvulamContentType("packetType", PacketType.class);
  }
}
