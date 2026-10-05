package LLL.entities;

import LLL.content.*;
import LLL.entities.gen.*;
import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class Momentum{
  public static float kineticDamageMulti = 0.5f; //todo 还得继续改可能

  public static float velocityThreshold = 1f;
  //法向动量传递系数
  public static float normalMomentumTransferCoefficient = 0.5f;

  private static Vec2 tmp = new Vec2(), tmp1 = new Vec2(), tmp2 = new Vec2();
  private static ObjectMap<Building, Vec2> velocitys = new ObjectMap<>();
  private static QuadTree<Building> marked;

  public static void init(){
    Events.on(EventType.WorldLoadEvent.class, e -> {
      marked = new QuadTree<>(new Rect(0, 0, Vars.world.width() * 8, Vars.world.height() * 8));
    });
  }

  public static void exertVelocity(HitboxEntityCollisionc unit){
    velocitys.clear();

    float mass = getMass(unit);
    Vec2 velocity = tmp.set(unit.vel());

    Point2 d8e = new Point2(Mathf.sign(velocity.x >= 0), Mathf.sign(velocity.y >= 0));
    tmp1.set(Vars.world.width() * 8, Vars.world.height() * 8).scl(Mathf.num(velocity.x < 0), Mathf.num(velocity.y < 0));

    unit.hitbox(Tmp.r1);
    Tmp.r1.grow(0.1f);

    for(int i = Mathf.round(Tmp.r1.x / tilesize); i <= Mathf.round((Tmp.r1.x + Tmp.r1.width) / tilesize); i++){
      for(int j = Mathf.round(Tmp.r1.y / tilesize); j <= Mathf.round((Tmp.r1.y + Tmp.r1.height) / tilesize); j++){
        if(Tmp.v1.set(i * 8, j * 8).sub(unit).dot(d8e.x, d8e.y) <= 0) continue;

        Building b = Vars.world.build(i, j);
        if(b == null) continue;

        applyCollision(velocity, mass, b, Math.abs(b.x - unit.x()) > Math.abs(b.y - unit.y()));
      }
    }

    for(Building key : velocitys.keys()){
      marked.insert(key);
    }

    while(marked.totalObjects != 0){
      Building current = marked.find(0, 0, Vars.world.width() * 8, Vars.world.height() * 8, b -> true);
      Building best = null;

      while(current != null){
        best = current;
        tmp2.set(best).add(best.hitSize() / 2f * d8e.x, best.hitSize() / 2f * d8e.y);

        Building finalBest = best;
        current = marked.find(Math.min(tmp1.x, tmp2.x), Math.min(tmp1.y, tmp2.y),
          Math.abs(tmp1.x - tmp2.x), Math.abs(tmp1.y - tmp2.y), b -> b != finalBest);
      }

      allocationMomentum(best);
      marked.remove(best);
    }

    applyVelocitys();
  }

  public static float getMass(Entityc entity){
    if(entity instanceof Healthc he){
      return Mathf.lerp(he.health(), he.maxHealth(), 0.2f);
    }else if(entity instanceof Hitboxc hi){
      return 5 * Mathf.sqr(hi.hitSize());
    }else{
      return 1000;
    }
  }

  public static void exertMomentum(Building target, float momentum, float angle){
    exertVelocity(target, momentum / getMass(target), angle);
  }

  public static void exertVelocity(Building target, float velocity, float angle){
    velocitys.clear();

    Vec2 initialVelocity = new Vec2().trns(angle, velocity);
    Point2 d8e = new Point2(Mathf.sign(initialVelocity.x >= 0), Mathf.sign(initialVelocity.y >= 0));
    velocitys.put(target, initialVelocity);

    marked.insert(target);

    while(marked.totalObjects != 0){
      Building current = marked.find(0, 0, Vars.world.width() * 8, Vars.world.height() * 8, b -> true);
      Building best = null;

      while(current != null){
        best = current;
        tmp2.set(best).add(best.hitSize() / 2f * d8e.x, best.hitSize() / 2f * d8e.y);

        Building finalBest = best;
        current = marked.find(Math.min(tmp1.x, tmp2.x), Math.min(tmp1.y, tmp2.y),
          Math.abs(tmp1.x - tmp2.x), Math.abs(tmp1.y - tmp2.y), b -> b != finalBest);
      }

      allocationMomentum(best);
      marked.remove(best);
    }

    applyVelocitys();
  }

  private static void applyVelocitys(){
    velocitys.each((b, vel) -> {
      float len = vel.len();
      if(len < velocityThreshold || !b.canPickup()){
        b.damage(len * len * getMass(b) * kineticDamageMulti);
        return;
      }

      BlockUnitc unit = (BlockUnitc)OvulamUnitTypes.inertiaBuildUnit.create(b.team);
      unit.set(b);
      unit.tile(b);
      unit.vel().set(vel);
      unit.add();

      b.tile.remove();
      b.remove();
    });
  }

  private static void allocationMomentum(Building A){
    if(!velocitys.containsKey(A)){
      velocitys.put(A, new Vec2());
    }
    Vec2 momentum = velocitys.get(A);
    Point2 d8e = new Point2(Mathf.sign(momentum.x >= 0), Mathf.sign(momentum.y >= 0));

    Seq<Building> bsx = new Seq<>();
    Seq<Building> bsy = new Seq<>();

    for(Building b : A.proximity){
      if(Tmp.v1.set(b).sub(A).dot(d8e.x, d8e.y) > 0){
        if(Math.abs(b.x - A.x) > Math.abs(b.y - A.y)){
          bsx.add(b);
        }else{
          bsy.add(b);
        }
      }
    }

    for(Building B : bsx){
      Tmp.v1.set(velocitys.get(B, Vec2.ZERO));
      if(applyCollision(momentum, getMass(A), B, true) && Math.abs(Tmp.v1.sub(velocitys.get(B)).x) > velocityThreshold){
        if(!marked.objects.contains(B)){
          marked.insert(B);
        }
      }
    }

    for(Building B : bsy){
      Tmp.v1.set(velocitys.get(B, Vec2.ZERO));
      if(applyCollision(momentum, getMass(A), B, false) && Math.abs(Tmp.v1.sub(velocitys.get(B)).y) > velocityThreshold){
        if(!marked.objects.contains(B)){
          marked.insert(B);
        }
      }
    }
  }

  private static boolean applyCollision(Vec2 velocity, float mass1, Building B, boolean isX){
    if(!velocitys.containsKey(B)){
      velocitys.put(B, new Vec2());
    }
    Vec2 velocity2 = velocitys.get(B);

    float v1e, v2e, v1, v2;

    if(isX){
      v1e = velocity.x;
      v2e = velocity2.x;
      v1 = velocity.y;
      v2 = velocity2.y;
    }else{
      v1e = velocity.y;
      v2e = velocity2.y;
      v1 = velocity.x;
      v2 = velocity2.x;
    }

    float mass2 = getMass(B);

    boolean collided = false;
    if(v1e > 0 ? v2e < v1e : v1e < v2e){
      fullElasticCollision(v1e, v2e, mass1, mass2, (v12, v22) -> {
        if(isX){
          velocity.x = v12;
          velocity2.x = v22;
        }else{
          velocity.y = v12;
          velocity2.y = v22;
        }
      });
      collided = true;
    }

    if(v1 > 0 ? v2 < v1 : v1 < v2){
      elasticCollision(v1, v2, mass1, mass2, normalMomentumTransferCoefficient, (v12, v22) -> {
        if(isX){
          velocity.y = v12;
          velocity2.y = v22;
        }else{
          velocity.x = v12;
          velocity2.x = v22;
        }
      });
      collided = true;
    }

    return collided;
  }

  private static void fullElasticCollision(float v1, float v2, float mass1, float mass2, Floatc2 cons){
    float mass = mass1 + mass2;

    float v12 = (mass1 - mass2) / mass * v1 + 2 * mass2 / mass * v2;
    float v22 = 2 * mass1 / mass * v1 + (mass2 - mass1) / mass * v2;

    cons.get(v12, v22);
  }

  private static void elasticCollision(float v1, float v2, float mass1, float mass2, float coefficient, Floatc2 cons){
    float mass = mass1 + mass2;

    float v12 = ((mass1 - coefficient * mass2) * v1 + (1 + coefficient) * mass2 * v2) / mass;
    float v22 = ((mass2 - coefficient * mass1) * v2 + (1 + coefficient) * mass1 * v1) / mass;

    cons.get(v12, v22);
  }
}
