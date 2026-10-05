package LLL.lib.singularity.world.blocks.turrets;

import arc.math.*;
import mindustry.content.*;
import mindustry.gen.*;

public class EmpArtilleryBulletType extends BasicEmpBulletType{
  public float trailMult = 1f, trailSize = 4f;

  public EmpArtilleryBulletType(float speed, float damage, String bulletSprite){
    super(speed, damage, bulletSprite);
    collidesTiles = false;
    collides = false;
    collidesAir = false;
    scaleLife = true;
    hitShake = 1f;
    hitSound = Sounds.explosion;
    hitEffect = Fx.flakExplosion;
    shootEffect = Fx.shootBig;
    trailEffect = Fx.artilleryTrail;

    //default settings:
    shrinkX = 0.15f;
    shrinkY = 0.63f;
    shrinkInterp = Interp.slope;
  }

  public EmpArtilleryBulletType(float speed, float damage){
    this(speed, damage, "shell");
  }

  public EmpArtilleryBulletType(){
    this(1f, 1f, "shell");
  }

  @Override
  public void update(Bullet b){
    super.update(b);

    if(b.timer(0, (3 + b.fslope() * 2f) * trailMult)){
      trailEffect.at(b.x, b.y, b.fslope() * trailSize, backColor);
    }
  }
}
