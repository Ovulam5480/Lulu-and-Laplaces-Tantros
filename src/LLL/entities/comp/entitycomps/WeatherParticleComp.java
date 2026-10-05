package LLL.entities.comp.entitycomps;

import LLL.entities.gen.*;
import LLL.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;

@Annotations.EntityDef(value = WeatherParticlec.class, pooled = true)
@Annotations.EntityComponent
abstract class WeatherParticleComp implements Posc, Drawc{
  @Annotations.Import
  float x, y;

  public transient Color standardColor = Items.sporePod.color;
  public transient float saturationRange = 0.2f;
  public transient float hueRange = 16f;
  public transient float sizeRange = 1f;
  public transient float standardSize = 4;
  public transient float speed = 0.5f;
  public transient float drag = 0.05f;

  public float alpha = 1;
  public float size;
  public Color color = new Color();
  public boolean shouldRemove;

  public float lifetime, maxLifetime;
  public Vec2 velocity = new Vec2();

  public static CurlNoise curlNoise = new CurlNoise();

  @Override
  public void update(){
    if(shouldRemove){
      lifetime += Time.delta;

      if(lifetime >= maxLifetime){
        remove();
        return;
      }
    }

    velocity.add(curlNoise.curlAt(x, y, Tmp.v1).scl(speed * Time.delta));

    x = Mathf.mod(x + velocity.x, Vars.world.unitWidth());
    y = Mathf.mod(y + velocity.y, Vars.world.unitHeight());

    velocity.scl(Math.max(1f - drag * Time.delta, 0));

    alpha = !shouldRemove ? 1 : Mathf.lerp(1, 0, lifetime / maxLifetime);
  }

  @Override
  public void draw(){
    Draw.z(Layer.flyingUnit + 4);
    Draw.color(color, alpha);
    Fill.circle(x, y, size / Mathf.pow(Vars.renderer.camerascale, 0.3f));
    Draw.reset();
  }

  @Annotations.Replace
  @Override
  public float clipSize(){
    return size * 2;
  }

  @Override
  public void add(){
    float saturation = standardColor.saturation() + Mathf.range(saturationRange);

    if(saturation > 1) saturation -= saturationRange;

    color.set(standardColor).shiftHue(Mathf.range(hueRange)).saturation(saturation);

    x = Mathf.random(Vars.world.unitWidth());
    y = Mathf.random(Vars.world.unitHeight());

    velocity.set(speed, 0).rotate(Mathf.random(360));

    size = Mathf.range(sizeRange) + standardSize;
  }
}
