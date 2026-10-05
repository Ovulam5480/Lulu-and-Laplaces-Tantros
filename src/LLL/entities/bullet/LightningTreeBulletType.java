package LLL.entities.bullet;

import LLL.entities.*;
import arc.graphics.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class LightningTreeBulletType extends BulletType{
  public float lightningRange = 120f;
  public int amount = 15;
  //    public Color lightningRoot = Color.pink;
//    public Color lightningLeaf = Color.cyan;
  public Color lightningRoot = Items.surgeAlloy.color;
  public Color lightningLeaf = Items.surgeAlloy.color;

  public LightningTreeBulletType(){
    damage = 1f;
    speed = 0f;
    //todo
    lifetime = 1000;
    despawnEffect = Fx.none;
    hitEffect = Fx.hitLancer;
    keepVelocity = false;
    hittable = false;
    status = StatusEffects.shocked;
  }

  @Override
  protected float calculateRange(){
    return lightningRange;
  }

  @Override
  public float estimateDPS(){
    return damage * lightningRange / 10 * (amount + amount * 0.5f);
  }

  @Override
  public void draw(Bullet b){
  }

  @Override
  public void init(Bullet b){
    LightningTree.create(b, lightningRoot, lightningLeaf, damage, amount, lightningRange);
  }
}
