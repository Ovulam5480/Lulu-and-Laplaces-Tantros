package LLL.lib.singularity.contents;

import LLL.lib.singularity.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.particles.*;
import LLL.lib.singularity.world.unit.*;
import LLL.lib.singularity.world.unit.types.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.world.lightnings.*;
import universecore.world.lightnings.generator.*;
import universecore.world.particles.models.*;

public class SglUnits implements ContentList{
  public static final String EPHEMERAS = "ephemeras";
  public static final String TIMER = "timer";
  public static final String STATUS = "status";
  public static final String PHASE = "phase";
  public static final String SHOOTERS = "shooters";

  /**
   * 辉夜
   */
  @UnitEntityType(SglUnitEntity.class)
  public static UnitType kaguya,
  /**
   * 虚宿
   */
  emptiness;

  /**
   * 晨星
   */
  @UnitEntityType(AirSeaAmphibiousUnit.AirSeaUnit.class)
  public static UnitType mornstar,
  /**
   * 极光
   */
  aurora;

  @UnitEntityType(SglUnitEntity.class)
  public static UnitType unstable_energy_body;

  /**
   * 机械构造坞
   */
  public static Block cstr_1;

  @Override
  public void load(){
    UnitTypeRegister.registerAll();

    mornstar = new MornstarType();
    kaguya = new KaguyaType();
    aurora = new AuroraType();
    emptiness = new EmptinessType();

    unstable_energy_body = new SglUnitType<SglUnitEntity>("unstable_energy_body"){
      public static final float FULL_SIZE_ENERGY = 3680;

      {
        Events.on(EventType.ClientLoadEvent.class, e -> {
          //immunities.addAll(content.statusEffects());
          Sgl.empHealth.setEmpDisabled(this);
        });

        isEnemy = false;

        health = 10;
        hidden = true;
        hitSize = 32;
        playerControllable = false;
        createWreck = false;
        createScorch = false;
        logicControllable = false;
        useUnitCap = false;

        aiController = () -> new UnitController(){
          @Override
          public void unit(Unit unit){
            //no ai
          }

          @Override
          public Unit unit(){
            // no ai
            return null;
          }
        };
      }

      final CircleGenerator generator = new CircleGenerator();

      final ShrinkGenerator linGen = new ShrinkGenerator(){{
        minInterval = 2.8f;
        maxInterval = 4f;
        maxSpread = 4f;
      }};

      @Override
      public Unit create(Team team){
        SglUnitEntity res = (SglUnitEntity)super.create(team);

        res.setVar("controlTime", Time.time);

        return res;
      }

      @Override
      public void init(SglUnitEntity unit){
        LightningContainer cont = new LightningContainer();
        cont.time = 0;
        cont.lifeTime = 18;
        cont.minWidth = 0.8f;
        cont.maxWidth = 1.8f;
        unit.setVar("lightnings", cont);

        LightningContainer lin = new LightningContainer();
        lin.headClose = true;
        lin.endClose = true;
        lin.time = 12;
        lin.lifeTime = 22;
        lin.minWidth = 1.2f;
        lin.maxWidth = 2.4f;
        unit.setVar("lin", lin);
      }

      @Override
      public void update(Unit u){
        SglUnitEntity unit = (SglUnitEntity)u;

        super.update(unit);

        LightningContainer lightnings = unit.getVar("lightnings");
        LightningContainer lin = unit.getVar("lin");
        if(Mathf.chanceDelta(0.08f)){
          generator.radius = hitSize * Math.min(unit.health / FULL_SIZE_ENERGY, 2);
          generator.minInterval = 4.5f;
          generator.maxInterval = 6.5f;
          generator.maxSpread = 5f;
          lightnings.create(generator);

          Angles.randLenVectors(System.nanoTime(), 1, 1.8f, 2.75f,
            (x, y) -> SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(3.55f, 4.25f))
              .setVar(RandDeflectParticle.STRENGTH, 0.22f));
        }

        if(Mathf.chanceDelta(0.1f)){
          linGen.minRange = linGen.maxRange = hitSize * Math.min(unit.health / FULL_SIZE_ENERGY, 2);
          int n = Mathf.random(1, 3);
          for(int i = 0; i < n; i++){
            lin.create(linGen);
          }
        }

        if(unit.handleVar("timer", (float t) -> t - Time.delta, 15f) <= 0){
          unit.setVar("timer", 12f);
          generator.minInterval = 3.5f;
          generator.maxInterval = 4.5f;
          generator.maxSpread = 4f;
          generator.radius = hitSize * Math.min(unit.health / FULL_SIZE_ENERGY, 2) / 2;
          lightnings.create(generator);
        }

        lightnings.update();
        lin.update();

        unit.hitSize = hitSize * Math.min(unit.health / FULL_SIZE_ENERGY, 2);
        float controlTime = 900 - Time.time + unit.getVar("controlTime", 0f);
        if(controlTime <= 0){
          if(unit.health >= 1280){
            Effect.shake(8f, 120f, u.x, u.y);
            Damage.damage(u.x, u.y, unit.hitSize * 5, unit.health / FULL_SIZE_ENERGY * 4680);

            //Sounds.largeExplosion.at(u.x, u.y, 0.8f, 3.5f);

            SglFx.reactorExplode.at(u.x, u.y, 0, unit.hitSize * 5);
            Angles.randLenVectors(System.nanoTime(), Mathf.random(20, 34), 2.8f, 6.5f, (x, y) -> {
              float len = Tmp.v1.set(x, y).len();
              SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f) * ((len - 3) / 4.5f));
            });
          }

          unit.kill();
        }else if(controlTime <= 300){
          float bullTime = unit.handleVar("bullTime", (float f) -> f - Time.delta, 0f);
          if(bullTime <= 0){
            SglTurrets.spilloverEnergy.create(u, u.team, u.x, u.y, Mathf.random(0, 360f), Mathf.random(0.5f, 1));
            unit.health -= 180;
            unit.setVar("bullTime", Math.max(controlTime / 10, 2));
          }

          if(Mathf.chanceDelta(1 - controlTime / 300)){
            float lerp = (900 - Time.time + unit.getVar("controlTime", 0f)) / 900;
            Tmp.v1.rnd(Mathf.random(u.hitSize / (3 - lerp), Math.max(u.hitSize / (2.5f - lerp), 15)));
            SglFx.impWave.at(u.x + Tmp.v1.x, u.y + Tmp.v1.y);
          }
        }
      }

      @Override
      public void draw(Unit u){
        SglUnitEntity unit = (SglUnitEntity)u;

        Draw.z(Layer.effect);

        float radius = u.hitSize;
        float lerp = (900 - Time.time + unit.getVar("controlTime", 0f)) / 900;
        float lerpStart = Mathf.clamp((1 - lerp) / 0.1f);
        float lerpEnd = Interp.pow3Out.apply(Mathf.clamp(lerp / 0.2f));

        Lines.stroke(radius * 0.055f * lerpStart, Pal.reactorPurple);
        Lines.circle(u.x, u.y, radius * lerpEnd + radius * Interp.pow2In.apply(1 - lerpStart));

        Draw.draw(Draw.z(), () -> {
          MathRenderer.setThreshold(0.4f, 0.7f);
          MathRenderer.setDispersion(lerpStart * 1.2f);
          Draw.color(Pal.reactorPurple);
          MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.7f + radius * Interp.pow2In.apply(1 - lerpStart), 3, radius * 0.6f, Time.time * 1.2f);
          MathRenderer.setDispersion(lerpStart);
          Draw.color(SglDrawConst.matrixNet);
          MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.72f + radius * Interp.pow2In.apply(1 - lerpStart), 4, radius * 0.67f, Time.time * 1.6f);
        });

        Draw.color(SglDrawConst.matrixNet);
        Fill.circle(u.x, u.y, radius / (2.4f - lerp) * Interp.pow2Out.apply(lerpStart) * lerpEnd);
        Lines.stroke(lerp);
        Lines.circle(u.x, u.y, radius * 1.2f * lerpEnd);
        unit.<LightningContainer>getVar("lightnings").draw(u.x, u.y);
        unit.<LightningContainer>getVar("lin").draw(u.x, u.y);

        Draw.color(Color.white);
        Fill.circle(u.x, u.y, Mathf.maxZero(radius / (2.6f - lerp)) * Interp.pow2Out.apply(lerpStart) * lerpEnd);
      }

      @Override
      public void read(SglUnitEntity sglUnitEntity, Reads read, int revision){
        sglUnitEntity.getVar("controlTime", Time.time + read.f());
      }

      @Override
      public void write(SglUnitEntity sglUnitEntity, Writes write){
        write.f(Time.time - sglUnitEntity.getVar("controlTime", 0f));
      }
    };

    cstr_1 = new SglUnitFactory("cstr_1"){{
      requirements(Category.units, ItemStack.empty);
      size = 5;
      liquidCapacity = 240;


      consCustom = (u, c) -> {
        c.power(Mathf.round(u.health / u.hitSize) * 0.02f).showIcon = true;
      };

      sizeLimit = 24;
      healthLimit = 7200;
      machineLevel = 4;

      newBooster(1.5f);
      consume.liquid(Liquids.cryofluid, 2.4f);
      newBooster(1.8f);
      consume.liquid(SglLiquids.FEX_liquid, 2f);
    }};
  }
}
