package lulu.content;

import lulu.world.recipe.*;
import arc.struct.*;

public class FactoryRecipes{
  public static Seq<BaseConsumers> smeltConsumers = new Seq<>();
  public static Seq<BaseProducers> smeltProducers = new Seq<>();

  public static void load(){
    smeltConsumers.add(new BaseConsumers(false).item(OvulamItems.manganeseNodule, 4).parent);
    smeltProducers.add(new BaseProducers().item(OvulamItems.ferroManganese, 1).parent);
  }
}
