package LLL.graphics;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.entities.*;

public class EffectBuilder{
  private final Seq<Cons<OvulamEffect.OvulamEffectContainer>> runnables = new Seq<>();
  private static final Floatp deltaProv = () -> Core.graphics.getDeltaTime() * 60f;
  private final Seq<Runnable> start = new Seq<>();
  private final Seq<Runnable> end = new Seq<>();

  public EffectBuilder start(Runnable runnable){
    start.add(runnable);
    return this;
  }

  public EffectBuilder end(Runnable runnable){
    end.add(runnable);
    return this;
  }

  public EffectBuilder runnable(Cons<OvulamEffect.OvulamEffectContainer> container){
    runnables.add(container);
    return this;
  }
//
//    public EffectBuilder timeScale(Floatp deltaProv){
//        runnables.add(e -> Time.setDeltaProvider(deltaProv));
//        return this;
//    }
//
//
//    public EffectBuilder timeScale(){
//        runnables.add(e -> Time.setDeltaProvider(deltaProv));
//        return this;
//    }


  public EffectBuilder scale(float startTime, float endTime, boolean scale, Cons<OvulamEffect.OvulamEffectContainer> cons){
    runnables.add(e -> e.scaled(startTime, endTime, scale, cons));
    return this;
  }

  public EffectBuilder stroke(float stroke){
    runnables.add(e -> Lines.stroke(stroke));
    return this;
  }

  public EffectBuilder alpha(float alpha){
    runnables.add(e -> Draw.alpha(alpha));
    return this;
  }

  public EffectBuilder color(Color color){
    runnables.add(e -> Draw.color(color));
    return this;
  }

  public EffectBuilder color(Color color, float alpha){
    runnables.add(e -> Draw.color(color, alpha));
    return this;
  }

  public EffectBuilder layer(float layer){
    runnables.add(e -> Draw.z(layer));
    return this;
  }

  private Effect build(float life){
    return new OvulamEffect(life, e -> runnables.each(c -> c.get(e))){{
      setStart(start);
      setEnd(end);
    }};
  }

  public static Effect builder(float life, Cons<EffectBuilder> cons){
    EffectBuilder builder = new EffectBuilder();
    cons.get(builder);
    return builder.build(life);
  }
}
