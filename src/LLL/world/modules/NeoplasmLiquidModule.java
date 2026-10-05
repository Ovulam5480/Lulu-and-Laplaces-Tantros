package LLL.world.modules;

import arc.math.*;
import arc.util.io.*;
import mindustry.world.modules.*;

public class NeoplasmLiquidModule extends BlockModule{
  private float neoplasmAmount = 0;
  public float liquidCapacity;

  public float lastAdd, lastRemove;
  public WindowedMean neoplasmMean = new WindowedMean(60);

  public NeoplasmLiquidModule(float liquidCapacity){
    this.liquidCapacity = liquidCapacity;
  }

  public void remove(float amount){
    neoplasmAmount -= amount;
  }

  public void removeClamp(float amount){
    neoplasmAmount = Math.max(neoplasmAmount - amount, 0);
  }

  public void add(float amount){
    neoplasmAmount += amount;

    if(amount > 0){
      lastAdd = amount;
    }else{
      lastRemove = -amount;
      neoplasmMean.add(-amount);
    }
  }

  public void addClamp(float amount, float max){
    neoplasmAmount = Math.min(neoplasmAmount + amount, max);
  }

  public void set(float amount){
    neoplasmAmount = amount;
  }

  public void setClamp(float amount, float max){
    neoplasmAmount = Mathf.clamp(amount, 0, max);
  }

  public float amount(){
    return neoplasmAmount;
  }

  public boolean hasAny(){
    return neoplasmAmount > 0;
  }

  public boolean has(float amount){
    return neoplasmAmount >= amount;
  }

  @Override
  public void write(Writes write){
    write.f(neoplasmAmount);
  }

  @Override
  public void read(Reads read, boolean legacy){
    neoplasmAmount = read.f();
  }
}
