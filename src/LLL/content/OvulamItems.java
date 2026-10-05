package LLL.content;

import LLL.ctype.*;
import arc.graphics.*;
import mindustry.content.*;
import mindustry.type.*;

public class OvulamItems{
  public static Item manganeseNodule, gasHydrate, amethyst, ferroManganese, cobaltNodule, cobalt;
  public static Item calcium, phosphorus, ferrum, hydroxyapatite;
  public static Item NR, BR, SBR, NBR, EPDM, IIR, SiR, FKM;
  public static Item berserkSpore;
  public static Item causality;

  public static Item[] trophosomes;

  public static void load(){
    manganeseNodule = new Item("manganese-nodule", Color.valueOf("6b4ec1")){{
      hardness = 0;
      buildable = false;
      alwaysUnlocked = true;
    }};

    gasHydrate = new Item("gas-hydrate", Color.valueOf("dffffe")){{
    }};

    amethyst = new Item("amethyst", Color.valueOf("d05c7c")){{
    }};

    ferroManganese = new Item("ferro-manganese", Color.valueOf("8871d1")){{
    }};

    cobaltNodule = new Item("cobalt-nodule", Color.valueOf("7370db")){{
      hardness = 1;
      buildable = false;
    }};

    cobalt = new Item("cobalt", Color.valueOf("5d5d5d")){{
    }};

    calcium = new Item("calcium", Color.valueOf("c0f755")){{
    }};
    phosphorus = new Item("phosphorus", Color.valueOf("74f2eb")){{
    }};
    ferrum = new Item("ferrum", Color.valueOf("B22222")){{
    }};
    hydroxyapatite = new Item("hydroxyapatite", Color.valueOf("55f7ab")){{
    }};

    berserkSpore = new Item("berserk-spore", Color.valueOf("ff6fbd")){{
      //hidden = hideDatabase = true;
    }};

    trophosomes = new Item[]{calcium, phosphorus, Items.silicon, ferrum};

    causality = new Item("causality", Color.valueOf("ff6fbd")){{
      //hidden = hideDatabase = true;
    }};
  }

  public static void loadRubbers(){
    NR = new Rubber("naturalRubber", Color.valueOf("c0c0c0")){{
      strength = 0.90f;
      abrasionResistance = 0.85f;
      chemicalResistance = 0.60f;
      lowTempResistance = 0.80f;
      highTempResistance = 0.70f;
      gasImpermeability = 0.75f;
      processability = 1.20f;
    }};

    BR = new Rubber("polybutadieneRubber", Color.valueOf("c0c0c0")){{
      strength = 0.80f;
      abrasionResistance = 1.50f;
      chemicalResistance = 0.70f;
      lowTempResistance = 1.50f;
      highTempResistance = 0.75f;
      gasImpermeability = 0.65f;
      processability = 1.10f;
    }};
    SBR = new Rubber("styreneButadieneRubber", Color.valueOf("c0c0c0")){{
      strength = 1.00f;
      abrasionResistance = 1.00f;
      chemicalResistance = 1.00f;
      lowTempResistance = 1.00f;
      highTempResistance = 1.00f;
      gasImpermeability = 1.00f;
      processability = 1.00f;
    }};
    NBR = new Rubber("nitrileButadieneRubber", Color.valueOf("c0c0c0")){{
      strength = 1.10f;
      abrasionResistance = 1.20f;
      chemicalResistance = 2.50f;
      lowTempResistance = 0.40f;
      highTempResistance = 1.20f;
      gasImpermeability = 1.30f;
      processability = 0.95f;
    }};
    EPDM = new Rubber("ethylenePropyleneDieneMonomer", Color.valueOf("c0c0c0")){{
      strength = 0.95f;
      abrasionResistance = 1.05f;
      chemicalResistance = 1.80f;
      lowTempResistance = 1.30f;
      highTempResistance = 1.50f;
      gasImpermeability = 0.90f;
      processability = 1.05f;
    }};
    IIR = new Rubber("isobutyleneIsopreneRubber", Color.valueOf("c0c0c0")){{
      strength = 0.85f;
      abrasionResistance = 0.80f;
      chemicalResistance = 1.60f;
      lowTempResistance = 1.10f;
      highTempResistance = 1.35f;
      gasImpermeability = 5.00f;
      processability = 0.70f;
    }};
    SiR = new Rubber("siliconeRubber", Color.valueOf("c0c0c0")){{
      strength = 0.40f;
      abrasionResistance = 0.60f;
      chemicalResistance = 1.50f;
      lowTempResistance = 3.00f;
      highTempResistance = 3.00f;
      gasImpermeability = 1.20f;
      processability = 0.85f;
    }};
    FKM = new Rubber("fluoroelastomer", Color.valueOf("c0c0c0")){{
      strength = 1.30f;
      abrasionResistance = 1.10f;
      chemicalResistance = 8.00f;
      lowTempResistance = 0.60f;
      highTempResistance = 4.50f;
      gasImpermeability = 1.80f;
      processability = 0.40f;
    }};
  }
}