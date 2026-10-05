package LLL.content;

import LLL.world.uncRecipes.*;
import arc.struct.*;
import universecore.world.consumers.*;
import universecore.world.producers.*;

public class FactoryRecipes{
  public static Seq<BaseConsumers> smeltConsumers = new Seq<>();
  public static Seq<BaseProducers> smeltProducers = new Seq<>();

  public static void load(){
    smeltConsumers.add(new BaseConsumers(false).item(OvulamItems.manganeseNodule, 4).parent);
    smeltProducers.add(new BaseProducers().add(new OvulamProduceItems<>(OvulamItems.ferroManganese, 1)).parent);
  }
}
