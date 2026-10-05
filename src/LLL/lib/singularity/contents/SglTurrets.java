package LLL.lib.singularity.contents;

import LLL.lib.singularity.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.*;
import LLL.lib.singularity.world.blocks.turrets.*;
import LLL.lib.singularity.world.blocks.turrets.EmpBulletType;
import LLL.lib.singularity.world.draw.*;
import LLL.lib.singularity.world.draw.part.*;
import LLL.lib.singularity.world.meta.*;
import LLL.lib.singularity.world.particles.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.audio.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import universecore.world.lightnings.*;
import universecore.world.lightnings.generator.*;
import universecore.world.particles.models.*;

import static arc.math.Angles.*;
import static mindustry.Vars.*;
import static mindustry.entities.Damage.*;

public class SglTurrets implements ContentList{
  private static final Rand rand = new Rand();

  public static final String CONTAINER = "lightningContainer";
  /**
   * 碎冰
   */
  public static BulletType crushedIce,
  /**
   * 极寒领域
   */
  freezingField,
  /**
   * 破碎FEX结晶
   */
  crushCrystal,
  /**
   * 溢出能量
   */
  spilloverEnergy;

  /**
   * 闪光
   */
  public static Block flash,
  /**
   * 伦琴
   */
  roentgen,

  thunder,
  /**
   * 阴霾
   */
  haze,
  /**
   * 白露
   */
  dew,
  /**
   * 冬至
   */
  winter,
  /**
   * 边界
   */
  edge;

  private static final RandomGenerator branch = new RandomGenerator();

  @Override
  public void load(){
    crushedIce = new BulletType(){
      {
        lifetime = 45;
        hitColor = SglDrawConst.frost;
        hitEffect = SglFx.railShootRecoil;
        damage = 18;
        speed = 2;
        collidesGround = true;
        collidesAir = false;
        pierceCap = 1;
        hitSize = 2;
      }

      @Override
      public void hitEntity(Bullet b, Hitboxc entity, float health){
        if(entity instanceof Healthc h){
          h.damage(b.damage);
        }

        if(entity instanceof Unit unit){
          unit.apply(OtherContents.frost, unit.getDuration(OtherContents.frost) + 6);
        }
      }

      @Override
      public void draw(Bullet b){
        super.draw(b);

        Draw.color(SglDrawConst.frost);
        SglDraw.drawDiamond(b.x, b.y, 6 * b.fout(), 3 * b.fout(), b.rotation());
      }
    };

    freezingField = new BulletType(){
      {
        lifetime = 600;
        hittable = false;
        pierce = true;
        absorbable = false;
        collides = false;
        despawnEffect = Fx.none;
        hitEffect = Fx.none;
        drawSize = 200;
      }

      @Override
      public void update(Bullet b){
        super.update(b);
        float radius = 200 * b.fout();
        Damage.damage(b.team, b.x, b.y, radius, 12 * Time.delta);

        //control.sound.loop(Sounds.windhowl, b, 2);

        if(Mathf.chanceDelta(0.075f * b.fout())){
          SglFx.particleSpread.at(b.x, b.y, SglDrawConst.winter);
        }

        if(Mathf.chanceDelta(0.25f * b.fout(Interp.pow2Out))){
          Angles.randLenVectors((long)Time.time, 1, radius, (dx, dy) -> {
            if(Mathf.chanceDelta(0.7f)){
              SglFx.iceParticle.at(b.x + dx, b.y + dy, -45 + Mathf.random(-15, 15), SglDrawConst.frost);
            }else{
              SglFx.iceCrystal.at(b.x + dx, b.y + dy, SglDrawConst.frost);
            }
          });
        }

        Units.nearbyEnemies(b.team, b.x, b.y, radius, unit -> {
          unit.apply(OtherContents.frost, unit.getDuration(OtherContents.frost) + 2f * Time.delta);
        });
      }

      @Override
      public void draw(Bullet b){
        super.draw(b);
        Draw.z(Layer.flyingUnit + 0.01f);
        Draw.color(SglDrawConst.winter);

        Draw.alpha(0);
        float lerp = b.fin() <= 0.1f ? 1 - Mathf.pow(1 - Mathf.clamp(b.fin() / 0.1f), 2) : Mathf.clamp(b.fout() / 0.9f);
        SglDraw.gradientCircle(b.x, b.y, 215 * lerp, 0.8f);

        Draw.z(Layer.effect);
        Draw.alpha(1);
        Lines.stroke(2 * lerp);
        SglDraw.dashCircle(b.x, b.y, 200 * b.fout(), 12, 180, Time.time);
      }
    };

    crushCrystal = new BulletType(){
      {
        lifetime = 60;
        hitColor = SglDrawConst.fexCrystal;
        hitEffect = SglFx.railShootRecoil;
        damage = 48;
        speed = 3.5f;
        collidesGround = true;
        collidesAir = true;
        pierceCap = 2;
        hitSize = 2.2f;

        trailColor = SglDrawConst.fexCrystal;
        trailEffect = SglFx.trailLine;
        trailInterval = 3;
        trailRotation = true;

        homingRange = 130;
        homingPower = 0.065f;
      }

      @Override
      public void update(Bullet b){
        super.update(b);
        b.vel.x = Mathf.lerpDelta(b.vel.x, 0, 0.025f);
        b.vel.y = Mathf.lerpDelta(b.vel.y, 0, 0.025f);
      }

      @Override
      public void draw(Bullet b){
        drawTrail(b);

        Draw.color(SglDrawConst.fexCrystal);
        SglDraw.drawDiamond(b.x, b.y, 8.6f, 4.4f, b.rotation());
      }
    };

    spilloverEnergy = new BulletType(){
      {
        collides = false;
        absorbable = false;

        splashDamage = 120;
        splashDamageRadius = 40;
        speed = 4.4f;
        lifetime = 64;

        hitShake = 4;
        hitSize = 3;

        despawnHit = true;
        hitEffect = new MultiEffect(
          SglFx.explodeImpWaveSmall,
          SglFx.diamondSpark
        );
        hitColor = SglDrawConst.matrixNet;

        trailColor = SglDrawConst.matrixNet;
        trailEffect = SglFx.movingCrystalFrag;
        trailRotation = true;
        trailInterval = 4f;

        fragBullet = new LightningBulletType(){{
          lightningLength = 14;
          lightningLengthRand = 4;
          damage = 24;
        }};
        fragBullets = 1;
      }

      @Override
      public void update(Bullet b){
        super.update(b);

        b.vel.lerp(0, 0, 0.012f);

        if(b.timer(4, 3)){
          Angles.randLenVectors(System.nanoTime(), 2, 2.2f,
            (x, y) -> SglParticleModels.floatParticle.create(b.x, b.y, SglDrawConst.matrixNet, x, y, 2.2f).setVar(RandDeflectParticle.STRENGTH, 0.3f)
          );
        }
      }

      @Override
      public void draw(Bullet b){
        Draw.color(hitColor);
        float fout = b.fout(Interp.pow3Out);
        Fill.circle(b.x, b.y, 5f * fout);
        Draw.color(Color.black);
        Fill.circle(b.x, b.y, 2.6f * fout);
      }
    };

    flash = new SglTurret("flash"){{
      requirements(Category.turret, ItemStack.with(
        SglItems.strengthening_alloy, 35,
        Items.surgeAlloy, 40,
        Items.plastanium, 45
      ));
      size = 2;

      itemCapacity = 20;
      liquidCapacity = 30;
      range = 240;

//      shootSound = Sounds.shootSmite;

      //copy from smite
      newAmmo(new BasicBulletType(6f, 72){{
        sprite = "large-orb";
        width = 17f;
        height = 21f;
        hitSize = 8f;

        recoilTime = 120;

        shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
          colorFrom = colorTo = Pal.accent;
          lifetime = 12f;
          sizeTo = 20f;
          strokeFrom = 3f;
          strokeTo = 0.3f;
        }});
        smokeEffect = Fx.shootSmokeSmite;
        ammoMultiplier = 1;
        pierceCap = 3;
        pierce = true;
        pierceBuilding = true;
        hitColor = backColor = trailColor = Pal.accent;
        frontColor = Color.white;
        trailWidth = 2.8f;
        trailLength = 9;
        hitEffect = Fx.hitBulletColor;
        buildingDamageMultiplier = 0.3f;

        despawnEffect = new MultiEffect(Fx.hitBulletColor, new WaveEffect(){{
          sizeTo = 30f;
          colorFrom = colorTo = Pal.accent;
          lifetime = 12f;
        }});

        trailRotation = true;
        trailEffect = Fx.disperseTrail;
        trailInterval = 3f;

        intervalBullet = new LightningBulletType(){{
          damage = 18;
          collidesAir = false;
          ammoMultiplier = 1f;
          lightningColor = Pal.accent;
          lightningLength = 5;
          lightningLengthRand = 10;
          buildingDamageMultiplier = 0.25f;
          lightningType = new BulletType(0.0001f, 0f){{
            lifetime = Fx.lightning.lifetime;
            hitEffect = Fx.hitLancer;
            despawnEffect = Fx.none;
            status = StatusEffects.shocked;
            statusDuration = 10f;
            hittable = false;
            lightColor = Color.white;
            buildingDamageMultiplier = 0.25f;
          }};
        }};

        bulletInterval = 3f;
      }}).setReloadAmount(3);
      consume.item(Items.surgeAlloy, 1);
      consume.power(2.4f);
      consume.time(45);

      newCoolant(1f, 0.4f, l -> l.heatCapacity >= 0.4f && l.temperature <= 0.5f, 0.25f, 20);
    }};

    roentgen = new ProjectileTurret("roentgen"){{
      requirements(Category.turret, ItemStack.with(
        SglItems.strengthening_alloy, 120,
        SglItems.aerogel, 100,
        SglItems.aluminium, 60,
        SglItems.crystal_FEX, 40,
        Items.silicon, 75,
        Items.surgeAlloy, 45
      ));
      size = 4;
      range = 240;
      shootY = 12;
      cooldownTime = 60;

      moveWhileCharging = false;
      shoot.firstShotDelay = 40;
//      shootSound = Sounds.laser;

      newAmmo(new LightLaserBulletType(){{
        length = 240;
        damage = 225;
        empDamage = 180;
        lightColor = Pal.reactorPurple;
        chargeEffect = new MultiEffect(SglFx.colorLaserChargeBegin, SglFx.colorLaserCharge, Fx.lightningCharge);
        status = StatusEffects.electrified;
        statusDuration = 12;
        hitColor = Pal.reactorPurple;
        shootEffect = new MultiEffect(
          SglFx.crossLightMini,
          Fx.circleColorSpark
        );

        colors = new Color[]{
          Pal.reactorPurple.cpy().mul(1f, 1f, 1f, 0.4f),
          Pal.reactorPurple,
          Color.white
        };

        generator.maxSpread = 6;
      }});
      consume.time(30);
      consume.power(12.4f);

      newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, b -> {
        LightLaserBulletType res = (LightLaserBulletType)b.copy();
        res.damage *= 1.25f;
        res.colors = new Color[]{
          SglDrawConst.fexCrystal.cpy().mul(1f, 1f, 1f, 0.4f),
          SglDrawConst.fexCrystal,
          Color.white
        };
        res.lightColor = SglDrawConst.fexCrystal;
        res.empDamage *= 0.8f;
        res.status = OtherContents.crystallize;
        res.statusDuration = 15f;

        return res;
      }, t -> {
        t.add(SglStat.exDamageMultiplier.localized() + 125 + "%");
        t.row();
        t.add(Core.bundle.get("bullet.empDamageMulti") + 80 + "%");
        t.row();
        t.add(OtherContents.crystallize.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"));
      });
      consume.time(60);
      consume.liquid(SglLiquids.FEX_liquid, 0.1f);

      newCoolant(1.5f, 20);
      consume.liquid(SglLiquids.phase_FEX_liquid, 0.1f);

      draw = new DrawSglTurret(){
        @Override
        public void drawTurret(SglTurret block, SglTurretBuild build){
          super.drawTurret(block, build);
        }
      };
    }};

    haze = new SglTurret("haze"){{
      requirements(Category.turret, ItemStack.with(
        SglItems.strengthening_alloy, 180,
        SglItems.aerogel, 180,
        SglItems.matrix_alloy, 120,
        SglItems.uranium_238, 100,
        Items.surgeAlloy, 140,
        Items.graphite, 200
      ));
      size = 5;

      accurateDelay = false;
      accurateSpeed = false;
      itemCapacity = 36;
      range = 580;
      minRange = 100f;
      shake = 7.5f;
      recoil = 2f;
      recoilTime = 150;
      cooldownTime = 150f;

//      shootSound = Sounds.missileLaunch;

      rotateSpeed = 1.25f;

      shootY = 4;

      warmupSpeed = 0.015f;
      fireWarmupThreshold = 0.94f;
      linearWarmup = false;

      scaledHealth = 200;

      Func3<Float, Float, Float, EmpBulletType> type = (dam, empD, r) -> new EmpBulletType(){
        {
          lifetime = 180;
          splashDamage = dam;
          splashDamageRadius = r;

          damage = 0;
          empDamage = empD;
          empRange = r;

          hitSize = 5;

          hitShake = 16;
          despawnHit = true;

          hitEffect = new MultiEffect(
            Fx.shockwave,
            Fx.bigShockwave,
            SglFx.explodeImpWaveLarge,
            SglFx.spreadLightning
          );

          homingPower = 0.02f;
          homingRange = 240;

          shootEffect = Fx.shootBig;
          smokeEffect = Fx.shootSmokeMissile;
          trailColor = Pal.redLight;
          trailEffect = SglFx.shootSmokeMissileSmall;
          trailInterval = 1;
          trailRotation = true;
          hitColor = Items.graphite.color;

          trailWidth = 3;
          trailLength = 28;

//          hitSound = Sounds.largeExplosion;
          hitSoundVolume = 1.2f;

          speed = 0.1f;

          fragOnHit = true;
          fragBullets = 1;
          fragVelocityMin = 0;
          fragVelocityMax = 0;
          fragBullet = new BulletType(0, 0){
            {
              lifetime = 450;
              collides = false;
              pierce = true;
              hittable = false;
              absorbable = false;
              hitEffect = Fx.none;
              shootEffect = Fx.none;
              despawnEffect = Fx.none;
              smokeEffect = Fx.none;
              drawSize = r * 1.2f;
            }

            final RandomGenerator branch = new RandomGenerator();
            final RandomGenerator generator = new RandomGenerator(){
              {
                maxLength = 100;
                maxDeflect = 55;

                branchChance = 0.2f;
                minBranchStrength = 0.8f;
                maxBranchStrength = 1;
                branchMaker = (vert, strength) -> {
                  branch.maxLength = 60 * strength;
                  branch.originAngle = vert.angle + Mathf.random(-90, 90);

                  return branch;
                };
              }
            };

            @Override
            public void init(Bullet b){
              super.init(b);
              LightningContainer c;
              b.data = c = Pools.obtain(LightningContainer.PoolLightningContainer.class, LightningContainer.PoolLightningContainer::new);
              c.maxWidth = 6;
              c.lerp = Interp.linear;
              c.minWidth = 4;
              c.lifeTime = 60;
              c.time = 30;
            }

            @Override
            public void update(Bullet b){
              super.update(b);
              Units.nearbyEnemies(b.team, b.x, b.y, r, u -> Sgl.empHealth.empDamage(u, 0.8f, false));
              if(b.timer(0, 6)){
                Damage.status(b.team, b.x, b.y, r, OtherContents.electric_disturb, Math.min(450 - b.time, 120), true, true);
              }

              if(b.data instanceof LightningContainer c){
                if(b.timer(2, 15 / Mathf.clamp((b.fout() - 0.15f) * 4))){
                  generator.setOffset(Mathf.random(-45f, 45f), Mathf.random(-45f, 45f));
                  generator.originAngle = Mathf.random(0, 360f);
                  c.create(generator);
                }
                c.update();
              }
            }

            @Override
            public void draw(Bullet e){
              Draw.z(Layer.bullet - 5);
              Draw.color(Pal.stoneGray);
              Draw.alpha(0.6f);
              rand.setSeed(e.id);
              randLenVectors(e.id, 8 + 70, r * 1.2f, (x, y) -> {
                float size = rand.random(14, 20);
                float i = e.fin(Interp.pow3Out);
                Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out));
              });

              Draw.color(Items.graphite.color);
              Draw.z(Layer.effect);

              if(e.data instanceof LightningContainer c){
                c.draw(e.x, e.y);
              }
            }

            @Override
            public void removed(Bullet b){
              if(b.data instanceof LightningContainer c){
                Pools.free(c);
              }
              super.removed(b);
            }
          };
        }

        TextureRegion regionOutline;

        @Override
        public void init(Bullet b){
          super.init(b);
          //b.data = new SoundLoop(Sounds.missileTrail, 0.65f);
        }

        @Override
        public void update(Bullet b){
          super.update(b);
          Tmp.v1.set(b.vel).setLength(28);
          b.vel.approachDelta(Tmp.v1, 0.06f * Mathf.clamp((b.fin() - 0.10f) * 5f));

          if(b.data instanceof SoundLoop loop){
            loop.update(b.x, b.y, true);
          }
        }

        @Override
        public void removed(Bullet b){
          super.removed(b);
          if(b.data instanceof SoundLoop loop){
            loop.stop();
          }
        }

        @Override
        public void draw(Bullet b){
          drawTrail(b);
          Draw.z(Layer.effect + 1);
          Draw.rect(regionOutline, b.x, b.y, b.rotation() - 90);

          SglDraw.drawTransform(b.x, b.y, 0, 4 * b.fin(), b.rotation() - 90, (x, y, r) -> {
            Draw.rect(regionOutline, x, y, 4, 10.5f, r);
          });
          SglDraw.drawTransform(b.x, b.y, 0, -4, b.rotation() - 90, (x, y, r) -> {
            Draw.color(hitColor, 0.75f);
            Fill.circle(x, y, 2.5f);
            Draw.color(Color.white);
            Fill.circle(x, y, 1.5f);
          });
        }

        @Override
        public void load(){
          super.load();
          TextureRegion r = Singularity.getModAtlas("haze_missile");
          PixmapRegion p = Core.atlas.getPixmap(r);
          regionOutline = new TextureRegion(new Texture(Pixmaps.outline(p, Pal.darkOutline, 3)));
        }
      };

      newAmmo(type.get(480f, 500f, 120f), (t, b) -> {
        t.add(Core.bundle.get("infos.graphiteEmpAmmo"));
        t.row();
        t.table(table -> {
          table.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.8f * 60, 1) + "/" + StatUnit.seconds.localized(), ""));
          table.row();
          table.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"));
        }).padLeft(15);
      });
      consume.items(ItemStack.with(
        Items.graphite, 12,
        SglItems.concentration_uranium_235, 1
      ));
      consume.time(480);

      newAmmo(type.get(600f, 550f, 145f), (t, b) -> {
        t.add(Core.bundle.get("infos.graphiteEmpAmmo"));
        t.row();
        t.table(table -> {
          table.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.5f * 60, 1) + "/" + StatUnit.seconds.localized(), ""));
          table.row();
          table.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"));
        }).padLeft(15);
      });
      consume.items(ItemStack.with(
        Items.graphite, 12,
        SglItems.concentration_plutonium_239, 1
      ));
      consume.time(510);

      draw = new DrawSglTurret(
        new RegionPart("_missile"){{
          progress = PartProgress.warmup.mul(PartProgress.reload.inv());
          x = 0;
          y = -4;
          moveY = 8;
        }},
        new RegionPart("_side"){{
          progress = PartProgress.warmup;
          mirror = true;
          moveX = 4;
          moveY = 2;
          moveRot = -35;

          under = true;
          layerOffset = -0.3f;
          turretHeatLayer = Layer.turret - 0.2f;

          moves.add(new PartMove(PartProgress.recoil, 0, -2, -10));
        }},
        new RegionPart("_spine"){{
          progress = PartProgress.warmup;
          heatProgress = PartProgress.warmup;
          mirror = true;
          outline = false;

          heatColor = Items.graphite.color;
          heatLayerOffset = 0;

          xScl = 1.5f;
          yScl = 1.5f;

          x = 3.3f;
          y = 7.3f;
          moveX = 10f;
          moveY = 5;
          moveRot = -30;

          under = true;
          layerOffset = -0.3f;
          turretHeatLayer = Layer.turret - 0.2f;

          moves.add(new PartMove(PartProgress.recoil.delay(0.8f), -1.33f, 0, 16));
        }},

        new RegionPart("_spine"){{
          progress = PartProgress.warmup;
          heatProgress = PartProgress.warmup;
          mirror = true;
          outline = false;

          heatColor = Items.graphite.color;
          heatLayerOffset = 0;

          xScl = 1.5f;
          yScl = 1.5f;

          x = 3.3f;
          y = 7.3f;
          moveX = 12.3f;
          moveY = -2.6f;
          moveRot = -45;

          under = true;
          layerOffset = -0.3f;
          turretHeatLayer = Layer.turret - 0.2f;

          moves.add(new PartMove(PartProgress.recoil.delay(0.4f), -1.33f, 0, 24));
        }},

        new RegionPart("_spine"){{
          progress = PartProgress.warmup;
          heatProgress = PartProgress.warmup;
          mirror = true;
          outline = false;

          heatColor = Items.graphite.color;
          heatLayerOffset = 0;

          xScl = 1.5f;
          yScl = 1.5f;

          x = 3.3f;
          y = 7.3f;
          moveX = 13f;
          moveY = -9.2f;
          moveRot = -60;

          under = true;
          layerOffset = -0.3f;
          turretHeatLayer = Layer.turret - 0.2f;

          moves.add(new PartMove(PartProgress.recoil, -1.33f, 0, 30));
        }},
        new RegionPart("_blade"){{
          progress = PartProgress.warmup;
          mirror = true;
          moveX = 2.5f;

          heatProgress = PartProgress.warmup;
          heatColor = Items.graphite.color;

          moves.add(new PartMove(PartProgress.recoil, 0, -2, 0));
        }},
        new RegionPart("_body"){{
          mirror = false;
          heatProgress = PartProgress.warmup;
          heatColor = Items.graphite.color;
        }}
      );
    }};

    thunder = new SglTurret("thunder"){{
      requirements(Category.turret, ItemStack.empty);
      float shootRan;
      size = 5;
      scaledHealth = 320;
      shootRan = range = 400;
      warmupSpeed = 0.016f;
      linearWarmup = false;
      fireWarmupThreshold = 0.8f;
      rotateSpeed = 1.6f;
      cooldownTime = 90;
      recoil = 3.4f;

      shootY = 22;

      shake = 4;
//      shootSound = Sounds.largeCannon;

      newAmmo(new BulletType(){
        {
          speed = 0;
          lifetime = 60;
          collides = false;
          hittable = false;
          absorbable = false;
          splashDamage = 1460;
          splashDamageRadius = 46;
          damage = 0;
          drawSize = shootRan;

          hitColor = Pal.reactorPurple;
          shootEffect = new MultiEffect(SglFx.impactBubble, SglFx.shootRecoilWave, new WaveEffect(){{
            colorFrom = colorTo = Pal.reactorPurple;
            lifetime = 12f;
            sizeTo = 40f;
            strokeFrom = 6f;
            strokeTo = 0.3f;
          }});

          hitEffect = Fx.none;
          despawnEffect = Fx.none;
          smokeEffect = Fx.none;

          RandomGenerator g = new RandomGenerator(){{
            maxLength = 100;
            maxDeflect = 55;

            branchChance = 0.2f;
            minBranchStrength = 0.8f;
            maxBranchStrength = 1;
            branchMaker = (vert, strength) -> {
              branch.maxLength = 60 * strength;
              branch.originAngle = vert.angle + Mathf.random(-90, 90);

              return branch;
            };
          }};

          fragBullet = lightning(82, 25, 42, 4.8f, Pal.reactorPurple, b -> {
            Unit u = Units.closest(b.team, b.x, b.y, 80, e -> true);
            g.originAngle = u == null ? b.rotation() : b.angleTo(u);
            return g;
          });
          fragSpread = 25;
          fragOnHit = false;
        }

        final VectorLightningGenerator generator = new VectorLightningGenerator(){{
          maxSpread = 14;
          minInterval = 8;
          maxInterval = 20;

          branchChance = 0.1f;
          minBranchStrength = 0.5f;
          maxBranchStrength = 0.8f;
          branchMaker = (vert, strength) -> {
            branch.maxLength = 60 * strength;
            branch.originAngle = vert.angle + Mathf.random(-90, 90);

            return branch;
          };
        }};

        @Override
        public void init(Bullet b){
          super.init(b);

          LightningContainer container = Pools.obtain(LightningContainer.PoolLightningContainer.class, LightningContainer.PoolLightningContainer::new);
          container.lifeTime = lifetime;
          container.minWidth = 5;
          container.maxWidth = 8;
          container.time = 6;
          container.lerp = Interp.linear;
          b.data = container;

          Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY);
          float scl = Mathf.clamp(Tmp.v1.len() / shootRan);
          Tmp.v1.setLength(shootRan).scl(scl);

          float shX, shY;

          Building absorber = Damage.findAbsorber(b.team, b.originX, b.originY, b.originX + Tmp.v1.x, b.originY + Tmp.v1.y);
          if(absorber != null){
            shX = absorber.x;
            shY = absorber.y;
          }else{
            shX = b.x + Tmp.v1.x;
            shY = b.y + Tmp.v1.y;
          }

          generator.vector.set(
            shX - b.originX,
            shY - b.originY
          );

          int amount = Mathf.random(5, 7);
          for(int i = 0; i < amount; i++){
            container.create(generator);
          }

          Time.run(6, () -> {
            SglFx.lightningBoltWave.at(shX, shY, Pal.reactorPurple);
            createFrags(b, shX, shY);
            Effect.shake(6, 6, shX, shY);
            //Sounds.largeExplosion.at(shX, shY, hitSoundPitch, hitSoundVolume);
            Damage.damage(b.team, shX, shY, splashDamageRadius, splashDamage);
          });
        }

        @Override
        public void update(Bullet b){
          super.update(b);
          ((LightningContainer)b.data).update();
        }

        @Override
        public void draw(Bullet b){
          LightningContainer container = (LightningContainer)b.data;
          Draw.z(Layer.bullet);
          Draw.color(Pal.reactorPurple);
          container.draw(b.x, b.y);
        }

        @Override
        public void createSplashDamage(Bullet b, float x, float y){
        }

        @Override
        public void despawned(Bullet b){
        }

        @Override
        public void removed(Bullet b){
          super.removed(b);
          if(b.data instanceof LightningContainer.PoolLightningContainer c){
            Pools.free(c);
          }
        }
      });
      consume.item(SglItems.crystal_FEX_power, 2);
      consume.time(180);

      LightningGenerator generator = new CircleGenerator(){{
        radius = 8;
        maxSpread = 2.5f;
        minInterval = 2;
        maxInterval = 2.5f;
      }};

      initialed = e -> {
        e.setVar(CONTAINER, new LightningContainer(){{
          lifeTime = 45f;
          maxWidth = 2f;
          lerp = Interp.linear;
          time = 0;
        }});
      };

      int timeId = timers++;
      updating = e -> {
        if(!Sgl.config.enableLightning || Sgl.config.animateLevel < 3) return;

        e.<LightningContainer>getVar(CONTAINER).update();
        SglTurretBuild turret = (SglTurretBuild)e;
        if(turret.warmup > 0 && e.timer(timeId, 25 / turret.warmup)){
          e.<LightningContainer>getVar(CONTAINER).create(generator);
        }

        if(Mathf.chanceDelta(0.03f * turret.warmup)){
          Tmp.v1.set(0, -16).rotate(turret.drawrot());
          SglFx.randomLightning.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple);
        }
      };

      newCoolant(1.45f, 20);
      consume.liquid(SglLiquids.phase_FEX_liquid, 0.25f);

      draw = new DrawMulti(
        new DrawSglTurret(
          new RegionPart("_center"){{
            moveY = 8;
            progress = PartProgress.warmup;
            heatColor = Pal.reactorPurple;
            heatProgress = PartProgress.warmup.delay(0.25f);

            moves.add(new PartMove(PartProgress.recoil, 0f, -4f, 0f));
          }},
          new RegionPart("_body"){{
            heatColor = Pal.reactorPurple;
            heatProgress = PartProgress.warmup.delay(0.25f);
          }},
          new RegionPart("_side"){{
            mirror = true;
            moveX = 5;
            moveY = -5;
            progress = PartProgress.warmup;
            heatColor = Pal.reactorPurple;
            heatProgress = PartProgress.warmup.delay(0.25f);
          }},
          new ShapePart(){{
            color = Pal.reactorPurple;
            circle = true;
            hollow = true;
            stroke = 0;
            strokeTo = 2f;
            y = -16;
            radius = 0;
            radiusTo = 10f;
            progress = PartProgress.warmup;
            layer = Layer.effect;
          }},
          new ShapePart(){{
            circle = true;
            y = -16;
            radius = 0;
            radiusTo = 3.5f;
            color = Pal.reactorPurple;
            layer = Layer.effect;
            progress = PartProgress.warmup;
          }},
          new HaloPart(){{
            progress = PartProgress.warmup;
            color = Pal.reactorPurple;
            layer = Layer.effect;
            y = -16;
            haloRotation = 90f;
            shapes = 2;
            triLength = 0f;
            triLengthTo = 30f;
            haloRadius = 10f;
            tri = true;
            radius = 4f;
          }},
          new HaloPart(){{
            progress = PartProgress.warmup;
            color = Pal.reactorPurple;
            layer = Layer.effect;
            y = -16;
            haloRotation = 90f;
            shapes = 2;
            triLength = 0f;
            triLengthTo = 6f;
            haloRadius = 10f;
            tri = true;
            radius = 4f;
            shapeRotation = 180f;
          }},
          new ShapePart(){{
            circle = true;
            y = 22;
            radius = 0;
            radiusTo = 5;
            color = Pal.reactorPurple;
            layer = Layer.effect;
            progress = PartProgress.warmup;
          }},
          new ShapePart(){{
            color = Pal.reactorPurple;
            circle = true;
            hollow = true;
            stroke = 0;
            strokeTo = 1.5f;
            y = 22;
            radius = 0;
            radiusTo = 8;
            progress = PartProgress.warmup;
            layer = Layer.effect;
          }}
        ),
        new DrawBlock(){
          @Override
          public void draw(Building build){
            if(Sgl.config.animateLevel < 3) return;

            rand.setSeed(build.id);

            SglTurretBuild turret = (SglTurretBuild)build;
            Draw.z(Layer.effect);
            Draw.color(Pal.reactorPurple);
            Tmp.v1.set(1, 0).setAngle(turret.rotation);
            float sclX = Tmp.v1.x, sclY = Tmp.v1.y;
            turret.<LightningContainer>getVar(CONTAINER).draw(turret.x + sclX * 22, turret.y + sclY * 22);

            float step = 45 / 16f;
            if(turret.warmup < 0.001f) return;
            for(int i = 0; i < 16; i++){
              float x = turret.x + (step * i) * sclX * turret.warmup + 14 * sclX;
              float y = turret.y + (step * i) * sclY * turret.warmup + 14 * sclY;
              SglDraw.drawRectAsCylindrical(x, y,
                rand.random(2, 18) * turret.warmup,
                rand.random(1.5f, 10),
                (10 + i * 0.75f + rand.random(8)) * turret.warmup,
                (Time.time * rand.random(0.8f, 2) + rand.random(360))
                  * (rand.random(1f) < 0.5 ? -1 : 1),
                turret.drawrot(),
                Pal.reactorPurple, Pal.reactorPurple2, Layer.bullet - 0.5f, Layer.effect
              );
            }
          }
        }
      );
    }};

    dew = new ProjectileTurret("dew"){{
      requirements(Category.turret, ItemStack.with(
        SglItems.strengthening_alloy, 150,
        SglItems.aluminium, 110,
        SglItems.aerogel, 120,
        SglItems.matrix_alloy, 160,
        Items.thorium, 100,
        Items.silicon, 85,
        SglItems.uranium_238, 85
      ));
      size = 5;
      scaledHealth = 360;
      rotateSpeed = 2.5f;
      range = 350;
      shootY = 17.4f;
      warmupSpeed = 0.035f;
      linearWarmup = false;
      recoil = 0f;
      fireWarmupThreshold = 0.75f;
      shootCone = 15;
      shake = 2.2f;

//      shootSound = Sounds.shootAlt;

      shoot = new ShootPattern(){
        @Override
        public void shoot(int totalShots, BulletHandler handler){
          float off = totalShots % 2 - 0.5f;

          for(int i = 0; i < 3; i++){
            handler.shoot(off * 16, 0, 0f, firstShotDelay + 3 * i);
          }
        }
      };

      newAmmo(new BulletType(){
        {
          damage = 80;
          speed = 8;
          lifetime = 45;
          hitSize = 4.3f;
          hitColor = SglDrawConst.matrixNet;
          hitEffect = Fx.colorSpark;
          despawnEffect = Fx.circleColorSpark;
          trailEffect = SglFx.polyParticle;
          trailRotation = true;
          trailChance = 0.04f;
          trailColor = SglDrawConst.matrixNet;
          shootEffect = new MultiEffect(Fx.shootBig, Fx.colorSparkBig);
          hittable = true;
          pierceBuilding = true;
          pierceCap = 4;
        }

        @Override
        public void update(Bullet b){
          super.update(b);
          b.damage = b.type.damage + b.type.damage * b.fin() * 0.3f;
        }

        @Override
        public void draw(Bullet b){
          SglDraw.drawDiamond(b.x, b.y, 18, 6, b.rotation(), SglDrawConst.matrixNet);
          Draw.color(SglDrawConst.matrixNet);
          for(int i : Mathf.signs){
            Drawf.tri(b.x, b.y, 6f * b.fin(), 20f * b.fin(), b.rotation() + 156f * i);
          }
        }
      });
      consume.item(Items.thorium, 1);
      consume.time(10);

      newAmmoCoating(Core.bundle.get("coating.depletedUranium"), Pal.accent, b -> new WarpedBulletType(b){
        {
          damage = b.damage * 1.15f;
          pierceArmor = true;
          pierceCap = 5;
        }

        public void hitEntity(Bullet b, Hitboxc entity, float health){
          if(entity instanceof Unit unit){
            if(unit.shield > 0){
              float damageShield = Math.min(Math.max(unit.shield, 0), damage * 0.85f);
              unit.shield -= damageShield;
              Fx.colorSparkBig.at(b.x, b.y, b.rotation(), Pal.bulletYellowBack);
            }
          }
          super.hitEntity(b, entity, health);
        }

        @Override
        public void draw(Bullet b){
          SglDraw.drawDiamond(b.x, b.y, 24, 6, b.rotation(), Pal.accent);
          Draw.color(SglDrawConst.matrixNet);
          for(int i : Mathf.signs){
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i);
          }
        }
      }, t -> {
        t.add(SglStat.exDamageMultiplier.localized() + 115 + "%");
        t.row();
        t.add(SglStat.exShieldDamage.localized() + 85 + "%");
        t.row();
        t.add(SglStat.exPierce.localized() + ": 1");
        t.row();
        t.add("@bullet.armorpierce");
      });
      consume.time(10);
      consume.item(SglItems.uranium_238, 1);

      newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, b -> new WarpedBulletType(b){
        {
          damage = b.damage * 1.25f;
          hitColor = SglDrawConst.fexCrystal;
          trailEffect = SglFx.movingCrystalFrag;
          trailInterval = 6;
          trailColor = SglDrawConst.fexCrystal;

          status = OtherContents.crystallize;
          statusDuration = 15;
        }

        @Override
        public void draw(Bullet b){
          SglDraw.drawDiamond(b.x, b.y, 24, 6, b.rotation(), hitColor);
          Draw.color(SglDrawConst.matrixNet);
          for(int i : Mathf.signs){
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i);
          }
        }
      }, t -> {
        t.add(SglStat.exDamageMultiplier.localized() + 125 + "%");
        t.row();
        t.add(OtherContents.crystallize.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"));
      }, 2);
      consume.time(20);
      consume.item(SglItems.crystal_FEX, 1);

      draw = new DrawSglTurret(
        new RegionPart("_blade"){{
          mirror = true;
          moveX = 4;
          progress = PartProgress.warmup;
          heatColor = SglDrawConst.dew;
          heatProgress = PartProgress.heat;

          moves.add(new PartMove(PartProgress.recoil, 0, -2.6f, 0));
        }},
        new RegionPart("_side"){{
          mirror = true;
          moveX = 8;
          moveRot = -25;
          progress = PartProgress.warmup;
          heatColor = SglDrawConst.dew;
          heatProgress = PartProgress.warmup.delay(0.25f);

          moves.add(new PartMove(PartProgress.recoil, 1f, -1f, -5));
        }},
        new RegionPart("_body"){{
          heatColor = SglDrawConst.dew;
          heatProgress = PartProgress.warmup.delay(0.25f);
        }},
        new ShapePart(){{
          layer = Layer.effect;
          color = SglDrawConst.matrixNet;
          x = 0;
          y = -16;
          circle = true;
          hollow = true;
          stroke = 0;
          strokeTo = 1.8f;
          radius = 0;
          radiusTo = 8;
          progress = PartProgress.warmup;
        }},
        new HaloPart(){{
          progress = PartProgress.warmup;
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          y = -16;
          shapes = 1;
          triLength = 16f;
          triLengthTo = 46f;
          haloRadius = 0;
          tri = true;
          radius = 0;
          radiusTo = 4;
        }},
        new HaloPart(){{
          progress = PartProgress.warmup;
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          y = -16;
          shapes = 1;
          triLength = 8f;
          triLengthTo = 20f;
          haloRotation = 180;
          haloRadius = 0;
          tri = true;
          radius = 0;
          radiusTo = 4;
        }},
        new HaloPart(){{
          progress = PartProgress.warmup;
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          y = -16;
          shapes = 2;
          haloRotation = 90;
          triLength = 6f;
          triLengthTo = 24f;
          haloRadius = 0;
          tri = true;
          radius = 0;
          radiusTo = 2.5f;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 2;
          y = -6;
          haloRotation = -135f;
          shapes = 1;
          triLength = 14f;
          triLengthTo = 21f;
          haloRadius = 10f;
          tri = true;
          radius = 0;
          radiusTo = 6;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 2;
          y = -6;
          haloRotation = -135;
          shapes = 1;
          triLength = 0f;
          triLengthTo = 6f;
          haloRadius = 10f;
          tri = true;
          radius = 0;
          radiusTo = 6f;
          shapeRotation = 180f;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 22;
          y = -6;
          haloRotation = -135f;
          shapes = 1;
          triLength = 8f;
          triLengthTo = 16f;
          haloRadius = 0f;
          tri = true;
          radius = 0;
          radiusTo = 4.5f;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 22;
          y = -6;
          haloRotation = -135;
          shapes = 1;
          triLength = 0f;
          triLengthTo = 4f;
          haloRadius = 0f;
          tri = true;
          radius = 0;
          radiusTo = 4.5f;
          shapeRotation = 180f;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 12;
          y = -4;
          haloRotation = -160f;
          shapes = 1;
          triLength = 12f;
          triLengthTo = 20f;
          haloRadius = 0f;
          tri = true;
          radius = 0;
          radiusTo = 5f;
        }},
        new HaloPart(){{
          progress = PartProgress.recoil.delay(0.3f);
          color = SglDrawConst.matrixNet;
          layer = Layer.effect;
          mirror = true;
          x = 12;
          y = -4;
          haloRotation = -160;
          shapes = 1;
          triLength = 0f;
          triLengthTo = 5f;
          haloRadius = 0f;
          tri = true;
          radius = 0;
          radiusTo = 5;
          shapeRotation = 180f;
        }}
      );

      newCoolant(1f, 0.25f, l -> l.heatCapacity > 0.7f && l.temperature < 0.35f, 0.4f, 20);
    }};

    winter = new SglTurret("winter"){{
      requirements(Category.turret, ItemStack.with(
        SglItems.strengthening_alloy, 210,
        SglItems.degenerate_neutron_polymer, 80,
        Items.phaseFabric, 180,
        SglItems.iridium, 100,
        SglItems.aerogel, 200,
        SglItems.aluminium, 220,
        SglItems.matrix_alloy, 160,
        SglItems.crystal_FEX_power, 180
      ));
      size = 6;
      scaledHealth = 410;
      recoil = 3.6f;
      rotateSpeed = 1.75f;
      warmupSpeed = 0.015f;
      shake = 6;
      fireWarmupThreshold = 0.925f;
      linearWarmup = false;
      range = 560;
      targetGround = true;
      targetAir = true;
      shootEffect = new MultiEffect(
        SglFx.winterShooting,
        SglFx.shootRecoilWave,
        new WaveEffect(){{
          colorFrom = colorTo = Pal.reactorPurple;
          lifetime = 12f;
          sizeTo = 40f;
          strokeFrom = 6f;
          strokeTo = 0.3f;
        }}
      );
      moveWhileCharging = true;
      shootY = 4;

      unitSort = SglUnitSorts.denser;

      shoot.firstShotDelay = 120;
//      chargeSound = Sounds.lasercharge;
      chargeSoundPitch = 0.9f;

//      shootSound = Sounds.plasmaboom;
      shootSoundPitch = 0.6f;
      shootSoundVolume = 2;

      soundPitchRange = 0.05f;

      newAmmo(new BulletType(){
        {
          lifetime = 20;
          speed = 28;
          collides = false;
          absorbable = false;
          scaleLife = true;
          drawSize = 80;
          fragBullet = new BulletType(){
            {
              lifetime = 120;
              speed = 0.6f;
              collides = false;
              hittable = true;
              absorbable = false;
              despawnHit = true;
              splashDamage = 2180;
              splashDamageRadius = 84;
              hitShake = 12;

              trailEffect = SglFx.particleSpread;
              trailInterval = 10;
              trailColor = SglDrawConst.winter;

              hitEffect = SglFx.iceExplode;
              hitColor = SglDrawConst.winter;

              //          hitSound = Sounds.release;
              hitSoundPitch = 0.6f;
              hitSoundVolume = 2.5f;

              fragBullet = freezingField;
              fragOnHit = false;
              fragBullets = 1;
              fragVelocityMin = 0;
              fragVelocityMax = 0;
            }

            @Override
            public void draw(Bullet b){
              super.draw(b);
              Draw.color(SglDrawConst.winter);

              SglDraw.drawBloomUponFlyUnit(b, e -> {
                float rot = e.fin(Interp.pow2Out) * 3600;
                SglDraw.drawCrystal(e.x, e.y, 30, 14, 8, 0, 0, 0.8f,
                  Layer.effect, Layer.bullet, rot, e.rotation(), SglDrawConst.frost, SglDrawConst.winter);

                Draw.alpha(1);
                Fill.circle(e.x, e.y, 18 * e.fin(Interp.pow3In));
                Draw.reset();
              });
            }

            @Override
            public void update(Bullet b){
              super.update(b);
              //control.sound.loop(Sounds.spellLoop, b, 2);
            }
          };
          fragBullets = 1;
          fragSpread = 0;
          fragRandomSpread = 0;
          fragAngle = 0;
          fragOnHit = false;
          hitColor = SglDrawConst.winter;

          hitEffect = Fx.none;
          despawnEffect = Fx.none;
          smokeEffect = Fx.none;

          trailEffect = new MultiEffect(
            SglFx.glowParticle,
            SglFx.railShootRecoil
          );
          trailRotation = true;
          trailChance = 1;

          trailLength = 75;
          trailWidth = 7;
          trailColor = SglDrawConst.winter;

          chargeEffect = SglFx.shrinkIceParticleSmall;
        }

        @Override
        public void draw(Bullet b){
          super.draw(b);
          Draw.z(Layer.bullet);
          Draw.color(SglDrawConst.winter);
          float rot = b.fin() * 3600;

          SglDraw.drawCrystal(b.x, b.y, 30, 14, 8, 0, 0, 0.8f,
            Layer.effect, Layer.bullet, rot, b.rotation(), SglDrawConst.frost, SglDrawConst.winter);
        }
      }, true, (bt, ammo) -> {
        bt.add(Core.bundle.format("bullet.splashdamage", (int)ammo.fragBullet.splashDamage, Strings.fixed(ammo.fragBullet.splashDamageRadius / tilesize, 1)));
        bt.row();
        bt.add(Core.bundle.get("infos.winterAmmo"));
      });
      consume.time(720);
      consume.liquids(LiquidStack.with(
        SglLiquids.phase_FEX_liquid, 0.2f,
        Liquids.cryofluid, 0.2f
      ));

      updating = e -> {
        SglTurretBuild t = (SglTurretBuild)e;
        if(Mathf.chanceDelta(0.06f * t.warmup)){
          Tmp.v1.set(36, 0).setAngle(t.rotation + 90 * Mathf.randomSign()).rotate(Mathf.random(-30, 30));
          SglFx.iceParticle.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Tmp.v1.angle(), SglDrawConst.frost);
        }
      };

      draw = new DrawSglTurret(
        new CustomPart(){{
          progress = PartProgress.warmup;
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 2) return;

            Draw.color(SglDrawConst.winter);
            SglDraw.gradientTri(x, y, 70 + 120 * p, 92 * p, r, 0);
            SglDraw.gradientTri(x, y, 40 + 68 * p, 92 * p, r + 180, 0);
            Draw.color();
          };
        }},
        new RegionPart("_blade"){{
          mirror = true;
          heatColor = SglDrawConst.winter;
          heatProgress = PartProgress.warmup.delay(0.3f);
          moveX = 5;
          moveY = 4;
          moveRot = -15;
          progress = PartProgress.warmup;

          moves.add(new PartMove(PartProgress.recoil, 0, -2, 0));
        }},
        new RegionPart("_side"){{
          mirror = true;
          heatColor = SglDrawConst.winter;
          heatProgress = PartProgress.warmup.delay(0.3f);
          moveX = 8;
          moveRot = -30;
          progress = PartProgress.warmup;

          moves.add(new PartMove(PartProgress.recoil, 0, -2, -5));
        }},
        new RegionPart("_bot"){{
          mirror = true;
          heatColor = SglDrawConst.winter;
          heatProgress = PartProgress.warmup.delay(0.3f);
          moveX = 6;
          moveY = 2;
          moveRot = -25;
          progress = PartProgress.warmup;

          moves.add(new PartMove(PartProgress.recoil, 0, -2, 0));
        }},
        new RegionPart("_body"){{
          heatColor = SglDrawConst.winter;
          heatProgress = PartProgress.warmup.delay(0.3f);
        }},
        new CustomPart(){{
          mirror = true;
          x = 20;
          drawRadius = 0;
          drawRadiusTo = 20;
          rotation = -30;
          layer = Layer.effect;
          progress = PartProgress.warmup;
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 3) return;

            SglDraw.drawCrystal(x, y, 8 + 8 * p, 6 * p, 4 * p, 0, 0, 0.4f * p,
              Layer.effect, Layer.bullet - 1, Time.time * 1.24f, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
          };
        }},
        new CustomPart(){{
          mirror = true;
          x = 20;
          drawRadius = 0;
          drawRadiusTo = 28;
          rotation = -65;
          layer = Layer.effect;
          progress = PartProgress.warmup.delay(0.15f);
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 3) return;

            SglDraw.drawCrystal(x, y, 16 + 21 * p, 12 * p, 8 * p, 0, 0, 0.7f * p,
              Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 45, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
          };
        }},
        new CustomPart(){{
          mirror = true;
          x = 20;
          drawRadius = 0;
          drawRadiusTo = 24;
          rotation = -105;
          layer = Layer.effect;
          progress = PartProgress.warmup.delay(0.3f);
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 3) return;

            SglDraw.drawCrystal(x, y, 12 + 14 * p, 10 * p, 6 * p, 0, 0, 0.6f * p,
              Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 90, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
          };
        }},
        new CustomPart(){{
          mirror = true;
          x = 20;
          drawRadius = 0;
          drawRadiusTo = 20;
          rotation = -135;
          layer = Layer.effect;
          progress = PartProgress.warmup.delay(0.45f);
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 3) return;

            SglDraw.drawCrystal(x, y, 9 + 12 * p, 8 * p, 5 * p, 0, 0, 0.65f * p,
              Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 135, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
          };
        }},
        new CustomPart(){{
          progress = PartProgress.charge;
          y = 4;
          layer = Layer.effect;
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 2) return;

            Draw.color(SglDrawConst.winter);
            Drawf.tri(x, y, 10 * p, 12 * p, r);
            Drawf.tri(x, y, 10 * p, 8 * p, r + 180);
            Draw.color(SglDrawConst.frost);
            SglDraw.gradientCircle(x, y, 4 + 12 * p, -7 * p, 0);
          };
        }},
        new CustomPart(){{
          progress = PartProgress.warmup;
          y = -18;
          layer = Layer.effect;
          draw = (x, y, r, p) -> {
            if(Sgl.config.animateLevel < 2) return;

            Draw.color(SglDrawConst.frost);
            Lines.stroke(1.8f * p);
            Lines.circle(x, y, 3.5f);
            Draw.alpha(0.7f);

            for(int i = 0; i < 6; i++){
              SglDraw.drawTransform(x, y, 14 * p, 0, r + Time.time * 1.5f + i * 60, (dx, dy, dr) -> {
                Drawf.tri(dx, dy, 4 * p, 4, dr);
                Drawf.tri(dx, dy, 4 * p, 14, dr + 180f);
              });
            }

            Draw.color(SglDrawConst.winter);
            float pl = Mathf.clamp((p - 0.3f) / 0.7f);
            for(int i = 0; i < 4; i++){
              SglDraw.drawTransform(x, y, 16 * pl, 0, r - Time.time + i * 90, (dx, dy, dr) -> {
                if(Sgl.config.animateLevel < 3){
                  SglDraw.drawDiamond(dx, dy, 12, 8 * pl, dr);
                }else{
                  SglDraw.drawCrystal(dx, dy, 12, 8 * pl, 8 * pl, 0, 0, 0.5f * pl,
                    Layer.effect, Layer.bullet - 1, Time.time, dr, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
                }
              });
            }
          };
        }}
      );
    }};
  }

  public BulletType graphiteCloud(float lifeTime, float size, boolean air, boolean ground, float empDamage){
    return new BulletType(0, 0){
      {
        lifetime = lifeTime;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        hitEffect = Fx.none;
        shootEffect = Fx.none;
        despawnEffect = Fx.none;
        smokeEffect = Fx.none;
        drawSize = size;
      }

      @Override
      public void update(Bullet b){
        super.update(b);
        if(empDamage > 0)
          Units.nearbyEnemies(b.team, b.x, b.y, size, u -> Sgl.empHealth.empDamage(u, empDamage, false));
        if(b.timer(0, 6)){
          Damage.status(b.team, b.x, b.y, size, OtherContents.electric_disturb, Math.min(lifeTime - b.time, 120), air, ground);
        }
      }

      @Override
      public void draw(Bullet e){
        Draw.z(Layer.bullet - 5);
        Draw.color(Pal.stoneGray);
        Draw.alpha(0.6f);
        rand.setSeed(e.id);
        randLenVectors(e.id, 8 + (int)size / 2, size * 1.2f, (x, y) -> {
          float size = rand.random(14, 20);
          float i = e.fin(Interp.pow3Out);
          Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out));
        });
      }
    };
  }

  public static BulletType lightning(float lifeTime, float damage, float size, Color color, boolean gradient, Func<Bullet, LightningGenerator> generator){
    return lightning(lifeTime, gradient ? lifeTime / 2 : 0, damage, size, color, generator);
  }

  public static BulletType lightning(float lifeTime, float time, float damage, float size, Color color, Func<Bullet, LightningGenerator> generator){
    return new LLL.lib.singularity.world.blocks.turrets.LightningBulletType(0, damage){
      {
        lifetime = lifeTime;
        collides = false;
        hittable = false;
        absorbable = false;
        reflectable = false;

        hitColor = color;
        hitEffect = Fx.hitLancer;
        shootEffect = Fx.none;
        despawnEffect = Fx.none;
        smokeEffect = Fx.none;

        status = StatusEffects.shocked;
        statusDuration = 18;

        drawSize = 120;
      }

      @Override
      public void init(Bullet b, LightningContainer container){
        container.time = time;
        container.lifeTime = lifeTime;
        container.maxWidth = size;
        container.minWidth = size * 0.85f;
        container.lerp = Interp.linear;

        container.trigger = (last, vert) -> {
          if(!b.isAdded()) return;
          Tmp.v1.set(vert.x - last.x, vert.y - last.y);
          float resultLength = findPierceLength(b, pierceCap, Tmp.v1.len());

          //collideLine(b, b.team, b.type.hitEffect, b.x + last.x, b.y + last.y, Tmp.v1.angle(), resultLength, false, false, pierceCap);

          b.fdata = resultLength;
        };

        LightningGenerator gen = generator.get(b);
        gen.blockNow = (last, vertex) -> {
          Building abs = Damage.findAbsorber(b.team, b.x + last.x, b.y + last.y, b.x + vertex.x, b.y + vertex.y);
          if(abs == null) return -1;
          float ox = b.x + last.x, oy = b.y + last.y;
          return Mathf.len(abs.x - ox, abs.y - oy);
        };
        container.create(gen);
      }
    };
  }
}
