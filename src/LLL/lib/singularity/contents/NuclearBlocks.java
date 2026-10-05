package LLL.lib.singularity.contents;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.type.*;
import LLL.lib.singularity.world.*;
import LLL.lib.singularity.world.blocks.nuclear.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.draw.*;
import LLL.lib.singularity.world.particles.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import universecore.world.particles.*;
import universecore.world.particles.models.*;

public class NuclearBlocks implements ContentList{
  /**
   * 核能塔座
   */
  public static Block
    /**中子能发电机*/
    neutron_generator,
  /**
   * 核子冲击反应堆
   */
  nuclear_impact_reactor,
  /**
   * 托卡马克点火装置
   */
  tokamak_firer,
  /**
   * 超导约束轨道
   */
  magnetic_confinement_orbit,
  /**
   * 潮汐约束轨道
   */
  tidal_confinement_orbit;

  @Override
  public void load(){
    neutron_generator = new NormalCrafter("neutron_generator"){{
      requirements(Category.power, ItemStack.empty);
      size = 3;

      warmupSpeed = 0.0075f;

      newConsume();
      newProduce();
      produce.power(50);

      draw = new DrawMulti(
        new DrawBottom(),
        new DrawDefault(),
        new DrawPlasma(){{
          suffix = "_plasma_";
          plasma1 = Pal.reactorPurple;
          plasma2 = Pal.reactorPurple2;
        }},
        new DrawRegion("_top")
      );
    }};

    nuclear_impact_reactor = new NormalCrafter("nuclear_impact_reactor"){{
      requirements(Category.power, ItemStack.empty);
      size = 5;
      itemCapacity = 30;
      liquidCapacity = 35;

      craftEffect = SglFx.explodeImpWaveBig;
      craftEffectColor = Pal.reactorPurple;

      updateEffect = SglFx.impWave;
      effectRange = 2;
      updateEffectChance = 0.025f;

//      ambientSound = Sounds.spellLoop;
      ambientSoundVolume = 0.55f;

//      craftedSound = Sounds.largeExplosion;
      craftedSoundVolume = 1f;

      ParticleModel model = new MultiParticleModel(
        new SizeVelRelatedParticle(),
        new TargetMoveParticle(){{
          dest = p -> p.getVar("dest");
          deflection = p -> p.getVar("eff", 0f);
        }},
        new RandDeflectParticle(){{
          deflectAngle = 0;
          strength = 0.125f;
        }},
        new TrailFadeParticle(){{
          trailFade = 0.04f;
          fadeColor = Pal.lightishGray;
          colorLerpSpeed = 0.03f;
        }},
        new ShapeParticle(),
        new DrawDefaultTrailParticle()
      );

      craftTrigger = e -> {
        for(Particle particle : Particle.get(p -> p.x < e.x + 20 && p.x > e.x - 20 && p.y < e.y + 20 && p.y > e.y - 20)){
          particle.remove();
        }

        Effect.shake(4f, 18f, e.x, e.y);
        Angles.randLenVectors(System.nanoTime(), Mathf.random(5, 9), 4.75f, 6.25f, (x, y) -> {
          Tmp.v1.set(x, y).setLength(4);
          Particle p = model.create(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f));
          p.setVar("dest", new Vec2(e.x, e.y));
          p.setVar("eff", e.workEfficiency() * 0.15f);
        });
      };
      crafting = e -> {
        if(Mathf.chanceDelta(0.02f)) Angles.randLenVectors(System.nanoTime(), 1, 2, 3.5f,
          (x, y) -> SglParticleModels.floatParticle.create(e.x, e.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f)));
      };

      warmupSpeed = 0.0008f;

      newConsume().consValidCondition((NormalCrafterBuild e) -> e.power.status >= 0.99f);
      consume.item(SglItems.concentration_uranium_235, 1);
      consume.power(80);
      consume.liquid(Liquids.cryofluid, 0.6f);
      consume.time(180);
      newProduce();
      produce.power(400);

      newConsume().consValidCondition((NormalCrafterBuild e) -> e.power.status >= 0.99f);
      consume.item(SglItems.concentration_plutonium_239, 1);
      consume.power(80);
      consume.liquid(Liquids.cryofluid, 0.6f);
      consume.time(150);
      newProduce();
      produce.power(425);

      draw = new DrawMulti(
        new DrawBottom(),
        new DrawExpandPlasma(){{
          plasmas = 2;
        }},
        new DrawDefault()
      );
    }};

    tokamak_firer = new TokamakCore("tokamak_firer"){{
      requirements(SglCategory.nuclear, ItemStack.empty);
      size = 5;

      itemCapacity = 60;
      liquidCapacity = 65;

      warmupSpeed = 0.0005f;
      stopSpeed = 0.001f;

      conductivePower = true;

      draw = new DrawMulti(
        new DrawBottom(),
        new DrawPlasma(){{
          suffix = "_plasma_";
          plasma1 = SglDrawConst.matrixNet;
          plasma2 = Pal.reactorPurple;
        }},
        new DrawDefault(){
          @Override
          public void draw(Building build){
            Draw.z(Layer.blockOver);
            super.draw(build);
          }
        }
      );

      setFuel(28);
      consume.time(60);
      consume.item(SglItems.hydrogen_fusion_fuel, 1);
      consume.liquid(SglLiquids.phase_FEX_liquid, 0.1f);
      consume.power(32);

      setFuel(30);
      consume.time(60);
      consume.item(SglItems.helium_fusion_fuel, 1);
      consume.liquid(SglLiquids.phase_FEX_liquid, 0.1f);
      consume.power(32);
    }};

    magnetic_confinement_orbit = new TokamakOrbit("magnetic_confinement_orbit"){{
      requirements(SglCategory.nuclear, ItemStack.empty);
      size = 3;

      conductivePower = true;

      newConsume();
      consume.power(3);

      itemCapacity = 20;
      liquidCapacity = 20;

      flueMulti = 1;
      efficiencyPow = 1.5f;
    }};

    tidal_confinement_orbit = new TokamakOrbit("tidal_confinement_orbit"){{
      requirements(SglCategory.nuclear, ItemStack.empty);
      size = 5;

      itemCapacity = 40;
      liquidCapacity = 45;

      flueMulti = 2f;
      efficiencyPow = 2f;
    }};
  }
}
