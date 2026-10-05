package LLL.content.blocks;

import LLL.content.*;
import LLL.content.extensions.*;
import LLL.entities.gen.*;
import LLL.world.neoplasm.*;
import LLL.world.neoplasm.crafting.*;
import LLL.world.neoplasm.effect.*;
import LLL.world.neoplasm.pedigree.*;
import LLL.world.neoplasm.pedigree.nemertinea.*;
import LLL.world.neoplasm.pedigree.porifera.*;
import LLL.world.neoplasm.production.*;
import LLL.world.neoplasm.units.*;
import LLL.world.uncRecipes.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.meta.*;
import universecore.world.producers.*;

public class Neoplasm{
  public static @Annotations.EntityDef(value = {Buildingc.class, DisplayFlowBuildingc.class}, serialize = false)
  Block neoplasmNeuron, neoplasmGanglion,
    neoplasmBranchialHeart, neoplasmEye, neoplasmRadula, neoplasmPholas, neoplasmMurex,
    neoplasmCalciumPylorus, neoplasmPhosphorusPylorus, neoplasmSiliconPylorus, neoplasmFerrumPylorus,
    neoplasmPhosphorusLarvaCyst,
    neoplasmTriaxonTurret, neoplasmHexactinTurret, neoplasmDodecactinTurret, neoplasmHectoTurret,
    neoplasmTriaxonBase, neoplasmHexactinBase, neoplasmDodecactinBase, neoplasmHectoBase,
    neoplasmHecto,
    calciumCarbonateBoneHuge, hydroxyapatiteBoneHuge, siliconDioxideBoneLarge,
    neoplasmMantle,
    neoplasmActiniaria,
    neoplasmSporebed, neoplasmSporecyst, neoplasmSporevent, settledlarvaCyst;//孢芽床, 孢育囊, 孢风塔

  public static @Annotations.EntityDef(value = {Buildingc.class, VesselBuildc.class, DisplayFlowBuildingc.class}, serialize = false)
  Block neoplasmVessel, neoplasmArterius, neoplasmAortus;

  public static Seq<ItemStack> calcium = new Seq<>();
  public static Seq<ItemStack> phosphorus = new Seq<>();
  public static Seq<ItemStack> silicon = new Seq<>();
  public static Seq<ItemStack> ferrum = new Seq<>();
  public static Seq<ItemStack> hydroxyapatite = new Seq<>();

  public static ObjectMap<Item, Seq<ItemStack>> all = new ObjectMap<>();
  public static IntMap<Item> transform = new IntMap<>();

  static{
    calcium.addAll(ItemStack.with(Items.copper, 4, Items.beryllium, 3, Items.scrap, 3));
    phosphorus.addAll(ItemStack.with(Items.lead, 4, Items.coal, 3, Items.graphite, 3));//silicon 2
    silicon.addAll(ItemStack.with(Items.titanium, 4, Items.tungsten, 3));
    ferrum.addAll(ItemStack.with(Items.thorium, 3, Items.phaseFabric, 1));
    hydroxyapatite.addAll(ItemStack.with(Items.plastanium, 3, Items.surgeAlloy, 2, Items.oxide, 4, Items.carbide, 2));

    all.put(OvulamItems.calcium, calcium);
    all.put(OvulamItems.phosphorus, phosphorus);
    all.put(Items.silicon, silicon);
    all.put(OvulamItems.ferrum, ferrum);
    all.put(OvulamItems.hydroxyapatite, hydroxyapatite);

    for(ObjectMap.Entry<Item, Seq<ItemStack>> entry : all){
      for(ItemStack stack : entry.value){
        transform.put(stack.item.id, entry.key);
      }
    }
  }

  public static final Seq<Item> element = Seq.with(OvulamItems.calcium, OvulamItems.phosphorus, Items.silicon, OvulamItems.ferrum);

  public static void load(){
    neoplasmVessel = new NeoplasmVessel("neoplasm-vessel");
    neoplasmArterius = new NeoplasmVessel("neoplasm-arterius"){{
      requirements = ItemStack.with(OvulamItems.calcium, 1, OvulamItems.phosphorus, 1);
      speed = 2f;
      liquidCapacity = 150f;
    }};
    neoplasmAortus = new NeoplasmVessel("neoplasm-aortus"){{
      requirements = ItemStack.with(OvulamItems.ferrum, 1);
      speed = 1.01f;
      liquidCapacity = 300f;
    }};
    neoplasmGanglion = new NeoplasmGanglion("neoplasm-ganglion"){{
      requirements = ItemStack.with(OvulamItems.phosphorus, 50);
    }};
    neoplasmBranchialHeart = new NeoplasmBranchialHeart("neoplasm-branchial-heart");
    neoplasmNeuron = new NeoplasmNeuron("neoplasm-neuron");
    neoplasmEye = new NeoplasmEye("neoplasm-eye");
    neoplasmRadula = new NeoplasmRadula("neoplasm-radula");
    neoplasmPholas = new NeoplasmPholas("neoplasm-pholas");
    neoplasmMurex = new NeoplasmMurex("neoplasm-murex"){{
      requirements = ItemStack.with(Items.silicon, 10);
    }};

    neoplasmCalciumPylorus = new NeoplasmFactory("neoplasm-calcium-pylorus"){{
      for(ItemStack itemStack : calcium){
        newConsume();
        consume.item(itemStack.item, itemStack.amount);
        consume.add(new ConsumeNeoplasm<>(0.1f));
        consume.time(30);
        newProduce();
        produce.item(OvulamItems.calcium, 1);
      }
    }};

    neoplasmPhosphorusPylorus = new NeoplasmFactory("neoplasm-phosphorus-pylorus"){{
      for(ItemStack itemStack : phosphorus){
        newConsume();
        consume.item(itemStack.item, itemStack.amount);
        consume.add(new ConsumeNeoplasm<>(0.1f));
        consume.time(30);
        newProduce();
        produce.item(OvulamItems.phosphorus, 1);
      }
    }};

    neoplasmSiliconPylorus = new NeoplasmFactory("neoplasm-silicon-pylorus"){{
      for(ItemStack itemStack : silicon){
        newConsume();
        consume.item(itemStack.item, itemStack.amount);
        consume.add(new ConsumeNeoplasm<>(0.1f));
        consume.time(30);
        newProduce();
        produce.item(Items.silicon, 1);
      }
    }};

    neoplasmFerrumPylorus = new NeoplasmFactory("neoplasm-ferrum-pylorus"){{
      for(ItemStack itemStack : ferrum){
        newConsume();
        consume.item(itemStack.item, itemStack.amount);
        consume.add(new ConsumeNeoplasm<>(0.1f));
        consume.time(30);
        newProduce();
        produce.item(OvulamItems.ferrum, 1);
      }
    }};

    neoplasmPhosphorusLarvaCyst = new NeoplasmLarvaCyst("neoplasm-phosphorus-larva-cyst"){{
      newConsume();
      consume.item(OvulamItems.phosphorus, 20);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(600);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.trophosomePhosphorus, 1), (b, c) -> true));
    }};

    neoplasmTriaxonTurret = new NeoplasmTriaxonTurret("neoplasm-triaxon-turret"){{
      reload = 60f;
      range = 280f;

      buildTime = 60 * 15;

      shootType = new BasicBulletType(12, 60){{
        hitSize = 5;
        width = 10;
        height = 13;
        knockback = 0.5f;
        pierceCap = 2;
        pierceBuilding = true;

        frontColor = backColor = Pal.remove;
        trailInterval = 1f;
      }};
    }};
    neoplasmHexactinTurret = new NeoplasmTriaxonTurret("neoplasm-hexactin-turret"){{
      reload = 60f;
      range = 320f;

      buildTime = 60 * 30;

      shootType = new BasicBulletType(12, 100){{
        hitSize = 5;
        width = 12;
        height = 16;
        knockback = 0.7f;
        pierceCap = 2;
        pierceBuilding = true;

        frontColor = backColor = Pal.remove;
        trailInterval = 1f;
      }};
    }};
    neoplasmDodecactinTurret = new NeoplasmTriaxonTurret("neoplasm-dodecactin-turret"){{
      reload = 60f;
      range = 360f;

      buildTime = 60 * 30;

      shootType = new BasicBulletType(12, 140){{
        hitSize = 5;
        width = 15;
        height = 20;
        knockback = 1f;
        pierceCap = 3;
        pierceBuilding = true;

        frontColor = backColor = Pal.remove;
        trailInterval = 1f;
      }};
    }};
    neoplasmHectoTurret = new NeoplasmTriaxonTurret("neoplasm-hecto-turret"){{
      size = 1;
      reload = 60f;
      range = 400f;
      buildTime = 60 * 10;

      shootType = new BasicBulletType(12, 180){{
        hitSize = 4;
        width = 10;
        height = 12;

        frontColor = backColor = Pal.remove;
      }};
    }};

    neoplasmTriaxonBase = new NeoplasmTriaxTurretBase("neoplasm-triax-turret-base"){{
      maxUnits = 5;

      requirements = ItemStack.with(OvulamItems.calcium, 150, OvulamItems.phosphorus, 250);
      blockType = neoplasmTriaxonTurret;

      itemCapacity = 65;

      newConsume();
      consume.item(OvulamItems.calcium, 30);
      consume.add(new OvulamConsumePayload<>(PayloadStack.with(OvulamUnitTypes.trophosomePhosphorus, 1)));
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(30 * 60);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.amphiblastula, 1), (b, u) -> true));
    }};

    neoplasmHexactinBase = new NeoplasmTriaxTurretBase("neoplasm-hexactin-turret-base"){{
      maxUnits = 7;
      orbitRadius = 100;
      override = 1.2f;

      requirements = ItemStack.with(OvulamItems.calcium, 200, Items.silicon, 150);
      blockType = neoplasmHexactinTurret;

      itemCapacity = 165;

      newConsume();
      consume.item(Items.silicon, 40);
      consume.add(new OvulamConsumePayload<>(PayloadStack.with(OvulamUnitTypes.trophosomePhosphorus, 2)));
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(50 * 60);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.parenchymella, 1), (b, u) -> true));
    }};

    neoplasmDodecactinBase = new NeoplasmTriaxTurretBase("neoplasm-dodecactin-turret-base"){{
      size = 5;
      maxUnits = 9;
      orbitRadius = 120;
      override = 1.4f;

      requirements = ItemStack.with(Items.silicon, 200, OvulamItems.ferrum, 300);
      blockType = neoplasmDodecactinTurret;

      itemCapacity = 405;

      newConsume();
      consume.add(new OvulamConsumePayload<>(PayloadStack.with(OvulamUnitTypes.amphiblastula, 4, OvulamUnitTypes.trophosomePhosphorus, 8)));
      consume.item(OvulamItems.ferrum, 200);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(180 * 60);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.trichimella, 4), (b, u) -> true));
    }};

    neoplasmHectoBase = new NeoplasmTriaxTurretBase("neoplasm-hecto-turret-base"){
      float neoplasmAbsorbMulti = 0;

      {
        blockType = neoplasmHectoTurret;

        maxUnits = 4 * 8 + 3;
        approachRange = 1f;
        drawBase = false;

        posHandler = (i, v) -> {
          int dir = i % 4;
          int mi = maxUnits - i;

          if(mi == 1){
            return v.setZero();
          }else{
            float x = 1 - (i / 4) * 4f / maxUnits;
            v.set(x, x * x).scl(120);

            if(mi < 4){
              return v.scl(Mathf.sign(mi == 2), 0);
            }else{
              Point2 d8e = Geometry.d8edge(dir);
              return v.scl(d8e.x, d8e.y);
            }
          }
        };
      }
    };

    neoplasmHecto = new NeoplasmHecto("neoplasm-hecto"){{
      unitValue.put(OvulamUnitTypes.amphiblastula, 1);
      unitValue.put(OvulamUnitTypes.parenchymella, 3);
      unitValue.put(OvulamUnitTypes.trichimella, 4);
      unitValue.put(OvulamUnitTypes.settledlarva, 15);
      triaxType = neoplasmHectoBase;
      targetUnit = OvulamUnitTypes.juvenileSponge;

      clipSize = 120 + 30f;
    }};

    calciumCarbonateBoneHuge = new Wall("calcium-carbonate-bone-huge"){{
      requirements(OvulamCategory.neoplastic, BuildVisibility.shown, new ItemStack[0]);
      size = 3;

      health = 2880;
      armor = 6;
    }};

    hydroxyapatiteBoneHuge = new Wall("hydroxyapatite-bone-huge"){{
      requirements(OvulamCategory.neoplastic, BuildVisibility.shown, new ItemStack[0]);
      size = 3;

      health = 4500;
      armor = 12;
    }};

    siliconDioxideBoneLarge = new Wall("silicon-dioxide-bone-large"){{
      requirements(OvulamCategory.neoplastic, BuildVisibility.shown, new ItemStack[0]);
      size = 2;

      health = 2880;
      armor = 16;
    }};

    neoplasmMantle = new NeoplasmMetamorphicCyst("neoplasm-mantle"){{
      size = 5;

      newConsume();
      consume.item(OvulamItems.calcium, 27);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(6 * 15);
      newProduce();
      produce.add(new OvulamProducePayload<>(PayloadStack.with(calciumCarbonateBoneHuge, 1), (b, u) -> true));

      newConsume();
      consume.item(OvulamItems.calcium, 27);
      consume.add(new OvulamConsumePayload<>(PayloadStack.with(OvulamUnitTypes.trophosomePhosphorus, 1)));
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(6 * 15);
      newProduce();
      produce.add(new OvulamProducePayload<>(PayloadStack.with(hydroxyapatiteBoneHuge, 1), (b, u) -> true));

      newConsume();
      consume.item(Items.silicon, 12);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(6 * 15);
      newProduce();
      produce.add(new OvulamProducePayload<>(PayloadStack.with(siliconDioxideBoneLarge, 1), (b, u) -> true));
    }};

    neoplasmActiniaria = new NeoplasmActiniaria("neoplasm-actiniaria"){{

    }};

    settledlarvaCyst = new NeoplasmMetamorphicCyst("neoplasm-settledlarva-cyst"){{
      size = 7;

      newConsume();
      consume.add(new OvulamConsumePayload<>(PayloadStack.with(OvulamUnitTypes.amphiblastula, 4, OvulamUnitTypes.parenchymella, 2)));
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(60 * 120);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(OvulamUnitTypes.settledlarva, 1), (b, u) -> true));
    }};

    neoplasmSporecyst = new NeoplasmLarvaCyst("neoplasm-sporecyst"){{
      newConsume();
      consume.item(OvulamItems.berserkSpore, 10);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(300);
      newProduce();
      produce.add(new ProducePayload<>(PayloadStack.with(UnitTypes.alpha, 1), (b, u) -> true));
    }};

    neoplasmSporevent = new NeoplasmSporevent("neoplasm-sporevent"){{
      newConsume();
      consume.item(OvulamItems.berserkSpore, 1);
      consume.add(new ConsumeNeoplasm<>(0.1f));
      consume.time(300);
    }};
  }

}
