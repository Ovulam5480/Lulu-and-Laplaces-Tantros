package LLL.content.blocks;

import LLL.content.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.*;

import static mindustry.type.ItemStack.*;

public class Defense{
  public static void load(){
    OvulamBlocks.setCement = new Wall("set-cement"){{
      requirements(Category.defense, with());
      health = 300;
    }};

    OvulamBlocks.setMortar = new Wall("set-mortar"){{
      requirements(Category.defense, with());
      health = 400;
    }};

    OvulamBlocks.setConcrete = new Wall("set-concrete"){{
      requirements(Category.defense, with());
      health = 500;
    }};
  }
}
