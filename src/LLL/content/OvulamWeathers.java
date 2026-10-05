package LLL.content;

import LLL.type.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class OvulamWeathers{
  public static Weather berserkSporeWind;

  public static void load(){
    berserkSporeWind = new BerserkSporeWind("berserk-spore-wind"){{
      attrs.set(Attribute.spores, 0.3f);
      attrs.set(Attribute.water, 0.1f);
      attrs.set(Attribute.heat, -0.1f);
      attrs.set(Attribute.light, -0.2f);
    }};
  }
}
