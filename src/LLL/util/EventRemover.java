package LLL.util;

import arc.*;
import arc.func.*;
import arc.struct.*;

import java.lang.reflect.*;

public class EventRemover{
  public static ObjectMap<Object, Seq<Cons<?>>> events;

  static{
    Class<?> c = Events.class;
    try{
      Field field = c.getDeclaredField("events");
      field.setAccessible(true);
      events = (ObjectMap<Object, Seq<Cons<?>>>)field.get(c);
    }catch(NoSuchFieldException | IllegalAccessException e){
      throw new RuntimeException(e);
    }
  }

  public static void removeEvent(Class<?> eventType, Class<?> targetClass, int index, boolean isTrigger){
    String target = targetClass.getSimpleName();

    int i = 0;
    for(ObjectMap.Entry<Object, Seq<Cons<?>>> event : events){
      if(isTrigger ? event.key.getClass() == eventType : event.key == eventType){
        for(Cons<?> cons : event.value){
          if(String.valueOf(cons).contains(target)){
            if(i == index){
              event.value.remove(cons);
              break;
            }else i++;
          }
        }
      }
    }
  }

  public static void removeEvent(Class<?> eventType, Class<?> targetClass, boolean trigger){
    removeEvent(eventType, targetClass, 0, trigger);
  }

  public static void removeEvent(Class<?> eventType, Class<?> targetClass){
    removeEvent(eventType, targetClass, false);
  }
}
