package LLL.entities.ability;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class ShockwaveAbility extends Ability{
  public float range, unitRadius;
  public float stroke = 4f, unitStroke = 0.7f;

  public float waveDamage;

  public float[] reloads;
  public float x, y;

  public float waveTime = 40f;

  public Effect hitEffect = Fx.hitSquaresColor;
  public Color waveColor = Color.white;

  protected float totalTime;
  protected float lastTime;
  protected FloatSeq wavedTimes = new FloatSeq();

  public ShockwaveAbility(float range, float waveDamage, float... reloads){
    this.range = range;
    this.waveDamage = waveDamage;
    this.reloads = reloads;

    for(float reload : reloads){
      totalTime += reload;
    }
  }

  @Override
  public void init(UnitType type){
    if(unitRadius == 0) unitRadius = type.hitSize * 0.7f;
    type.clipSize = range + 8 + Mathf.dst(x, y);
  }

  @Override
  public void created(Unit unit){
    lastTime = Time.time;
  }

  @Override
  public void update(Unit unit){
    float curTime = Time.time;
    if(curTime == lastTime){
      return;
    }

    Tmp.v1.set(x, y).rotate(unit.rotation - 90).add(unit.x, unit.y);

    float cx = Tmp.v1.x;
    float cy = Tmp.v1.y;

    float t = lastTime % totalTime;
    for(float reload : reloads){
      if(t > reload){
        t -= reload;
      }else{
        if(t + Time.delta > reload){
          wavedTimes.add(curTime);

          Groups.bullet.intersect(cx - range, cy - range, range * 2, range * 2, target -> {
            if(target.team != unit.team && target.type.hittable && target.within(cx, cy, range)){
              if(target.damage > waveDamage){
                target.damage -= waveDamage;
              }else{
                target.remove();
              }
            }
          });
        }

        break;
      }
    }

    lastTime = curTime;

    if(!wavedTimes.isEmpty() && curTime - wavedTimes.first() > waveTime){
      wavedTimes.removeIndex(0);
    }
  }

  @Override
  public void draw(Unit unit){
    float curTime = Time.time;
    Tmp.v1.set(x, y).rotate(unit.rotation - 90).add(unit.x, unit.y);

    float cx = Tmp.v1.x;
    float cy = Tmp.v1.y;

    Draw.color(waveColor);
    for(int i = 0; i < wavedTimes.size; i++){
      float progress = (curTime - wavedTimes.get(i)) / waveTime;

      Draw.alpha((1 - progress) * 0.6f);

      Lines.stroke(stroke);
      Lines.circle(cx, cy, Mathf.lerp(unitRadius, range, progress));
      Lines.stroke(unitStroke);
      Lines.circle(cx, cy, Mathf.lerp(0, unitRadius, progress));
    }
  }

  @Override
  public Ability copy(){
    try{
      return ((ShockwaveAbility)clone()).newWaved();
    }catch(CloneNotSupportedException e){
      throw new RuntimeException("AAA", e);
    }
  }

  public ShockwaveAbility newWaved(){
    wavedTimes = new FloatSeq();
    return this;
  }
}
