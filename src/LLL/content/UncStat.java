package LLL.content;

import arc.struct.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class UncStat{
  private static final FieldAccessor<Stat, Integer> idField = Reflection.accessField(Stat.class, int.class, "id");
  public static final Stat optionalInputs = create("optionalInputs", UncStatCat.other),
    inputs = create("inputs", StatCat.crafting),
    maxStructureSize = create("maxStructureSize", UncStatCat.structure);

  private static Stat create(String name, StatCat cat){
    return create(name, Stat.all.size, cat);
  }

  private static Stat create(String name, int index, StatCat cat){
    Seq<Stat> all = Stat.all;
    Stat res = new Stat(name, cat);

    idField.set(res, res.id);
    all.insert(index, res);

    return res;
  }
}
