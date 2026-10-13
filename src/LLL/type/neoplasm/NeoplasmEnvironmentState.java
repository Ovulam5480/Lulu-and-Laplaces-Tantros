package LLL.type.neoplasm;

import LLL.world.buildComp.module.*;
import arc.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public class NeoplasmEnvironmentState{
  private float neoplasmEnvironment;
  private float planetAttributes;
  private final float defaultNeoplasmDecay = 0.2f;

  private final Seq<NeoplasmBuildComp> all = new Seq<>();

  public NeoplasmEnvironmentState(){
    Events.run(EventType.Trigger.update, () -> {
      float newWeatherAttributes = updateWeatherAttributes();

      if(neoplasmEnvironment != newWeatherAttributes){
        all.each(n -> {
          n.environmentHandler(newWeatherAttributes / 60f * (newWeatherAttributes > 0 ? n.blockAs().neoplasmAbsorbMulti() : n.blockAs().neoplasmDecayMulti()));
        });

        neoplasmEnvironment = newWeatherAttributes;
      }
    });

    Events.on(EventType.WorldLoadEvent.class, e -> {
      planetAttributes = defaultNeoplasmDecay;

      planetAttributes += Vars.state.rules.planet.defaultAttributes.get(Attribute.water) / 2;
      planetAttributes -= Vars.state.rules.planet.defaultAttributes.get(Attribute.heat) / 2;

      if((Vars.state.rules.env & Env.groundWater) != 0){
        planetAttributes += 0.1f;
      }

      if((Vars.state.rules.env & Env.underwater) != 0){
        planetAttributes = 1;
      }
    });
  }

  public float updateWeatherAttributes(){
    float weatherAttributes = 0;

    for(WeatherState state : Groups.weather){
      if(state.weather.attrs.get(Attribute.water) > 0){
        weatherAttributes += state.weather.attrs.get(Attribute.water);
      }

      if(state.weather.attrs.get(Attribute.heat) > 0){
        weatherAttributes += state.weather.attrs.get(Attribute.heat);
      }
    }

    return weatherAttributes + planetAttributes;
  }

  public void add(NeoplasmBuildComp module){
    all.add(module);
    module.environmentHandler(neoplasmEnvironment / 60f * (neoplasmEnvironment > 0 ? module.blockAs().neoplasmAbsorbMulti() : module.blockAs().neoplasmDecayMulti()));
  }

  public void remove(NeoplasmBuildComp module){
    all.remove(module);
  }

//    public void checkPuddle(){
//        Puddle puddle = Puddles.get(tile);
//        if(puddle != null){
//            float space = liquidCapacity - liquids.get(Liquids.neoplasm);
//
//            if(puddle.liquid == Liquids.neoplasm){
//                liquids.add(Liquids.neoplasm, Math.min(space, puddle.amount));
//                puddle.amount -= space;
//            }else if(puddle.liquid == Liquids.water){
//                float amount = Math.min(space, Math.min(puddle.amount, 10 * delta()));
//
//                liquids.add(Liquids.neoplasm, amount);
//                puddle.amount -= amount / 5;
//            }
//        }
//    }
}
