package LLL.type.biomeplanet;

import arc.math.*;
import arc.struct.*;
import mindustry.world.*;

public class BiomeBlockData{
  public ArrayMap<Block, Float> blocks;
  public Interp interp = Interp.linear;

  public BiomeBlockData setData(Object... data){
    this.blocks = of(data);
    return this;
  }

  public BiomeBlockData setInterp(Interp interp){
    this.interp = interp;
    return this;
  }

  public Block get(float noise){
    float n = Mathf.clamp(interp.apply(noise));
    for(ObjectMap.Entry<Block, Float> entry : blocks){
      if(entry.value > n){
        return entry.key;
      }
    }
    return null;
  }

  public static <T> ArrayMap<T, Float> of(Object... blocks){
    ArrayMap<T, Float> map = new ArrayMap<>();
    float total = 0;
    for(int i = 0; i < blocks.length; i += 2){
      float value = blocks[i + 1] instanceof Integer ? (int)blocks[i + 1] :
        blocks[i + 1] instanceof Float ? (float)blocks[i + 1] :
          (float)(double)blocks[i + 1];

      total += value;
      map.put((T)blocks[i], total);
    }

    float finalTotal = total;
    map.forEach(e -> map.put(e.key, e.value / finalTotal));
    return map;
  }
}
