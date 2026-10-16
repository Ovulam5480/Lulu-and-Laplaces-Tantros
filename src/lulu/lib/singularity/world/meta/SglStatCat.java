package lulu.lib.singularity.world.meta;

import arc.struct.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class SglStatCat{
  private static final FieldAccessor<StatCat, Integer> idField = Reflection.accessField(StatCat.class, int.class, "id");
  public static final StatCat
    neutron = create("neutron", 3),
    matrix = create("matrix", 5),
    heat = create("heat", 6),
    reaction = create("reaction");

  private static StatCat create(String name){
    return create(name, StatCat.all.size);
  }

  private static StatCat create(String name, int index){
    Seq<StatCat> all = StatCat.all;
    StatCat res = new StatCat(name);

    all.remove(res);
    all.insert(index, res);

    for(int i = 0; i < all.size; i++){
      idField.set(all.get(i), i);
    }

    return res;
  }
}
