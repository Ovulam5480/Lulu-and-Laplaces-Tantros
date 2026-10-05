package LLL.entities.types;

import LLL.entities.ability.drawer.*;
import LLL.lib.singularity.graphic.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;

import static mindustry.Vars.*;

public class SpongeUnitTupe extends OvulamNeoplasmUnitType{
  public boolean hasTailFlagellum = true;

  private float totalInterval = 0;
  private Cons<Weapon> weaponCons;
  private BulletType buttleType;
  private final Seq<Weapon> spreadWeapon = new Seq<>();

  public SpongeUnitTupe(String name){
    super(name);
    rotateSpeed = 2;

    drawCell = false;
    constructor = UnitTypes.flare.constructor;
    flying = true;
    wobble = false;

    drawSoftShadow = false;
  }

  public void shootSpreadWeapon(int shots, float spread, float shotInterval){
    spreadWeapon.add(new Weapon(){{
      shoot = new ShootSpread(shots, spread){{
        firstShotDelay = totalInterval;
      }};
    }});

    totalInterval += shotInterval;
  }

  public void setWeaponCons(Cons<Weapon> weaponCons){
    this.weaponCons = weaponCons;
  }

  public void setButtleType(BulletType buttleType){
    this.buttleType = buttleType;
  }

  @Override
  public void init(){
    spreadWeapon.each(w -> {
      w.reload = totalInterval;
      weaponCons.get(w);

      if(buttleType != null){
        w.bullet = buttleType;
      }
    });

    weapons.addAll(spreadWeapon);
    weapons.each(w -> {
      w.bullet.shootEffect = w.bullet.smokeEffect = Fx.none;//todo
    });

    super.init();

    engines.clear();

    if(hasTailFlagellum){
      abilities.add(new TailFlagellumDrawer(hitSize * 0.7f));
    }
  }

  @Override
  public void drawBody(Unit unit){
    Draw.z(Layer.flyingUnit + 2);
    Draw.color(Pal.remove);
    Tmp.c2.set(Draw.getColor()).a(0.1f);

    float ux = unit.x(), uy = unit.y();
    float radius = unit.hitSize * 0.7f;

    Draw.alpha(0.8f);
    SglDraw.gradientPoly(ux, uy, Lines.circleVertices(radius) * 2, radius,
      Draw.getColor(), ux, uy, -radius * 0.3f, Tmp.c2, 0);
    SglDraw.gradientPoly(ux, uy, 12, radius * 0.0001f,
      Draw.getColor(), ux, uy, radius * 0.2f * (1 + Mathf.absin(20, 0.4f)), Tmp.c2, 0);
    Draw.alpha(1);
    Lines.poly(ux, uy, Lines.circleVertices(radius) * 2, radius + 0.5f);

    Draw.alpha(0.1f);
    Fill.circle(ux, uy, radius * 0.7f);
    Draw.reset();
  }

  public void drawShadow(Unit unit){
    float e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime);
    float x = unit.x + shadowTX * e, y = unit.y + shadowTY * e;
    Floor floor = world.floorWorld(x, y);

    float dest = floor.canShadow ? 1f : 0f;
    unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
    Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

    Drawf.shadow(x, y, unit.hitSize * 4 * 0.7f, 0.5f);
    Draw.reset();
  }
}
