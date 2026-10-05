package LLL.util;

import arc.*;
import arc.func.*;
import arc.struct.*;

public class OvulamSettings{
  private static final ObjectMap<String, Seq<Cons<Boolean>>> boolEvents = new ObjectMap<>();
  private static final ObjectMap<String, Seq<Cons<Integer>>> intEvents = new ObjectMap<>();
  private static final ObjectMap<String, Seq<Cons<Long>>> longEvents = new ObjectMap<>();
  private static final ObjectMap<String, Seq<Cons<Float>>> floatEvents = new ObjectMap<>();
  private static final ObjectMap<String, Seq<Cons<String>>> stringEvents = new ObjectMap<>();
  private static final ObjectMap<String, Seq<Cons<byte[]>>> bytesEvents = new ObjectMap<>();

  public static boolean registerBool(String name, Cons<Boolean> event, boolean defaultValue){
    boolean value = Core.settings.getBool(name, defaultValue);
    register(boolEvents, name, event);
    return value;
  }

  public static int registerInt(String name, Cons<Integer> event, int defaultValue){
    int value = Core.settings.getInt(name, defaultValue);
    register(intEvents, name, event);
    return value;
  }

  public static long registerLong(String name, Cons<Long> event, long defaultValue){
    long value = Core.settings.getLong(name, defaultValue);
    register(longEvents, name, event);
    return value;
  }

  public static float registerFloat(String name, Cons<Float> event, float defaultValue){
    float value = Core.settings.getFloat(name, defaultValue);
    register(floatEvents, name, event);
    return value;
  }

  public static String registerString(String name, Cons<String> event, String defaultValue){
    String value = Core.settings.getString(name, defaultValue);
    register(stringEvents, name, event);
    return value;
  }

  public static byte[] registerBytes(String name, Cons<byte[]> event, byte[] defaultValue){
    byte[] value = Core.settings.getBytes(name, defaultValue);
    register(bytesEvents, name, event);
    return value;
  }

  private static <T> void register(ObjectMap<String, Seq<Cons<T>>> events, String name, Cons<T> event){
    Seq<Cons<T>> seq = events.get(name);
    if(seq == null){
      events.put(name, seq = new Seq<>());
    }
    seq.add(event);
  }

  public static void putBool(String name, boolean value){
    Core.settings.put(name, value);
    fire(boolEvents, name, value);
  }

  public static void putInt(String name, int value){
    Core.settings.put(name, value);
    fire(intEvents, name, value);
  }

  public static void putLong(String name, long value){
    Core.settings.put(name, value);
    fire(longEvents, name, value);
  }

  public static void putFloat(String name, float value){
    Core.settings.put(name, value);
    fire(floatEvents, name, value);
  }

  public static void putString(String name, String value){
    Core.settings.put(name, value);
    fire(stringEvents, name, value);
  }

  public static void putBytes(String name, byte[] value){
    Core.settings.put(name, value);
    fire(bytesEvents, name, value);
  }

  private static <T> void fire(ObjectMap<String, Seq<Cons<T>>> events, String name, T value){
    Seq<Cons<T>> seq = events.get(name);
    if(seq != null){
      seq.each(c -> c.get(value));
    }
  }

  public static boolean getBool(String name, boolean defaultValue){
    return Core.settings.getBool(name, defaultValue);
  }

  public static int getInt(String name, int defaultValue){
    return Core.settings.getInt(name, defaultValue);
  }

  public static long getLong(String name, long defaultValue){
    return Core.settings.getLong(name, defaultValue);
  }

  public static float getFloat(String name, float defaultValue){
    return Core.settings.getFloat(name, defaultValue);
  }

  public static String getString(String name, String defaultValue){
    return Core.settings.getString(name, defaultValue);
  }

  public static byte[] getBytes(String name, byte[] defaultValue){
    return Core.settings.getBytes(name, defaultValue);
  }
}
