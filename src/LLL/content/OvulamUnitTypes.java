package LLL.content;

import LLL.entities.ability.*;
import LLL.entities.ability.drawer.*;
import LLL.entities.ai.*;
import LLL.entities.gen.*;
import LLL.entities.types.*;
import LLL.entities.weapon.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class OvulamUnitTypes{
  public static @Annotations.EntityDef(value = {Unitc.class, MovableBuildUnitc.class}) UnitType boostBuildUnit, neoplasmTraixBuildUnit;
  public static @Annotations.EntityDef(value = {Unitc.class, MovableBuildUnitc.class, RollCubeUnitc.class}) UnitType rollingBuildUnit;
  public static @Annotations.EntityDef(value = {Unitc.class, MovableBuildUnitc.class, InertiaDamagec.class, HitboxEntityCollisionc.class}) UnitType inertiaBuildUnit;
  public static @Annotations.EntityDef(value = {Unitc.class, SeizePayloadc.class}) UnitType amphiprioninae;
  public static @Annotations.EntityDef(value = {Unitc.class, PacketUnitc.class}) UnitType daw;
  public static @Annotations.EntityDef(value = {Unitc.class, PacketUnitc.class, FrameBuilderc.class}) UnitType ddd;

  public static UnitType manganeseTorpedo, cobaltTorpedo;

  public static UnitType trophosomeCalcium, trophosomePhosphorus, trophosomeSilicon, trophosomeFerrum;
  public static ObjectMap<Item, UnitType> trophosomeUnitTypes = new ObjectMap<>();
  public static UnitType coeloblastula, amphiblastula, parenchymella, trichimella, settledlarva, juvenileSponge;

  public static UnitType pilidium, desorsLarva, juvenileNemertean;

  //public static @Annotations.EntityDef(value = {Unitc.class, TentacleOwnerc.class}) UnitType head;

  public static void load(){
    daw = EntityRegistry.content("daw", FrameBuilderPacketUnitUnit.class, name -> new PacketUnitType(name){{
      health = 500;
      hitSize = 16f;

      packetDragResist = 1f;

      speed = 4.85f;
      accel = 0.1f;
      drag = 0.05f;
      buildSpeed = 1f;
      flying = true;
    }});

    manganeseTorpedo = new MissileUnitType("manganese-torpedo"){{
      speed = 2.5f;
      maxRange = 6f;
      lifetime = 300f;
      hitSize = 10f;
      missileAccelTime = 180f;

      weapons.add(new Weapon(){{
        shootCone = 360f;
        mirror = false;
        reload = 1f;
        shootOnDeath = true;
        bullet = new ExplosionBulletType(300f, 100f);
      }});
    }};

    cobaltTorpedo = new MissileUnitType("cobalt-torpedo"){{
      speed = 2.5f;
      maxRange = 6f;
      lifetime = 400f;
      hitSize = 10f;
      missileAccelTime = 180f;

      weapons.add(new Weapon(){{
        shootCone = 360f;
        mirror = false;
        reload = 1f;
        shootOnDeath = true;
        bullet = new ExplosionBulletType(800f, 150f);
      }});
    }};

    neoplasmTraixBuildUnit = EntityRegistry.content("flying-build-unit", MovableBuildUnitUnit.class, name -> new UnitType(name){{
      health = 100;
      hidden = true;

      aiController = AIController::new;
      controller = u -> new AIController();

      flying = true;
      wobble = false;
      useUnitCap = false;

      logicControllable = playerControllable = false;

      speed = 0.5f;
      drag = 0.015f;

      envEnabled = Env.any;
      envDisabled = Env.none;
    }});

    boostBuildUnit = EntityRegistry.content("boost-build-unit", MovableBuildUnitUnit.class, name -> new UnitType(name){{
      health = 100;
      canBoost = true;
      wobble = false;
      useUnitCap = false;

      speed = 0.5f;
      hidden = true;

      riseSpeed = 0.004f;
      descentSpeed = 0.004f;

      physics = false;

      controller = u -> new FlyingBuildUnitAI();
    }});

    inertiaBuildUnit = EntityRegistry.content("inertia-build-unit", HitboxEntityCollisionInertiaDamageMovableBuildUnitUnit.class, name -> new UnitType(name){{
      speed = 0;
      hidden = true;
      useUnitCap = false;

      canBoost = true;

      drag = 0.05f;
    }});

    amphiprioninae = EntityRegistry.content("amphiprioninae", SeizePayloadUnit.class, name -> new UnitType(name){{
      flying = true;
      aiController = SeizePayloadAI::new;
      health = 3000f;

      payloadCapacity = (3 * 3) * tilePayload;
    }});

//        rollingBuildUnit = EntityRegistry.content("rolling-build-unit", RollCubeUnitMovableBuildUnitUnit.class, name -> new RollCubeUnitType(name){{
//            deceiveAccurateDelay = true;
//            deceiveMulti = 2f;
//            useUnitCap = false;
//
//            controller = u -> new FlyingBuildUnitAI();
//        }});

    amphiblastula = new SpongeUnitTupe("amphiblastula"){{
      range = 35 * 8;

      health = 1500;
      armor = 8;
      hitSize = 12;

      drag = 0.1f;

      speed = 1.4f;

      weapons.add(new Weapon(){{
        mirror = false;
        reload = 90;
        x = 0;
        shootSound = OvulamSounds.shootSounds.random();

        bullet = new BasicBulletType(12, 50){{
          hitSize = 5;
          width = 10;
          height = 13;
          knockback = 0.5f;

          lifetime = 280 / speed;

          frontColor = backColor = Pal.remove;
          trailInterval = 1f;
        }};
      }});

      abilities.add(new AmphiblastulaDrawer());
    }};

    parenchymella = new SpongeUnitTupe("parenchymella"){{
      range = 35 * 8;

      health = 4500;
      armor = 10;
      hitSize = 15;

      weapons.add(new Weapon(){{
        rotate = true;
        mirror = false;
        reload = 90;
        x = 0;
        shootSound = OvulamSounds.shootSounds.random();

        shoot = new ShootSpread(11, 15);

        bullet = new BasicBulletType(12, 60){{
          hitSize = 5;
          width = 10;
          height = 13;
          knockback = 0.5f;

          lifetime = 280 / speed;

          frontColor = backColor = Pal.remove;
        }};
      }});

      abilities.add(new ShockwaveAbility(120, 20, 80, 80, 80, 35, 35))
        .add(new ParenchymellaDrawer(hitSize * 0.7f + 0.75f, 4));
    }};

    trichimella = new SpongeUnitTupe("trichimella"){{
      range = 40 * 8;

      speed = 4;

      health = 3000;
      armor = 8;
      hitSize = 8;

      drag = 0.01f;

      circleTarget = true;
      omniMovement = false;
      rotateSpeed = 8f;
      circleTargetRadius = 160f;

      weapons.add(new Weapon(){{
        alwaysShooting = true;
        mirror = false;
        reload = 5;
        x = 0;
        shootSound = OvulamSounds.trichimellaShootSounds.random();

        bullet = new BasicBulletType(10, 60){{
          hitSize = 4;
          width = 7;
          height = 7;
          knockback = 0.5f;

          lifetime = 320 / speed;

          frontColor = backColor = Pal.remove;
        }};
      }}).add(new Weapon(){{
        controllable = false;
        display = false;
        rotate = true;
        shootCone = 361;

        shoot = new ShootPattern(){{
          firstShotDelay = 300f;
        }};

        reload = 300f;

        bullet = new EmptyBulletType();
        shootStatus = StatusEffects.fast;
      }});

      drawBody = false;
      hasTailFlagellum = false;

      abilities.add(new TrichimellaDrawer(2.5f, 0.7f))
        .add(new TailFlagellumDrawer(hitSize * 0.7f * 2f){{
          swingScl = 0.3f;
          lcsGniws = 10f;
          tailLength = 24f;
        }});
    }};

    settledlarva = new SpongeUnitTupe("settled-larva"){{
      range = 40 * 8;
      hasTailFlagellum = false;
      faceTarget = false;

      health = 12000;
      armor = 15;
      hitSize = 36;

      speed = 0.85f;

      shootSpreadWeapon(12, 30, 90);
      shootSpreadWeapon(16, 12, 90);
      shootSpreadWeapon(5, 3, 5);
      shootSpreadWeapon(5, 3, 5);
      shootSpreadWeapon(5, 3, 90);
      shootSpreadWeapon(20, 6, 90);

      setWeaponCons(w -> {
        w.x = 0;
        w.mirror = false;
        w.rotate = true;
        w.shootSound = OvulamSounds.shootSounds.random();
      });

      setButtleType(new BasicBulletType(7, 90){{
        pierceBuilding = true;
        pierceCap = 2;
        hitSize = 5;
        width = 10;
        height = 10;
        knockback = 0.5f;

        lifetime = 320 / speed;

        frontColor = backColor = Pal.remove;
      }});

      drawBody = false;

      abilities.add(new ShockwaveAbility(70, 100, 80, 80, 80, 35, 35))
        .add(new ParenchymellaDrawer(15 * 0.7f + 0.75f, 4, 20, -0.2f))
        .add(new ParenchymellaDrawer(hitSize * 0.7f + 0.75f, 6, 30, 0.2f))
        .add(new SettledlarvaDrawer())
        .add(new SpawnDeathAbility(parenchymella, 1, 0));
    }};

    juvenileSponge = new SpongeUnitTupe("juvenile-sponge"){{
      range = 55 * 8;

      health = 150000;
      armor = 25;
      hitSize = 90;
      faceTarget = false;

      speed = 0.4f;
      drawBody = false;
      hasTailFlagellum = false;

      shootSpreadWeapon(5, 5, 5);
      shootSpreadWeapon(6, 5, 5);
      shootSpreadWeapon(7, 5, 5);
      shootSpreadWeapon(8, 5, 5);
      shootSpreadWeapon(9, 5, 30);

      shootSpreadWeapon(16, 12f, 8);
      shootSpreadWeapon(8, 24f, 8);
      shootSpreadWeapon(12, 16f, 8);
      shootSpreadWeapon(8, 20f, 30);

      shootSpreadWeapon(15, 8, 8);
      shootSpreadWeapon(15, 8, 8);
      shootSpreadWeapon(15, 8, 12);
      shootSpreadWeapon(15, 8, 18);

      setWeaponCons(w -> {
        w.x = 0;
        w.mirror = false;
        w.rotate = true;
        w.shootSound = OvulamSounds.shootSounds.random();
      });

      setButtleType(new BasicBulletType(5, 200){{
        pierceBuilding = true;
        pierceCap = 2;
        hitSize = 5;
        width = 10;
        height = 10;
        knockback = 0.5f;

        lifetime = 55 * 8 / speed;

        frontColor = backColor = Pal.remove;
      }});

      float gearRadius = 21;
      Seq<Vec2> vec2s = Seq.with(new Vec2(-2, 1),
        new Vec2(0, 1),
        new Vec2(2, 1),
        new Vec2(-2, -1),
        new Vec2(0, -1),
        new Vec2(2, -1));

      for(int i = 0; i < 6; i++){
        float rotate = Mathf.sign(i % 2 == 1) * 0.25f;
        Vec2 vec2 = vec2s.get(i);

        float gx = vec2.x * gearRadius, gy = vec2.y * gearRadius;

        abilities.add(new ShockwaveAbility(120, 100, 30, 30, 15, 15, 15){{
          x = gx;
          y = gy;
          unitRadius = gearRadius;
          waveTime = 30f;
        }}).add(new JuvenileSpongePartDrawer(gearRadius - 1, 5, 30, rotate, gx, gy));
      }

      abilities.add(new JuvenileSpongeDrawer(gearRadius - 1, gearRadius));
    }};

    pilidium = new NemerteaUnitType("pilidium"){{
      health = 2000;
      hitSize = 24f;
      range = 30 * 8;
      constructor = UnitTypes.atrax.constructor;

      weapons.add(new NemerteaProboscis(){{
        proboscisRange = range + 80f;
        reload = 300;
      }});
    }};

    desorsLarva = new NemerteaUnitType("desors-larva"){{
      health = 15000;
      hitSize = 24f;

      range = 50 * 8;

      constructor = UnitTypes.atrax.constructor;
      weapons.add(new NemerteaProboscis(){{
        maxPayloadMass = 5000;
        proboscisRange = range + 80f;
        flyingStart = true;

        retractSpeed = 0.2f;
        //reload = 300;
      }});
    }};

    trophosomeCalcium = EntityRegistry.content("trophosome-calcium", CrawlUnit.class, name -> new NeoplasmUnitType(name){{
      controller = u -> new TrophosomeAI();
      speed = 1f;
      hitSize = 8f;
      health = 150;
      stepSoundVolume = 0.4f;
    }});

    trophosomePhosphorus = EntityRegistry.content("trophosome-phosphorus", CrawlUnit.class, name -> new NeoplasmUnitType(name){{
      controller = u -> new TrophosomeAI();
      speed = 1f;
      hitSize = 8f;
      health = 150;
      stepSoundVolume = 0.4f;
    }});

    trophosomeSilicon = EntityRegistry.content("trophosome-silicon", CrawlUnit.class, name -> new NeoplasmUnitType(name){{
      controller = u -> new TrophosomeAI();
      speed = 1f;
      hitSize = 8f;
      health = 150;
      stepSoundVolume = 0.4f;
    }});

    trophosomeFerrum = EntityRegistry.content("trophosome-ferrum", CrawlUnit.class, name -> new NeoplasmUnitType(name){{
      controller = u -> new TrophosomeAI();
      speed = 1f;
      hitSize = 8f;
      health = 150;
      stepSoundVolume = 0.4f;
    }});

    trophosomeUnitTypes.put(OvulamItems.calcium, trophosomeCalcium);
    trophosomeUnitTypes.put(OvulamItems.phosphorus, trophosomePhosphorus);
    trophosomeUnitTypes.put(Items.silicon, trophosomeSilicon);
    trophosomeUnitTypes.put(OvulamItems.ferrum, trophosomeFerrum);
  }
}
