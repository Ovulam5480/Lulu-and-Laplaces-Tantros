package LLL.content;

import LLL.ctype.*;
import arc.graphics.*;
import mindustry.type.*;

public class OvulamLiquids{
  public static Liquid gas, air;
  public static Liquid cementSlurry, mortar, concrete;

  public static void load(){
    gas = new Liquid("gas", Color.valueOf("dffffe")){{
      gas = true;
    }};

    air = new Liquid("air", Color.valueOf("dffffe")){{
      gas = true;
    }};

    cementSlurry = new HardeningLiquid("cement-slurry", Color.grays(0.45f), () -> OvulamBlocks.setCement){{
      hardeningRate = 0.0005f;
    }};

    mortar = new HardeningLiquid("mortar", Color.grays(0.4f), () -> OvulamBlocks.setMortar){{
      hardeningRate = 0.0008f;
    }};

    concrete = new HardeningLiquid("concrete", Color.grays(0.35f), () -> OvulamBlocks.setConcrete){{
      hardeningRate = 0.001f;
    }};
  }
}
