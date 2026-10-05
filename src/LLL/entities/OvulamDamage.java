package LLL.entities;

import LLL.util.*;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class OvulamDamage{
  public static void RingDamage(Team team, int tileX, int tileY, int radius, float damage, boolean air, boolean ground, boolean building, Effect effect){
    for(Point2 point2 : MathUtil.getPixelCircle(radius)){
      Tile tile = world.tile(tileX + point2.x, tileY + point2.y);

      if(tile == null) continue;

      if(ground && building){
        if(tile.build != null && (team == null || team != tile.team())){
          tile.build.damage(damage);
        }
      }

      effect.at(tile.worldx(), tile.worldy());
    }

    int inner2 = radius == 0 ? 0 : (2 * radius - 1) * (2 * radius - 1) * 64;
    int outer2 = (2 * radius + 1) * (2 * radius + 1) * 64;

    Units.nearbyEnemies(team, (tileX - radius) * 8, (tileY - radius) * 8, radius * 16, radius * 16, u -> {
      float dst24 = u.dst2(tileX * 8, tileY * 8) * 4;

      if((dst24 >= inner2 && dst24 < outer2)
        && u.hittable()
        && u.checkTarget(air, ground)){
        u.damage(damage);
      }
    });
  }
}
