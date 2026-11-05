package lulu.world.modules;

import arc.util.io.*;
import lulu.content.OvulamContents;
import lulu.ctype.*;
import lulu.util.func.*;
import lulu.world.OvulamPowerGraph;
import mindustry.world.modules.*;

public class PowerChannelModule extends PowerModule{
  public float[] typeCapacity;
  public float[] typeStatus;

  {
    graph = new OvulamPowerGraph();
  }

  public PowerChannelModule(){
    typeCapacity = new float[OvulamContents.powers().size];
    typeStatus = new float[OvulamContents.powers().size];
  }

  public void add(int id, float amount){
    typeCapacity[id]+=amount;
  }

  public void add(Power power, float amount){
    typeCapacity[power.id]+=amount;
  }

  public boolean has(int id){
    return typeCapacity[id] > 0;
  }

  public boolean has(int id, float amount){
    return typeCapacity[id] >= amount;
  }

  public boolean has(Power power){
    return typeCapacity[power.id] > 0;
  }

  public boolean has(Power power, float amount){
    return typeCapacity[power.id] >= amount;
  }

  public void remove(int id, float amount){
    typeCapacity[id] = Math.max(typeCapacity[id] - amount, 0);
  }

  public void remove(Power power, float amount){
    int id = power.id;
    typeCapacity[id] = Math.max(typeCapacity[id] - amount, 0);
  }


  public float get(int id){
    return typeCapacity[id];
  }

  public float get(Power power){
    return typeCapacity[power.id];
  }

  public void each(PowerConsumer cons){
    for(int i = 0; i < typeCapacity.length; i++){
      float a = typeCapacity[i];
      if(a > 0){
        cons.accept(OvulamContents.power(i), a);
      }
    }
  }

  public void eachId(IntFloatc cons){
    for(int i = 0; i < typeCapacity.length; i++){
      float a = typeCapacity[i];
      if(a > 0){
        cons.get(i, a);
      }
    }
  }

  public float sum(){
    float sum = 0f;
    for(float v : typeCapacity) sum += v;
    return sum;
  }

  public float sum(PowerCalculator calc){
    float sum = 0f;
    for(int i = 0; i < typeCapacity.length; i++){
      if(typeCapacity[i] > 0){
        sum += calc.get(OvulamContents.power(i), typeCapacity[i]);
      }
    }
    return sum;
  }

  @Override
  public void write(Writes write){
    super.write(write);
    write.i(typeCapacity.length);
    for(float f : typeCapacity){
      write.f(f);
    }
  }

  @Override
  public void read(Reads read){
    super.read(read);
    int n = read.i();
    for(int i = 0; i < n; i++){
      typeCapacity[i] = read.f();
    }
  }

  public interface PowerConsumer{
    void accept(Power power, float amount);
  }

  public interface PowerCalculator{
    float get(Power power, float amount);
  }
}
