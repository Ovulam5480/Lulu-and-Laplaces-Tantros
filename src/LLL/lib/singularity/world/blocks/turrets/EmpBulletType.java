package LLL.lib.singularity.world.blocks.turrets;

import LLL.lib.singularity.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class EmpBulletType extends BulletType{
  public float empDamage;
  public float empRange;

  public EmpBulletType(){
  }

  public EmpBulletType(float speed, float damage){
    this.speed = speed;
    this.damage = damage;
  }

  @Override
  public void hitEntity(Bullet b, Hitboxc entity, float health){
    super.hitEntity(b, entity, health);
    if(empDamage > 0){
      if(entity instanceof Unit unit){
        Sgl.empHealth.empDamage(unit, empDamage, false);
      }
    }
  }

  @Override
  public void createSplashDamage(Bullet b, float x, float y){
    super.createSplashDamage(b, x, y);
    if(empRange > 0 && empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, empRange, u -> {
      Sgl.empHealth.empDamage(u, empDamage, false);
    });
  }
}
