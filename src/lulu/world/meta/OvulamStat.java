package lulu.world.meta;

import arc.struct.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class OvulamStat{
  private static final FieldAccessor<Stat, Integer> idField = Reflection.accessField(Stat.class, int.class, "id");

  public static final Stat
    autoSelect = create("autoSelect", 46, StatCat.crafting),
    controllable = create("controllable", 47, StatCat.crafting),
    recipes = create("recipes", 48, StatCat.crafting),
    optionalInputs = create("optionalInputs", OvulamContentStatCat.other),
    maxStructureSize = create("maxStructureSize", OvulamContentStatCat.structure);

  private static Stat create(String name, StatCat cat){
    return create(name, Stat.all.size, cat);
  }

  private static Stat create(String name, int index, StatCat cat){
    Seq<Stat> all = Stat.all;
    Stat res = new Stat(name, cat);

    all.remove(res);
    all.insert(index, res);

    for(int i = 0; i < all.size; i++){
      idField.set(all.get(i), i);
    }

    return res;
  }
}
