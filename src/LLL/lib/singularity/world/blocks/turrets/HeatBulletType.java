package LLL.lib.singularity.world.blocks.turrets;

import LLL.lib.singularity.contents.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class HeatBulletType extends BulletType{
  public float melDamageScl = 0.4f;
  public float maxExDamage = -1;
  public float meltDownTime = 10;

  @Override
  public void hitEntity(Bullet b, Hitboxc entity, float health){
    super.hitEntity(b, entity, health);
    if(entity instanceof Unit u){
      float mel = u.getDuration(OtherContents.meltdown);

      u.damage(Math.min(mel * melDamageScl, maxExDamage < 0 ? damage : maxExDamage));

      u.apply(OtherContents.meltdown, mel + meltDownTime);
    }
  }

  @Override
  public void createSplashDamage(Bullet b, float x, float y){
    super.createSplashDamage(b, x, y);
    Units.nearbyEnemies(b.team, x, y, splashDamageRadius, u -> {
      float mel = u.getDuration(OtherContents.meltdown);

      u.damage(Math.min(mel * melDamageScl, maxExDamage < 0 ? splashDamage : maxExDamage));

      u.apply(OtherContents.meltdown, mel + meltDownTime);
    });
  }
}
