package lulu.content;

import arc.struct.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class UncStatCat{
  private static final FieldAccessor<StatCat, Integer> idField = Reflection.accessField(StatCat.class, int.class, "id");
  public static final StatCat
    structure = create("structure", 2),
    other = create("other");

  private static StatCat create(String name){
    return create(name, StatCat.all.size);
  }

  private static StatCat create(String name, int index){
    Seq<StatCat> all = StatCat.all;
    StatCat res = new StatCat(name);

    idField.set(res, index);
    all.insert(index, res);

    return res;
  }
}
