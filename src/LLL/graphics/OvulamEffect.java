package LLL.graphics;

import LLL.*;
import LLL.entities.gen.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class OvulamEffect extends Effect{
  public static final OvulamEffectContainer container = new OvulamEffectContainer();
  public Cons<OvulamEffectContainer> renderer;
  public Seq<Runnable> runnables = new Seq<>(), start = new Seq<>(), end = new Seq<>();

  private static final float shakeFalloff = 10000f;

  public static final Floatp deltaProv = () -> Core.graphics.getDeltaTime() * 60f;

  public OvulamEffect(float life, Cons<OvulamEffectContainer> renderer){
    this(life, 50f, renderer);
  }

  public OvulamEffect(float life, float clipsize, Cons<OvulamEffectContainer> renderer){
    super(life, clipsize, null);
    this.renderer = renderer;
  }

  public OvulamEffect setTimeDelta(Floatf<OvulamEffectContainer> cons){
    runnables.add(() -> Time.setDeltaProvider(() -> cons.get(container) * deltaProv.get()));
    end.add(() -> Time.setDeltaProvider(deltaProv));

    return this;
  }

  public OvulamEffect setZoom(Floatf<OvulamEffectContainer> cons){
    runnables.add(() -> Vars.renderer.targetscale = cons.get(container));

    return this;
  }

  public float render(int id, Color color, float life, float lifetime, float rotation, float x, float y, Object data){
    container.set(id, color, life, lifetime, rotation, x, y, data);
    Draw.z(layer);
    Draw.reset();
    render(container);
    Draw.reset();

    return container.lifetime;
  }

  public void render(OvulamEffectContainer e){
    renderer.get(e);
  }

  public void update(int id, Color color, float life, float lifetime, float rotation, float x, float y, Object data){
    container.set(id, color, life, lifetime, rotation, x, y, data);
    runnables.each(Runnable::run);
  }

  protected void add(float x, float y, float rotation, Color color, Object data){
    OvulamEffectState entity = OvulamEffectState.create();
    entity.effect = this;
    entity.rotation = baseRotation + rotation;
    entity.data = data;
    entity.lifetime = lifetime;
    entity.set(x, y);
    entity.color.set(color);
    if(followParent && data instanceof Posc p){
      entity.parent = p;
      entity.rotWithParent = rotWithParent;
    }
    entity.add();
  }

  private static void shake(float intensity, float duration){
    if(!headless){
      LuluMod.graphics.shake.shake(intensity, duration);
    }
  }

  public static void shake(float intensity, float duration, float x, float y){
    if(Core.camera == null) return;

    float distance = Core.camera.position.dst(x, y);
    if(distance < 1) distance = 1;

    shake(Mathf.clamp(1f / (distance * distance / shakeFalloff)) * intensity, duration);
  }

  public static void shake(float intensity, float duration, Position loc){
    shake(intensity, duration, loc.getX(), loc.getY());
  }

  public void setStart(Seq<Runnable> start){
    this.start = start;
  }

  public void setEnd(Seq<Runnable> end){
    this.end = end;
  }

  public static class OvulamEffectContainer extends EffectContainer{
    private OvulamEffectContainer innerContainer;

    public void centerCamera(){
      Core.camera.position.set(x, y);
    }

    public void scaled(float startTime, float endTime, boolean scale, Cons<OvulamEffectContainer> cons){
      if(innerContainer == null) innerContainer = new OvulamEffectContainer();
      if(time <= endTime && time >= startTime){
        innerContainer.set(id, color, time, scale ? (endTime - startTime) : lifetime, rotation, x, y, data);
        cons.get(innerContainer);
      }
    }
  }
}
