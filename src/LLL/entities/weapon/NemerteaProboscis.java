package LLL.entities.weapon;

import LLL.content.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

public class NemerteaProboscis extends Weapon{
  public static UnitType inertiaBuildUnit = OvulamUnitTypes.inertiaBuildUnit;

  public float velocity;
  public float proboscisRange = 40 * 8f;

  public float maxPayloadMass = 3000;
  public float payloadSearchRadius = 40f;
  public float searchInterval = 60f;

  public float retractSpeed = 0.1f;

  public float healOwner = 1f;
  public boolean flyingStart = false;

  public Effect absorbPayloadEffect = OvulamFx.absorbPayloadNearby;

  public NemerteaProboscis(){
    mountType = Proboscis::new;

    x = 0;
    mirror = false;
  }

  @Override
  public void init(){
    super.init();

    float decay = 1 - inertiaBuildUnit.drag;
    float logED = (float)Math.log(decay);

    if(velocity != 0 && proboscisRange == 0){
      proboscisRange = -velocity / logED;
    }else if(velocity == 0 && proboscisRange != 0){
      velocity = -proboscisRange * logED;
    }
  }

  @Override
  public void update(Unit unit, WeaponMount mount){
    if(mount instanceof Proboscis p){
      if(p.owner != null && p.owner.dead()){
        p.owner = null;
        p.isRetract = false;
      }

      if(p.owner == null){
        p.searchTimer += Time.delta;

        if(p.searchTimer >= searchInterval){
          absorbPayloadNearby(unit, p);
          p.searchTimer = 0;
        }
        return;
      }

      InertiaDamagec projectile = p.owner;

      if(projectile.isAdded()){
        if(p.isRetract){
          projectile.vel().add(Tmp.v1.set(unit).sub(projectile).setLength(retractSpeed));

          if(projectile.within(unit, (projectile.hitSize() + unit.hitSize) / 2f + 8)){
            p.isRetract = false;

            projectile.vel().setZero();
            projectile.remove();

            p.reload = reload;
            return;
          }
        }else if(projectile.vel().isZero(0.1f)){
          p.isRetract = true;
        }else if(flyingStart){
          float vel = projectile.vel().len();

          float elevationChange = 2 * (vel / velocity) - 1;

          p.owner.elevation(Math.max(p.owner.elevation() + elevationChange * 0.2f * Time.delta, 0));
        }

        p.reload = 999999;
        return;
      }else{
        p.owner.tile().heal(healOwner * Time.delta);
      }
    }

    super.update(unit, mount);
  }

  //todo Call
  public void absorbPayloadNearby(Unit unit, Proboscis proboscis){
    Vars.indexer.eachBlock(unit.team, unit.x, unit.y, payloadSearchRadius, b -> b.block.outputsPayload, b -> {
      if(proboscis.owner == null && b.getPayload() instanceof BuildPayload bp && Momentum.getMass(bp.build) <= maxPayloadMass){
        Payload payload = b.takePayload();
        absorbPayloadEffect.at(payload.x(), payload.y(), payload.size() / 8);

        handleOwner(unit, proboscis, bp.build);
      }
    });
  }

  @Override
  public float range(){
    return proboscisRange;
  }

  @Override
  public void draw(Unit unit, WeaponMount mount){
    super.draw(unit, mount);

    if(mount instanceof Proboscis p && p.owner != null && p.owner.isAdded()){
      Lines.line(p.owner.x(), p.owner.y(), unit.x(), unit.y());
    }
  }

  public void handleOwner(Unit unit, Proboscis proboscis, Building building){
    InertiaDamagec inertia = (InertiaDamagec)inertiaBuildUnit.create(unit.team);

    inertia.tile(building);
    inertia.canDrop(false);

    building.tile = Vars.emptyTile;

    proboscis.owner = inertia;
    proboscis.reload = reload;
  }

  @Override
  protected void bullet(Unit unit, WeaponMount mount, float xOffset, float yOffset, float angleOffset, Mover mover){
    if(!unit.isAdded()) return;

    if(mount instanceof Proboscis p && (p.owner == null || p.owner.isAdded())){
      return;
    }

    mount.charging = false;
    float
      xSpread = Mathf.range(xRand),
      ySpread = Mathf.range(yRand),
      weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
      mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
      mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
      bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset + ySpread),
      bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset + ySpread),
      shootAngle = bulletRotation(unit, mount, bulletX, bulletY) + angleOffset,
      angle = shootAngle + Mathf.range(inaccuracy + bullet.inaccuracy);

    Proboscis p = (Proboscis)mount;
    p.isRetract = false;
    p.owner.set(bulletX, bulletY);
    p.owner.add();
    p.owner.vel().trns(angle, velocity);
    if(flyingStart){
      p.owner.elevation(0.1f);
    }

    if(!continuous){
      shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax), shootSoundVolume);
    }else{
      initialShootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax), shootSoundVolume);
    }

    if(mount.allowShootEffects){
      ejectEffect.at(mountX, mountY, angle * Mathf.sign(this.x));
      bullet.shootEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
      bullet.smokeEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
    }

    unit.vel.add(Tmp.v1.trns(shootAngle + 180f, bullet.recoil));
    Effect.shake(shake, shake, bulletX, bulletY);
    mount.recoil = 1f;
    if(recoils > 0){
      mount.recoils[mount.barrelCounter % recoils] = 1f;
    }
    mount.heat = 1f;
  }

  public static boolean canPick(Unit unit, BuildPayload payload){
    float mass = Momentum.getMass(payload.build);

    for(WeaponMount mount : unit.mounts){
      if(mount instanceof Proboscis p && ((NemerteaProboscis)p.weapon).maxPayloadMass > mass){
        return true;
      }
    }

    return false;
  }

  public static class Proboscis extends WeaponMount{
    public @Nullable InertiaDamagec owner;
    public boolean isRetract = false;
    public float searchTimer;

    public Proboscis(Weapon weapon){
      super(weapon);
    }

  }
}
