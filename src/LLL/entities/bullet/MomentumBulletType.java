package LLL.entities.bullet;

import LLL.entities.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class MomentumBulletType extends BasicBulletType{
  public float impulse;

  public MomentumBulletType(){
    this(10000, 10);
  }

  public MomentumBulletType(float impulse, float speed){
    super(speed, impulse / 100);
    shieldDamageMultiplier = 10f;

    this.impulse = impulse;
  }

  @Override
  public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct){
    super.hitTile(b, build, x, y, initialHealth, direct);

    Momentum.exertMomentum(build, impulse, b.rotation());
  }

  @Override
  public void hitEntity(Bullet b, Hitboxc entity, float health){
    super.hitEntity(b, entity, health);

    if(entity instanceof Velc v){
      float mass = Momentum.getMass(entity);

      Tmp.v1.trns(b.rotation(), impulse / mass);

      v.vel().add(Tmp.v1);
    }
  }
}
