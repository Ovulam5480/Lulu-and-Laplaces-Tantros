package lulu.util.uncUtil.handler;

import lulu.util.uncUtil.*;
import arc.*;
import arc.func.*;
import arc.struct.*;
import universe.util.reflect.*;

import java.lang.reflect.*;

/**
 * 用于对arc库的Events执行搜索与删除操作的工具集
 */
public class EventsHandler{
  private static final ObjectMap<Object, Seq<Cons<?>>> events = Reflection.accessStaticField(Events.class, ObjectMap.class, "events").get();

  public static Cons<?>[] getListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter){
    return events.get(event, Empties.nilSeq()).select(e -> {
      Class<?> c = e.getClass();
      if(c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
        || c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName())){
        return filter.get(e, e.getClass().getDeclaredFields());
      }
      return false;
    }).toArray(Cons.class);
  }

  public static void removeListener(Object event, Class<?> declaringClass, Boolf2<Cons<?>, Field[]> filter){
    events.get(event, Empties.nilSeq()).remove(e -> {
      Class<?> c = e.getClass();
      if(c.getName().substring(0, c.getName().indexOf("Lambda")).equals(declaringClass.getName())
        || c.getName().substring(0, c.getName().indexOf("ExternalSyntheticLambda")).equals(declaringClass.getName())){
        return filter.get(e, e.getClass().getDeclaredFields());
      }
      return false;
    });
  }
}
