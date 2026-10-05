package LLL.lib.singularity.contents;

import LLL.lib.singularity.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.util.*;
import LLL.lib.singularity.world.blocks.drills.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.consumers.*;
import LLL.lib.singularity.world.draw.*;
import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class ProductBlocks implements ContentList{
  /**
   * 岩层钻井机
   */
  public static Block rock_drill,
  /**
   * 岩石粉碎机
   */
  rock_crusher,
  /**
   * 矩阵矿床
   */
  matrix_miner,
  /**
   * 采掘扇区
   */
  matrix_miner_node,
  /**
   * 矩阵增幅器
   */
  matrix_miner_overdrive,
  /**
   * 量子隧穿仪
   */
  matrix_miner_pierce,
  /**
   * 谐振增压组件
   */
  matrix_miner_extend;

  @Override
  public void load(){
    rock_drill = new FloorCrafter("rock_drill"){{
      requirements(Category.production, ItemStack.empty);
      size = 2;
      liquidCapacity = 24;
      oneOfOptionCons = true;
      health = 180;

      updateEffect = Fx.pulverizeSmall;
      craftEffect = Fx.mine;
      craftEffectColor = Pal.lightishGray;

      warmupSpeed = 0.005f;

      hasLiquids = true;

      autoSelect = true;

      newConsume();
      consume.time(90);
      consume.liquid(Liquids.water, 0.2f);
      consume.power(1.75f);
      newProduce();
      produce.item(SglItems.rock_bitumen, 1);

      newConsume();
      consume.time(60);
      consume.liquid(Liquids.cryofluid, 0.2f);
      consume.power(1.75f);
      newProduce();
      produce.item(SglItems.rock_bitumen, 2);

      newBooster(1);
      consume.add(new SglConsumeFloor<>(SglAttribute.bitumen, 1.12f));

      draw = new DrawMulti(
        new DrawBottom(),
        new DrawLiquidRegion(Liquids.water){{
          suffix = "_liquid";
        }},
        new DrawRegion("_rotator"){{
          rotateSpeed = 1.5f;
          spinSprite = true;
        }},
        new DrawDefault(),
        new DrawRegion("_top")
      );
    }};

    rock_crusher = new FloorCrafter("rock_crusher"){{
      requirements(Category.production, ItemStack.empty);
      size = 3;

      warmupSpeed = 0.004f;
      updateEffect = Fx.pulverizeSmall;
      craftEffect = Fx.mine;
      craftEffectColor = Items.sand.color;

      oneOfOptionCons = false;

      itemCapacity = 25;
      liquidCapacity = 30;

      newConsume();
      consume.time(30f);
      consume.power(2.2f);
      consume.add(new SglConsumeFloor<FloorCrafterBuild>(
        Blocks.stone, 1.2f / 9f,
        Blocks.craters, 0.8f / 9f,
        Blocks.dacite, 0.8f / 9f,
        Blocks.shale, 1f / 9f,
        Blocks.salt, 1f / 9f,
        Blocks.moss, 0.6f / 9f,
        Blocks.sporeMoss, 0.4f / 9f
      )).baseEfficiency = 0;

      newProduce();
      produce.item(Items.sand, 1);

      newOptionalProduct();
      consume.time(45f);
      consume.add(new SglConsumeFloor<FloorCrafterBuild>(
        Blocks.stone, 0.4f / 9f,
        Blocks.craters, 0.5f / 9f,
        Blocks.salt, 2f / 9f
      )).baseEfficiency = 0;
      consume.optionalAlwaysValid = false;
      produce.item(SglItems.alkali_stone, 1);

      newOptionalProduct();
      consume.add(new SglConsumeFloor<FloorCrafterBuild>(Attribute.spores, 1f)).baseEfficiency = 0;
      consume.optionalAlwaysValid = false;
      produce.liquid(SglLiquids.spore_cloud, 0.2f);

      newBooster(1.8f);
      consume.liquid(Liquids.water, 0.12f);

      draw = new DrawMulti(
        new DrawBottom(),
        new DrawDefault(),
        new DrawBlock(){
          TextureRegion rim;
          final Color heatColor = Color.valueOf("ff5512");

          @Override
          public void draw(Building build){
            NormalCrafterBuild e = (NormalCrafterBuild)build;

            Draw.color(heatColor);
            Draw.alpha(e.workEfficiency() * 0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)));
            Draw.blend(Blending.additive);
            Draw.rect(rim, e.x, e.y);
            Draw.blend();
            Draw.color();
          }

          @Override
          public void load(Block block){
            rim = Core.atlas.find(block.name + "_rim");
          }
        },
        new DrawRegion("_rotator"){{
          rotateSpeed = 2.8f;
          spinSprite = true;
        }},
        new DrawRegion("_top")
      );
    }};

    matrix_miner = new MatrixMiner("matrix_miner"){{
      requirements(Category.production, ItemStack.empty);
      size = 5;
      matrixEnergyUse = 0.6f;

      baseRange = 32;
    }};

    matrix_miner_node = new MatrixMinerSector("matrix_miner_node"){{
      requirements(Category.production, ItemStack.empty);
      size = 3;
      drillSize = 3;

      clipSize = 64 * tilesize;

      energyMulti = 2;
    }};

    matrix_miner_extend = new MatrixMinerComponent("matrix_miner_extend"){{
      requirements(Category.production, ItemStack.empty);
      size = 3;

      drillSize = 5;
      energyMulti = 4;

      clipSize = 64 * tilesize;

      draw = new DrawMulti(
        new DrawDefault(),
        new DrawBlock(){
          @Override
          public void draw(Building build){
            if(Sgl.config.animateLevel < 2) return;

            if(build instanceof MatrixMinerComponentBuild b){
              Draw.z(Layer.effect);
              Draw.color(SglDrawConst.matrixNet);
              Fill.circle(b.x, b.y, 2 * b.warmup);

              Draw.color(Pal.reactorPurple);
              Lines.stroke(2f * b.warmup);
              SglDraw.drawCornerTri(
                b.x, b.y,
                20 * b.warmup,
                4 * b.warmup,
                -Time.time * 1.5f,
                true
              );

              if(b.owner != null){
                for(MatrixMinerPluginBuild plugin : b.owner.plugins){
                  if(plugin instanceof MatrixMinerSector.MatrixMinerSectorBuild sec){
                    Lines.stroke(2f * b.warmup * sec.warmup);
                    SglDraw.drawCornerTri(
                      sec.drillPos.x, sec.drillPos.y,
                      36 * b.warmup * sec.warmup,
                      8 * b.warmup * sec.warmup,
                      -Time.time * 1.5f,
                      true
                    );
                  }
                }
              }
            }
          }
        }
      );
    }};

    matrix_miner_pierce = new MatrixMinerComponent("matrix_miner_pierce"){{
      requirements(Category.production, ItemStack.empty);
      size = 3;

      pierceBuild = true;
      energyMulti = 4;

      clipSize = 64 * tilesize;

      draw = new DrawMulti(
        new DrawDefault(),
        new DrawBlock(){
          final float[] param = new float[9];

          final String[] index = {"t1", "t2", "t3", "t4"};
          final String[] index2 = {"t11", "t12", "t13", "t14"};
          final String[] indexSelf = {"ts1", "ts2", "ts3"};

          @Override
          public void draw(Building build){
            if(Sgl.config.animateLevel < 2) return;

            if(build instanceof MatrixMinerComponentBuild b){
              rand.setSeed(build.id);

              Draw.z(Layer.effect);
              Draw.color(SglDrawConst.matrixNet);
              Fill.circle(b.x, b.y, 2 * b.warmup);
              Draw.color(Pal.reactorPurple);

              for(int i = 0; i < 3; i++){
                for(int d = 0; d < 3; d++){
                  param[d * 3] = rand.random(2f, 4f) / (d + 1) * (i % 2 == 0 ? 1 : -1);
                  param[d * 3 + 1] = rand.random(0f, 360f);
                  param[d * 3 + 2] = rand.random(8f, 20f) / ((d + 1) * (d + 1));
                }

                Vec2 v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, param)).scl(b.warmup);
                Draw.color(Pal.reactorPurple);
                Fill.circle(b.x + v.x, b.y + v.y, b.warmup);

                if(Sgl.config.animateLevel < 3) continue;
                Trail trail = b.getVar(indexSelf[i]);
                if(trail == null) b.setVar(indexSelf[i], trail = new Trail(60));

                trail.update(b.x + v.x, b.y + v.y);

                trail.draw(Pal.reactorPurple, b.warmup);
              }

              if(b.owner != null){
                int ind = 0;
                for(MatrixMinerPluginBuild plugin : b.owner.plugins){
                  if(plugin instanceof MatrixMinerSector.MatrixMinerSectorBuild sec){
                    boolean bool = rand.random(1) > 0.5f;
                    for(int d = 0; d < 3; d++){
                      param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (bool != (d % 2 == 0) ? 1 : -1);
                      param[d * 3 + 1] = rand.random(0f, 360f);
                      param[d * 3 + 2] = rand.random(16f, 40f) / ((d + 1) * (d + 1));
                    }
                    Vec2 v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, param));

                    for(int d = 0; d < 3; d++){
                      param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (bool != (d % 2 == 0) ? -1 : 1);
                      param[d * 3 + 1] = rand.random(0f, 360f);
                      param[d * 3 + 2] = rand.random(12f, 30f) / ((d + 1) * (d + 1));
                    }
                    Vec2 v2 = Tmp.v2.set(MathTransform.fourierSeries(Time.time, param));
                    Draw.color(Pal.reactorPurple);
                    Fill.circle(sec.drillPos.x + v.x, sec.drillPos.y + v.y, 1.5f * b.warmup * sec.warmup);
                    Fill.circle(sec.drillPos.x + v2.x, sec.drillPos.y + v2.y, b.warmup * sec.warmup);

                    if(Sgl.config.animateLevel < 3) continue;
                    Trail trail = b.getVar(index[ind]);
                    if(trail == null) b.setVar(index[ind], trail = new Trail(72));
                    Trail trail2 = b.getVar(index2[ind]);
                    if(trail2 == null) b.setVar(index2[ind], trail2 = new Trail(72));

                    trail.draw(Pal.reactorPurple, 1.5f * b.warmup * sec.warmup);
                    trail.update(sec.drillPos.x + v.x, sec.drillPos.y + v.y);

                    trail2.draw(Pal.reactorPurple, b.warmup * sec.warmup);
                    trail2.update(sec.drillPos.x + v2.x, sec.drillPos.y + v2.y);
                  }

                  ind++;
                }
              }
            }
          }
        }
      );
    }};

    matrix_miner_overdrive = new MatrixMinerComponent("matrix_miner_overdrive"){{
      requirements(Category.production, ItemStack.empty);
      size = 3;
      range = 16;
      drillMoveMulti = 2f;
      energyMulti = 2;

      clipSize = 10 * tilesize;

      liquidCapacity = 40;

      newConsume();
      consume.time(180);
      consume.item(Items.phaseFabric, 1);

      newBoost(1f, 0.6f, l -> l.heatCapacity >= 0.4f && l.temperature <= 0.5f, 0.3f);

      draw = new DrawMulti(
        new DrawDefault(),
        new DrawBlock(){
          @Override
          public void draw(Building build){
            if(Sgl.config.animateLevel < 2) return;

            if(build instanceof MatrixMinerComponentBuild b){
              Draw.z(Layer.effect);
              Draw.color(SglDrawConst.matrixNet);
              Fill.circle(b.x, b.y, 2 * b.warmup);

              Lines.stroke(1.4f * b.warmup, Pal.reactorPurple);
              SglDraw.dashCircle(b.x, b.y, 10, 5, 180, Time.time);

              if(b.owner != null){
                Lines.stroke(1.6f * b.warmup, Pal.reactorPurple);
                SglDraw.dashCircle(b.owner.x, b.owner.y, 18, 6, 180, -Time.time);
              }
            }
          }
        }
      );
    }};
  }
}
