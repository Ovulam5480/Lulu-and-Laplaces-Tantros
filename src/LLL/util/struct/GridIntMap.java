package LLL.util.struct;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;

public class GridIntMap<T>{
  public IntMap<T> map = new IntMap<>();
  private static final Rand rand = new Rand();

  private static int getHash(int x, int y){
    return (x << 16) | (y & 0xffff);
  }

  public T get(int x, int y){
    return map.get(getHash(x, y));
  }

  public T get(int x, int y, T defaultValue){
    int hash = getHash(x, y);
    if(!map.containsKey(hash)){
      return defaultValue;
    }
    return map.get(hash);
  }

  public boolean containsKey(int x, int y){
    return map.containsKey(getHash(x, y));
  }

  public void put(int x, int y, T t){
    map.put(getHash(x, y), t);
  }

  public void put(Posc posc){
    put(posc.tileX(), posc.tileY(), (T)posc);
  }

  public boolean putIfAbsent(int x, int y, T t){
    int hash = getHash(x, y);

    if(!map.containsKey(hash)){
      map.put(hash, t);
      return true;
    }
    return false;
  }

  public void remove(int x, int y){
    map.remove(getHash(x, y));
  }

  public void remove(Posc posc){
    remove(posc.tileX(), posc.tileY());
  }

  public IntMap.Values<T> values(){
    return map.values();
  }

  public IntMap.Keys keys(){
    return map.keys();
  }

  public void clear(){
    map.clear();
  }

  public int size(){
    return map.size;
  }

  public IntMap.Entry<T> random(){
    int index = rand.nextInt(map.size);

    for(IntMap.Entry<T> tEntry : map){
      if(index-- == 0){
        return tEntry;
      }
    }

    return null;
  }

  public void random(Cons3<Integer, Integer, T> cons3){
    int index = rand.nextInt(map.size);

    for(IntMap.Entry<T> tEntry : map){
      if(index-- == 0){
        cons3.get(tEntry.key >> 16, tEntry.key & 0xffff, tEntry.value);
        return;
      }
    }
  }

  public void each(Cons3<Integer, Integer, T> cons3){
    for(IntMap.Entry<T> entry : map){
      cons3.get(entry.key >> 16, entry.key & 0xffff, entry.value);
    }
  }
}