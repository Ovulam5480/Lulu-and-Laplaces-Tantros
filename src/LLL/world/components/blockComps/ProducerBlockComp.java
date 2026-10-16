package LLL.world.components.blockComps;

import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.meta.*;
import LLL.util.*;
import LLL.world.recipe.*;
import LLL.world.recipe.producers.*;
import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import java.util.concurrent.atomic.*;

/**
 * 生产者方块的组件，令方块具有记录输出资源配方的功能
 *
 * @author EBwilson
 * @since 1.0
 */
public interface ProducerBlockComp<T extends Building & ProducerBuildc<T>> extends ConsumerBlockComp<T>{
  static <T extends Building & ProducerBuildc<T>> void buildRecipeSimple(BaseConsumers<T> cons, BaseProducers<T> prod, Table ta){
    boolean[] first = {true};
    cons.forAll(consume -> {
      if(!consume.hasIcons()) return;

      if(!first[0]) ta.add("+").fillX().pad(4);
      ta.table(c -> {
        c.defaults().padLeft(3).fill();

        consume.buildIcons(c);
      }).fill();

      first[0] = false;
    });
    ta.image(Icon.right).padLeft(8).padRight(8).size(30);

    first[0] = true;
    prod.forAll(produce -> {
      if(!produce.hasIcons()) return;

      if(!first[0]) ta.add("+").fillX().pad(4);
      ta.table(c -> {
        c.defaults().padLeft(3).fill();

        produce.buildIcons(c);
      }).fill();

      first[0] = false;
    });
  }

  /**
   * 生产清单的列表
   */
  @Annotations.BindField(value = "producers", initialize = "new arc.struct.Seq<>()")
  default Seq<BaseProducers<T>> producers(){
    return null;
  }

  @Annotations.BindField("producers")
  default void producers(Seq<BaseProducers<T>> value){
  }

  /**
   * 同样的，这也是一个指针，指向当前编辑的produce
   */
  @Annotations.BindField("produce")
  default BaseProducers<T> produce(){
    return null;
  }

  @Annotations.BindField("produce")
  default void produce(BaseProducers<T> produce){
  }

  @Annotations.BindField(value = "optionalProducts", initialize = "new arc.struct.OrderedMap<>()")
  default OrderedMap<BaseConsumers<T>, BaseProducers<T>> optionalProducts(){
    return null;
  }

  @Annotations.BindField("optionalProducts")
  default void optionalProducts(OrderedMap<BaseConsumers<T>, BaseProducers<T>> value){
  }

  @Annotations.BindField(value = "rand", initialize = "new arc.math.Rand()")
  default Rand rand(){
    return null;
  }

  @Annotations.BindField("rand")
  default void rand(Rand value){
  }

  @Annotations.BindField(value = "updateEffectChance", initialize = "0.04f")
  default float updateEffectChance(){
    return 0;
  }

  @Annotations.BindField("updateEffectChance")
  default void updateEffectChance(float value){
  }

  @Annotations.BindField(value = "updateEffect", initialize = "mindustry.content.Fx.none")
  default Effect updateEffect(){
    return null;
  }

  @Annotations.BindField("updateEffect")
  default void updateEffect(Effect value){
  }

  @Annotations.BindField(value = "updateEffectColor", initialize = "arc.graphics.Color.white")
  default Color updateEffectColor(){
    return null;
  }

  @Annotations.BindField("updateEffectColor")
  default void updateEffectColor(Color value){
  }

  @Annotations.BindField(value = "craftEffect", initialize = "mindustry.content.Fx.none")
  default Effect craftEffect(){
    return null;
  }

  @Annotations.BindField("craftEffect")
  default void craftEffect(Effect value){
  }

  @Annotations.BindField(value = "craftEffectColor", initialize = "arc.graphics.Color.white")
  default Color craftEffectColor(){
    return null;
  }

  @Annotations.BindField("craftEffectColor")
  default void craftEffectColor(Color value){
  }

  @Annotations.BindField(value = "effectRange", initialize = "-1")
  default float effectRange(){
    return 0;
  }

  @Annotations.BindField(value = "effectRange")
  default void effectRange(float effectRange){
  }

  @Annotations.BindField(value = "craftedSound", initialize = "mindustry.gen.Sounds.none")
  default Sound craftedSound(){
    return null;
  }

  @Annotations.BindField("craftedSound")
  default void craftedSound(Sound value){
  }

  @Annotations.BindField(value = "craftedSoundVolume", initialize = "0.5f")
  default float craftedSoundVolume(){
    return 0;
  }

  @Annotations.BindField("craftedSoundVolume")
  default void craftedSoundVolume(float value){
  }

  @Annotations.BindField(value = "shouldConfig", initialize = "false")
  default boolean shouldConfig(){
    return false;
  }

  @Annotations.BindField("shouldConfig")
  default void shouldConfig(boolean value){
  }

  @Annotations.BindField(value = "craftTrigger")
  default Cons<T> craftTrigger(){
    return null;
  }

  @Annotations.BindField("craftTrigger")
  default void craftTrigger(Cons<T> value){
  }

  @Annotations.BindField(value = "crafting")
  default Cons<T> crafting(){
    return null;
  }

  @Annotations.BindField("crafting")
  default void crafting(Cons<T> value){
  }

  @Annotations.BindField(value = "warmupSpeed", initialize = "0.02f")
  default float warmupSpeed(){
    return 0;
  }

  @Annotations.BindField("warmupSpeed")
  default void warmupSpeed(float value){
  }

  @Annotations.BindField(value = "stopSpeed", initialize = "0.02f")
  default float stopSpeed(){
    return 0;
  }

  @Annotations.BindField("stopSpeed")
  default void stopSpeed(float value){
  }

  @Annotations.BindField(value = "byproducts", initialize = "new arc.struct.OrderedMap<>()")
  default OrderedMap<BaseConsumers<T>, Byproduct> byproducts(){
    return null;
  }

  @Annotations.BindField("byproducts")
  default void byproducts(OrderedMap<BaseConsumers<T>, Byproduct> value){
  }

  @Annotations.BindField(value = "boosts", initialize = "new arc.struct.ObjectFloatMap<>()")
  default ObjectFloatMap<BaseConsumers<T>> boosts(){
    return null;
  }

  @Annotations.BindField("boosts")
  default void boosts(ObjectFloatMap<BaseConsumers<T>> value){
  }

  static <C extends Building & ConsumerBuildc<C>, P extends Building & ProducerBuildc<P>> void buildRecipe(Table table, BaseConsumers<C> consumers, BaseProducers<P> producers){
    Stats stats = new Stats();

    if(consumers != null){
      consumers.display(stats);
    }
    if(producers != null){
      producers.display(stats);
    }

    buildStatTable(table, stats);
  }

  static void buildStatTable(Table table, Stats stat){
    for(StatCat cat : stat.toMap().keys()){
      OrderedMap<Stat, Seq<StatValue>> map = stat.toMap().get(cat);
      if(map.size == 0) continue;

      if(stat.useCategories){
        table.add("@category." + cat.name).color(Pal.accent).fillX();
        table.row();
      }

      for(Stat state : map.keys()){
        table.table(inset -> {
          inset.left();
          inset.add("[lightgray]" + state.localized() + ":[] ").left();
          Seq<StatValue> arr = map.get(state);
          for(StatValue value : arr){
            value.display(inset);
            inset.add().size(10f);
          }
        }).fillX().padLeft(10);
        table.row();
      }
    }
  }


  @Override
  default void constructFactory(){
    Block self = (Block)this;
    self.update = true;
    self.consumesPower = false;
    self.solid = true;
    self.sync = true;
    //self.ambientSound = Sounds.machine;
    self.ambientSoundVolume = 0.03f;
    self.flags = EnumSet.of(BlockFlag.factory);
    appliedConfig(self);
    self.config(byte[].class, (T e, byte[] code) -> {
      if(code.length == 0) return;
      parseConfigObjects(e, DataPackable.readObject(code, e));
    });
  }

  /**
   * 初始化匹配消耗生产列表，在init()最后调用
   */
  @Annotations.MethodEntry(entryMethod = "init")
  default void initProduct(){
    int b = producers().size;
    int a = consumers().size;
    /*控制produce添加/移除配方以使配方同步*/
    while(a > b){
      BaseProducers<T> p = new BaseProducers<>();
      producers().add(p);
      b++;
    }
    while(a < b){
      b--;
      producers().remove(b);
    }

    for(int index = 0; index < producers().size; index++){
      producers().get(index).cons = consumers().get(index);
    }
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initCrafter(){
    Block block = self();
    if(effectRange() == -1){
      effectRange(block.size);
    }

    if(producers().size > 0) for(BaseProducers<T> prod : producers()){
      outputItems(outputItems() | prod.get(ResourceType.item) != null);
      block.hasItems |= outputItems();
      block.hasLiquids |= block.outputsLiquid |= prod.get(ResourceType.liquid) != null;
      block.hasPower |= block.outputsPower |= prod.get(ResourceType.power) != null && prod.<ProducePower<T>>get(ResourceType.power).powerProduction != 0;
    }

    for(BaseProducers<T> producer : producers()){
      block.outputsPayload |= producer.get(ResourceType.payload) != null;
    }

    for(ObjectMap.Entry<BaseConsumers<T>, BaseProducers<T>> product : optionalProducts()){
      product.value.forAll(baseProduce -> baseProduce.parent.cons = product.key);
    }

    //super.init();

    if((producers().size > 1 && canSelect()) || shouldConfig()) block.configurable = true;
  }

  /**
   * 创建一张新的生产清单加入容器，并返回它
   */
  default void newProduce(Cons<BaseProducers<T>> cons){
    BaseProducers<T> produce = new BaseProducers<>();
    cons.get(produce);
    produce(produce);
    producers().add(produce);
  }

  default void newProduce(){
    BaseProducers<T> produce = new BaseProducers<>();
    produce(produce);
    producers().add(produce);
  }

  default void newOptionalProduct(Cons<BaseProducers<T>> cons){
    produce(new BaseProducers<>());
    BaseProducers<T> prod = produce();
    newOptionalConsume((T e, BaseConsumers<T> c) -> {
      prod.forAll(baseProduce -> {
        if(baseProduce.valid(e)) baseProduce.update(e);
        baseProduce.dump(e);
      });
    }, (s, c) -> {
      prod.display(s);
    }, c -> {
    });
    prod.cons = consume();
    cons.get(prod);
    consume().setConsTrigger((T e) -> {
      prod.forAll(baseProduce -> {
        if(baseProduce.valid(e)) baseProduce.produce(e);
      });
    });

    optionalProducts().put(consume(), prod);
  }

  /**
   * 对当前生产清单设置随机副产物
   *
   * @param chance 副产物产出的机会，为任意大于0的值，实际机会计算值从0到chance之间随机取值，结果将超出1的部分直接作为产出数量，与1取模后剩余机会取{@link Mathf#chance(double)}
   * @param item   副产物物品
   */
  default void setByProduct(float chance, Item item){
    setByProduct(0, chance, item);
  }

  /**
   * 对当前生产清单设置随机副产物
   *
   * @param base   最小产出量，机会的计算结果会直接加上这个数值
   * @param chance 副产物产出的机会，为任意大于0的值，实际机会计算值从0到chance之间随机取值，结果将超出1的部分直接作为产出数量，与1取模后剩余机会取{@link Mathf#chance(double)}
   * @param item   副产物物品
   */
  default void setByProduct(int base, float chance, Item item){
    byproducts().put(consume(), new Byproduct(item, chance, base));

    consume().addSelfAccess(ResourceType.item, item);
    consume().setConsTrigger((T e) -> {
      float chanceV = chance + base;
      while(chanceV >= 1){
        if(e.acceptItem(e, item)) e.offload(item);
        chanceV--;
      }

      if(rand().chance(chanceV)) if(e.acceptItem(e, item)) e.offload(item);
    });
    Cons2<Stats, BaseConsumers<T>> old = consume().display;
    consume().display = (s, c) -> s.add(Stat.output, t -> {
      old.get(s, c);
      t.row();
      t.table(i -> {
        i.add(Core.bundle.get("misc.extra") + ":");
        i.add(StatValues.displayItem(item, base)).left().padLeft(6);
        i.add("[gray]" + (base > 0 ? " +" : "") + Strings.autoFixed(chance * 100, 2) + "%[]");
      }).left().padLeft(5);
    });
  }

  default BaseConsumers<T> newBooster(float boost){
    Floatf<T>[] fun = new Floatf[1];
    newOptionalConsume((T e, BaseConsumers<T> c) -> {
      e.currBoost(fun[0]);
      e.mark(2);
    }, (s, c) -> {
      s.add(Stat.boostEffect, t -> {
        t.row();
        t.table(cons -> {
          cons.table(req -> {
            req.left().defaults().left().padLeft(3);
            Stats stats = new Stats();
            c.forAll(co -> co.display(stats));
            buildStatTable(req, stats);
          }).left().padRight(40);
          cons.add(Core.bundle.get("misc.efficiency") + Strings.autoFixed(boost * 100, 1) + "%").growX().right();
        });

      });
    }, c -> {
    });

    BaseConsumers<T> res = consume();
    boosts().put(res, boost);

    fun[0] = e -> {
      float[] mul = {1};
      res.forAll(cons -> mul[0] *= cons.efficiency(e));
      return boost * mul[0] * Mathf.clamp(e.consumer().consEfficiency) * e.consumer().getOptionalEff(res);
    };

    consume().customDisplayOnly = true;
    consume().optionalAlwaysValid = false;

    return res;
  }

  @Annotations.MethodEntry(entryMethod = "setBars")
  default void setBarsCrafter(){
    self().addBar("efficiency", (T e) -> new Bar(
      () -> Core.bundle.get("misc.efficiency") + ": " + Mathf.round(e.workEfficiency() * 100) + "%",
      () -> Pal.accent,
      e::workEfficiency
    ));
  }

  @Annotations.MethodEntry(entryMethod = "setStats", context = "stats -> stats")
  default void setStatsProducer(Stats stats){
    if(producers().size > 1){
      stats.add(SglStat.autoSelect, autoSelect());
      stats.add(SglStat.controllable, canSelect());
    }

    stats.add(SglStat.recipes, t -> {
      t.left().row();
      t.add(Core.bundle.get("infos.touchShowDetails")).color(Color.gray).left();
      t.row();
      for(int i = 0; i < consumers().size; i++){
        BaseConsumers<T> cons = consumers().get(i);
        BaseProducers<T> prod = producers().get(i);

        Table details = new Table();
        buildRecipe(details, cons, prod);

        Table simple = new Table(ta -> {
          ta.left().defaults().left();

          if(cons.showTime){
            ta.stack(
              new Table(o -> {
                o.left();
                o.add(new Image(SglDrawConst.time)).size(32f).scaling(Scaling.fit);
              }),
              new Table(o -> {
                o.left().bottom();
                o.add(Strings.autoFixed(cons.craftTime / 60, 1) + StatUnit.seconds.localized()).style(Styles.outlineLabel);
                o.pack();
              })
            );
            ta.add(" > ");
          }

          buildRecipeSimple(cons, prod, ta);
        });

        AtomicBoolean isSim = new AtomicBoolean(false);
        AtomicReference<Runnable> rebuild = new AtomicReference<>();

        t.table(SglDrawConst.grayUIAlpha, ta -> {
          rebuild.set(() -> {
            ta.clearChildren();
            ta.left().add(isSim.get() ? details : simple);

            isSim.set(!isSim.get());
          });
          rebuild.get().run();

          ta.touchable = Touchable.enabled;
        }).margin(8).left().growX().fillY().pad(3).get().clicked(() -> {
          rebuild.get().run();
        });
        t.row();
      }
    });
  }

  class Byproduct{
    public Item item;
    public float chance;
    public int base;

    public Byproduct(Item item, float chance, int base){
      this.item = item;
      this.chance = chance;
      this.base = base;
    }
  }
}
