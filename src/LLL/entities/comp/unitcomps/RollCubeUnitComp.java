package LLL.entities.comp.unitcomps;

import LLL.entities.types.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class RollCubeUnitComp implements Unitc{
  @Annotations.Import
  UnitType type;
  @Annotations.Import
  Vec2 vel;
  @Annotations.Import
  float x, y;
  @Annotations.Import
  float hitSize;
  @Annotations.Import
  Team team;
  @Annotations.Import
  float deltaX, deltaY;

  public boolean isRolling;
  public boolean hasInitRolling;
  public boolean zeroTarget = true;
  public int changeQuad;

  public float damageTimer;

  public float intervalTimer, rollingTimer;
  public float[] lengths = new float[6];

  public Vec2 target = new Vec2();

  public RollCubeUnitType asType(){
    return (RollCubeUnitType)type;
  }

  public TextureRegion getRegion(){
    return asType().region;
  }

  @Annotations.Replace(999)
  @Override
  public void draw(){
    asType().draw(self());
  }

  @Override
  public void moveAt(Vec2 vector, float acceleration){
    //初始化每一次的滚动, 保证滚动时变量不会改变
    if(isRolling && !hasInitRolling){
      //目标速度向量为0时, 停止计时器和初始化, 不设置目标
      zeroTarget = vector.epsilonEquals(Vec2.ZERO, 0.01f);
      if(zeroTarget) return;

      if(!Vars.mobile && isPlayer()) target.set(((DesktopInput)Vars.control.input).movement);
      else target.set(vector);

      float quad = target.angle() / 90 % 1;
      float change = asType().randomRoll ? quad : quad > 0.5f ? 1 : 0;
      changeQuad = Mathf.num(Mathf.randomBoolean(change));

      hasInitRolling = true;
    }
  }

  public float getProgress(){
    return Mathf.clamp(rollingTimer / asType().rollingTime, 0, 1);
  }

  public float getRollingVel(){
    return hitSize / asType().rollingTime;
  }

  public float reversal(float timer, float time){
    timer += Time.delta;
    if(timer > time){
      isRolling = !isRolling;
      hasInitRolling = false;

      timer = 0;
      changeQuad = -1;
    }
    return timer;
  }

  public int targetRot(){
    return Mathf.floor(target.angle() / 90) + changeQuad;
  }

  @Override
  public void update(){
    if(hasInitRolling) vel.trns(targetRot() * 90, getRollingVel());
    else{
      zeroTarget = true;
      vel.setZero();
    }

    //位于(0,0)的点不需要移动
    for(int i = 0; i < 3; i++){
      //-1,-1  -1,1  1,1  1,-1
      Tmp.v1.set(Geometry.d8edge(i + 3).x, Geometry.d8edge(i + 3).y)
        .rotate(getProgress() * 90f).add(0, 1).scl(0.5f);

      int index = i * 2;
      lengths[index] = -Tmp.v1.x;
      lengths[index + 1] = Mathf.lerp(asType().bScl, 1, Tmp.v1.y);
    }

    //滚动并且目标速度不是0, 则进行滚动
    if(!isRolling) intervalTimer = reversal(intervalTimer, asType().rollInterval);
    else if(!zeroTarget) rollingTimer = reversal(rollingTimer, asType().rollingTime);

    if((damageTimer += Time.delta) > 5f){
      float radius = hitSize / tilesize / 2f;
      for(float dx = -radius; dx <= radius; dx++){
        for(float dy = -radius; dy <= radius; dy++){
          Tile t = Vars.world.tileWorld(x + dx * tilesize, y + dy * tilesize);

          if(type.crushDamage > 0 && t != null && t.build != null && t.build.team != team){
            t.build.damage(team, type.crushDamage * 5f * t.block().crushDamageMultiplier * state.rules.unitDamage(team));
          }
        }
      }

      Units.nearbyEnemies(team, x - radius, y - radius, radius * 2f, radius * 2f, (unit) -> {
        if(!unit.isFlying()) unit.damagePierce(type.crushDamage * 5f * state.rules.unitDamage(team));
      });
      damageTimer -= 5f;
    }
  }

  @Annotations.Replace
  @Override
  public float deltaX(){
    return asType().deceiveAccurateDelay ? vel.x * asType().deceiveMulti : deltaX;
  }

  @Annotations.Replace
  @Override
  public float deltaY(){
    return asType().deceiveAccurateDelay ? vel.y * asType().deceiveMulti : deltaY;
  }

}
