package LLL.world.neoplasm.production;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

public class NeoplasmMurex extends NeoplasmPholas{
  public float chanceDeflect = 20f;
  public float damageTick = 10f;
  public float maxHealOnce = 20f;

  public NeoplasmMurex(String name){
    super(name);

    drillTime = 50f;
    size = 2;
    health = 1200;
    armor = 15f;
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return super.canPlaceOn(tile, team, rotation) || canDamageOn(tile, team);
  }

  public boolean canDamageOn(Tile tile, Team team){
    for(Point2 edge : getEdges()){
      Tile t = Vars.world.tile(tile.x + edge.x, tile.y + edge.y);

      if(t != null && t.build != null && t.build.team != team){
        return true;
      }
    }

    return false;
  }

  public class NeoplasmMurexBuild extends NeoplasmPholasBuild{
    public Seq<Building> targets = new Seq<>();
    public float suicideTimer = 0f;

    @Override
    public void onProximityUpdate(){//todo 在周围放置时并不触发
      super.onProximityUpdate();

      targets.clear();
      for(Point2 edge : getEdges()){
        Building t = Vars.world.build(tile.x + edge.x, tile.y + edge.y);

        if(t != null && t.team != team){
          targets.add(t);
        }
      }
    }

    @Override
    public void updateTile(){
      if(!targets.isEmpty()){
        warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 60f);

        float damageAmount = warmup * damageTick * edelta();

        targets.each(t -> t.damage(damageAmount));
      }else if(drillItems.total > 0){
        super.updateTile();
      }else{
        suicideTimer += delta();

        if(suicideTimer >= 160f){
          suicide();
        }
      }
    }

    @Override
    public boolean collision(Bullet bullet){
      super.collision(bullet);

      if(chanceDeflect > 0f){
        if(bullet.vel.len() <= 0.1f || !bullet.type.reflectable) return true;
        if(!Mathf.chance(chanceDeflect / bullet.damage())) return true;

        bullet.trns(-bullet.vel.x, -bullet.vel.y);
        bullet.vel.inv();

        bullet.owner = this;
        bullet.team = team;
        bullet.time += 1f;

        return false;
      }

      return true;
    }
  }
}
