package LLL.content.extensions;

import mindustry.world.meta.*;

public class OvulamAttributes{
  public static Attribute hydrothermal, gasHydrate;

  public static void load(){
    hydrothermal = Attribute.add("hydrothermal");
    gasHydrate = Attribute.add("gas-hydrate");
  }
}
