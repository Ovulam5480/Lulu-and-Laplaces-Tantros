package LLL.type;

import LLL.*;
import LLL.entities.gen.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.util.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;

public class BerserkSporeWind extends OvulamWeather{
  public Color color = Items.sporePod.color;
  public float amount = 250;
  public float minSize = 14, maxSize = 22;

  public float saturationRange = 0.5f;
  public float hueRange = 16f;

  public int particlePerTile = 400;

  protected Color tmpColor = new Color();
  public Seq<WeatherParticle> particles = new Seq<>();

  public TextureRegion crescentRegion;

  public BerserkSporeWind(String name, Prov<WeatherState> type){
    super(name, type);

    duration = 7f * Time.toMinutes;
  }

  public BerserkSporeWind(String name){
    super(name);

//        Events.on(EventType.WorldLoadEvent.class, e -> {
//            Call.createWeather(this, 1, duration, 0, 0);
//        });
  }

  @Override
  public void load(){
    super.load();

    crescentRegion = Core.atlas.find(LuluMod.modName + "crescent");
  }

  @Override
  public void update(WeatherState state){
    for(Unit unit : Groups.unit){
      if(unit.isFlying()){
        unit.vel.add(WeatherParticle.curlNoise.curlAt(unit.x, unit.y, Tmp.v1).scl(0.3f * Time.delta / unit.hitSize));
      }
    }
  }

  @Override
  public WeatherState create(float intensity, float duration){
    int amount = Vars.world.tiles.width * Vars.world.tiles.height / particlePerTile;

    for(int i = 0; i < amount; i++){
      WeatherParticle particle = WeatherParticle.create();
      particle.add();

      particles.add(particle);
    }

    return super.create(intensity, duration);
  }

  @Override
  public void drawOver(WeatherState state){
    rand.setSeed(0);

    float scl = Vars.renderer.getDisplayScale();
    float raduis = Math.max(Core.graphics.getWidth(), Core.graphics.getHeight()) / 2f / scl;
    float sin = Mathf.lerp(1, 1.1f, Mathf.sin(20, 1));

    Vec2 center = Tmp.v1.set(Core.camera.position);
    Vec2 offset = Tmp.v2.set(Vars.world.unitWidth(), Vars.world.unitHeight())
      .scl(0.5f)
      .sub(center)
      .scl(0.1f, 0.1f);

    float saturation = color.saturation();

    for(int i = 0; i < amount; i++){
      float percent = (float)i / amount;

      float sat = saturation + rand.nextFloat() * saturationRange;
      if(sat > 1) sat -= saturationRange;
      tmpColor.set(color).shiftHue(rand.nextFloat() * hueRange).saturation(sat);
      Draw.color(tmpColor);

      float time = Time.time * Mathf.lerp(1.05f, 0.95f, percent) + Mathf.lerp(0, 10000, percent);

      Vec2 pos = MathTransform.fourierSeries(
        time * 1.3f,
        Mathf.lerp(5.5f, 3.5f, percent), 360f * percent, percent * 1000 + 180,
        rand.nextFloat() * 4.2f, 0, 130).scl(Mathf.pow(scl, 0.2f));

      float size = Mathf.lerp(minSize, maxSize, percent) / scl;

      Fill.circle(center.x + (pos.x + offset.x) / scl, center.y + (pos.y + offset.y) / scl, size / 2);

      if(rand.chance(0.1f)){
        Vec2 crescent = MathTransform.fourierSeries(
          time,
          Mathf.lerp(5.5f, 3.5f, percent), 360f * percent, percent * 1000 + 180,
          rand.nextFloat() * 4.2f, 0, 130).scl(Mathf.pow(scl, 0.2f));

        Draw.rect(crescentRegion,
          center.x + (offset.x - crescent.x / 2) / scl,
          center.y + (offset.y - crescent.y / 2) / scl,
          crescent.len() / scl,
          crescent.len() / scl * 0.6f,
          crescent.angle() + 90);
      }
    }

    Draw.color(color, 0.8f * sin);
    SglDraw.gradientCircle(center.x, center.y, raduis * 1.2f, -raduis * 0.7f * sin, 0);
    drawNoise(state);
  }

  public void drawNoise(WeatherState state){
  }

  @Override
  public void remove(){
    super.remove();

    particles.each(w -> w.shouldRemove(true));
  }
}
