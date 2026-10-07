package LLL.content;

import LLL.world.recipe.*;
import LLL.world.recipe.producers.*;
import arc.struct.*;

public class FactoryRecipes{
  public static Seq<BaseConsumers> smeltConsumers = new Seq<>();
  public static Seq<BaseProducers> smeltProducers = new Seq<>();

  public static void load(){
    smeltConsumers.add(new BaseConsumers(false).item(OvulamItems.manganeseNodule, 4).parent);
    smeltProducers.add(new BaseProducers().add(new OvulamProduceItems<>(OvulamItems.ferroManganese, 1)).parent);
  }
}
