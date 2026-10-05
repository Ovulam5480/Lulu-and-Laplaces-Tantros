package LLL.entities.bullet;

import LLL.entities.*;
import LLL.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;

public class PulseBulletType extends BulletType{
  public int radiusTile;
  public Effect tileEffect = OvulamFx.neoplasmPulse;

  public PulseBulletType(int radiusTile){
    this.radiusTile = radiusTile;

    instantDisappear = true;
    hitEffect = despawnEffect = Fx.none;
  }

  @Override
  public void init(Bullet b){
    super.init(b);

    int tileX = b.tileX(), tileY = b.tileY();
    Team team = b.team;
    for(int i = 0; i < radiusTile; i++){
      int finalI = i;

      Time.run(Mathf.pow(i, 0.8f) * 5, () -> {
        OvulamDamage.RingDamage(team, tileX, tileY, finalI, damage, collidesAir, collidesGround, collidesTiles, tileEffect);
      });
    }
  }
}
