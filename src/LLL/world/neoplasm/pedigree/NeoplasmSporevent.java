package LLL.world.neoplasm.pedigree;

import LLL.content.*;
import LLL.world.neoplasm.crafting.*;
import arc.*;
import arc.util.io.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;

public class NeoplasmSporevent extends NeoplasmFactory{
  public static int total = 0;

  static{
    Events.on(EventType.ResetEvent.class, e -> total = 0);
  }

  public int trigger = 15;
  public Weather weather = OvulamWeathers.berserkSporeWind;

  public NeoplasmSporevent(String name){
    super(name);
  }

  public class NeoplasmSporeventBuild extends NeoplasmFactoryBuild{
    //todo 换世界时重置

    @Override
    public void craftTrigger(){
      super.craftTrigger();

      if(shouldNetworkUpdate()){
        total++;

        if(total >= trigger){
          total = 0;

          Call.createWeather(weather, 0, weather.duration, 0, 0);
        }
      }
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.i(total);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      total = read.i();
    }
  }
}
