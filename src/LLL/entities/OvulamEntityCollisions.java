package LLL.entities;

import LLL.entities.gen.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class OvulamEntityCollisions{
  EntityCollisions collisions;
  private static final float seg = 1f, maxDelta = 1000f;
  private static final Rect r1 = new Rect(), r2 = new Rect(), r3 = new Rect(), tmp = new Rect();
  private static float fallDamageMulti = 6f;

  public static void move(Hitboxc entity, float deltax, float deltay, EntityCollisions.SolidPred solidCheck){
    //check for NaN
    if((Math.abs(deltax) < 0.0001f && Math.abs(deltay) < 0.0001f) || deltax != deltax || deltay != deltay) return;

    deltax = Mathf.clamp(deltax, -maxDelta, maxDelta);
    deltay = Mathf.clamp(deltay, -maxDelta, maxDelta);

    boolean movedx = false;

    int r = Math.round(entity.hitSize() / tilesize / 2f);

    while(Math.abs(deltax) > 0 || !movedx){
      movedx = true;
      moveDelta(entity, Math.min(Math.abs(deltax), seg) * Mathf.sign(deltax), 0, r, true, solidCheck);

      if(Math.abs(deltax) >= seg){
        deltax -= seg * Mathf.sign(deltax);
      }else{
        deltax = 0f;
      }
    }

    boolean movedy = false;

    while(Math.abs(deltay) > 0 || !movedy){
      movedy = true;
      moveDelta(entity, 0, Math.min(Math.abs(deltay), seg) * Mathf.sign(deltay), r, false, solidCheck);

      if(Math.abs(deltay) >= seg){
        deltay -= seg * Mathf.sign(deltay);
      }else{
        deltay = 0f;
      }
    }
  }

  public static void moveDelta(Hitboxc entity, float deltax, float deltay, int r, boolean x, EntityCollisions.SolidPred solidCheck){
    entity.hitbox(r1);
    entity.hitbox(r2);
    r1.x += deltax;
    r1.y += deltay;

    r3.set(r1);
    r3.grow(-2f);

    boolean overlaped = false;
    boolean hasStaticSolid = false;
    Seq<Building> solids = new Seq<>();

    int tilex = Math.round((r1.x + r1.width / 2) / tilesize), tiley = Math.round((r1.y + r1.height / 2) / tilesize);

    for(int dx = -r; dx <= r; dx++){
      for(int dy = -r; dy <= r; dy++){
        int wx = dx + tilex, wy = dy + tiley;
        if(solidCheck.solid(wx, wy)){
          tmp.setSize(tilesize).setCenter(wx * tilesize, wy * tilesize);

          if(tmp.overlaps(r1)){
            Vec2 v = Geometry.overlap(r1, tmp, x);
            r1.x += v.x;
            r1.y += v.y;

            overlaped = true;

            if(tmp.overlaps(r3)){
              if(Vars.world.build(wx, wy) == null){
                hasStaticSolid = true;
              }else{
                solids.add(Vars.world.build(wx, wy));
              }
            }
          }
        }
      }
    }

    if(overlaped && entity instanceof HitboxEntityCollisionc unit && entity instanceof Unitc u){
      if(hasStaticSolid){
        return;
      }else if(!solids.isEmpty()){
        float totalHealth = solids.sumf(Building::health);

        if(unit.health() * fallDamageMulti > totalHealth){
          solids.each(Building::kill);
          unit.damage(totalHealth / fallDamageMulti, false);
        }else{
          unit.kill();
          solids.each(b -> b.damage(b.health / totalHealth * unit.health() * fallDamageMulti));
        }
        return;
      }else{
        Momentum.exertVelocity(unit);
      }
    }

    entity.trns(r1.x - r2.x, r1.y - r2.y);
  }
}
