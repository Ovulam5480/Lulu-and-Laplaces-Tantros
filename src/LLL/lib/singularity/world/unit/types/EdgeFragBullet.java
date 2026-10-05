package LLL.lib.singularity.world.unit.types;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.*;
import arc.math.geom.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class EdgeFragBullet extends BulletType{
  {
    damage = 80;
    splashDamage = 40;
    splashDamageRadius = 24;
    speed = 4;
    hitSize = 3;
    lifetime = 120;
    despawnHit = true;
    hitEffect = SglFx.diamondSpark;
    hitColor = SglDrawConst.matrixNet;

    collidesTiles = false;

    homingRange = 160;
    homingPower = 0.075f;

    trailColor = SglDrawConst.matrixNet;
    trailLength = 25;
    trailWidth = 3f;
  }

  @Override
  public void draw(Bullet b){
    super.draw(b);
    SglDraw.drawDiamond(b.x, b.y, 10, 4, b.rotation());
  }

  @Override
  public void update(Bullet b){
    super.update(b);

    b.vel.lerpDelta(Vec2.ZERO, 0.04f);
  }
}
