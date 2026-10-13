package LLL.world.blockcomp;

import LLL.content.*;
import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.lib.singularity.graphic.*;
import LLL.util.*;
import LLL.world.recipe.*;
import LLL.world.recipe.consumers.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;
import universecore.annotations.Annotations.*;

/**
 * Consume组件，为方块添加可标记消耗项的功能
 *
 * @author EBwilson
 * @since 1.0
 */
public interface ConsumerBlockComp<T extends Building & ConsumerBuildc<T>> extends BlockComp{
  String liquidsStr = Iconc.liquidWater + Core.bundle.get("fragment.bars.liquids");
  String recipeIndfo = Core.bundle.get("fragment.buttons.selectPrescripts");

  @Annotations.BindField(value = "autoSelect", initialize = "false")
  default boolean autoSelect(){
    return false;
  }

  @Annotations.BindField("autoSelect")
  default void autoSelect(boolean value){
  }

  @Annotations.BindField(value = "canSelect", initialize = "true")
  default boolean canSelect(){
    return false;
  }

  @Annotations.BindField("canSelect")
  default void canSelect(boolean value){
  }

  @Annotations.BindField(value = "outputItems", initialize = "false")
  default boolean outputItems(){
    return false;
  }

  @Annotations.BindField(value = "outputItems")
  default void outputItems(boolean outputItems){
  }

  /**
   * 独立的物品栏，为true所有物品具有独立的容量，否则共用物品空间
   */
  @Annotations.BindField(value = "independenceInventory", initialize = "true")
  default boolean independenceInventory(){
    return false;
  }

  @Annotations.BindField("independenceInventory")
  default void independenceInventory(boolean value){
  }

  /**
   * 独立的液体储罐，为true所有液体具有独立的容量，否则共用液体空间
   */
  @Annotations.BindField(value = "independenceLiquidTank", initialize = "true")
  default boolean independenceLiquidTank(){
    return false;
  }

  @Annotations.BindField("independenceLiquidTank")
  default void independenceLiquidTank(boolean value){
  }

  /**
   * 控制在多种可选输入都可用时是全部可用还是其中优先级最高的一种使用
   */
  @Annotations.BindField(value = "oneOfOptionCons", initialize = "true")
  default boolean oneOfOptionCons(){
    return false;
  }

  @Annotations.BindField("oneOfOptionCons")
  default void oneOfOptionCons(boolean value){
  }

  /**
   * 是否显示液体槽
   */
  @Annotations.BindField(value = "displayLiquid", initialize = "true")
  default boolean displayLiquid(){
    return false;
  }

  @Annotations.BindField("displayLiquid")
  default void displayLiquid(boolean value){
  }

  /**
   * 方块的消耗的清单列表
   */
  @BindField(value = "recipeConsumers", initialize = "new arc.struct.Seq<>()")
  default Seq<BaseConsumers<T>> consumers(){
    return null;
  }

  @BindField(value = "recipeConsumers", initialize = "new arc.struct.Seq<>()")
  default void consumers(Seq<BaseConsumers<T>> value){
  }

  /**
   * 这是一个指针，用于标记当前编辑的consume
   */
  @BindField("consume")
  default BaseConsumers<T> consume(){
    return null;
  }

  @BindField("consume")
  default void consume(BaseConsumers<T> consume){
  }

  /**
   * 方块的可选消耗的清单列表
   */
  @BindField(value = "optionalCons", initialize = "new arc.struct.Seq<>()")
  default Seq<BaseConsumers<T>> optionalCons(){
    return null;
  }

  @BindField("optionalCons")
  default void optionalCons(Seq<BaseConsumers<T>> value){
  }

  @BindField(value = "consFilter", initialize = "new LLL.world.recipe.ConsFilter<>()")
  default ConsFilter<T> filter(){
    return null;
  }

  @BindField("consFilter")
  default void filter(ConsFilter<T> value){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void constructFactory(){
    Block self = (Block)this;
    self.update = true;
    self.consumesPower = false;
    self.solid = true;
    self.sync = true;
    appliedConfig(self);
    self.config(byte[].class, (T e, byte[] code) -> {
      if(code.length == 0) return;
      parseConfigObjects(e, DataPackable.readObject(code, e));
    });
  }

  @Annotations.MethodEntry(entryMethod = "init", insert = Annotations.InsertPosition.HEAD)
  default void initFactory(){
    Block self = (Block)this;
    if(consumers().size > 1){
      self.configurable = true;
      self.saveConfig = true;
    }

    Seq<BaseConsumers<T>> consume = new Seq<>();
    if(consumers().size > 0) consume.addAll(consumers());
    if(optionalCons().size > 0) consume.addAll(optionalCons());
    for(BaseConsumers<T> cons : consume){
      if(cons.get(ResourceType.item) != null){
        self.hasItems = true;
        if(cons.craftTime == 0) cons.time(90f);
      }
      self.hasLiquids |= cons.get(ResourceType.liquid) != null;
      self.hasPower |= self.consumesPower |= cons.get(ResourceType.power) != null;
    }

    for(BaseConsumers<T> consumer : consumers()){
      self.acceptsPayload |= consumer.get(ResourceType.payload) != null;
    }
  }

  /**
   * 为将方块加入到能量网络中，需要初始化一个原有的能量消耗器进行代理，这在组件化实现中由{@link Block#init()}入口调用
   */
  @Annotations.MethodEntry(entryMethod = "init")
  default void initPower(){
    Block block = (Block)this;

    if(block.consumesPower){
      block.consumePowerDynamic(e -> {
        T entity = (T)e;
        if(entity.consumer().current == null) return 0f;
        if(entity.tile.build == null || entity.consumeCurrent() == -1 || !entity.consumer().excludeValid(ResourceType.power))
          return 0f;

        ConsumePower<T> cons = entity.consumer().current.<ConsumePower<T>>get(ResourceType.power);
        if(cons == null) return 0;

        if(cons.buffered){
          return (1f - entity.power.status) * cons.capacity;
        }else{
          return entity.consumer().getPowerUsage() * Mathf.num(entity.shouldConsume());
        }
      });
    }
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initFilter(){
    filter().applyFilter(consumers(), optionalCons());
    for(BaseConsumers<T> consumer : consumers()){
      consumer.initFilter();
    }
  }

  default void newConsume(Cons<BaseConsumers<T>> cons){
    BaseConsumers<T> consume = new BaseConsumers<>(false);
    cons.get(consume);
    consumers().add(consume);
    consume(consume);
  }

  default void newConsume(){
    BaseConsumers<T> consume = new BaseConsumers<>(false);
    consumers().add(consume);
    consume(consume);
  }

  default void newOptionalConsume(Cons<BaseConsumers<T>> cons){
    BaseConsumers<T> consume = new BaseConsumers<>(true);
    cons.get(consume);
    optionalCons().add(consume);
    consume(consume);
  }

  default void newOptionalConsume(Cons2<T, BaseConsumers<T>> validDef, Cons2<Stats, BaseConsumers<T>> displayDef, Cons<BaseConsumers> cons){
    BaseConsumers<T> consume = new BaseConsumers<>(true){{
      optionalDef = validDef;
      display = displayDef;
    }};
    cons.get(consume);
    optionalCons().add(consume);
    consume(consume);
  }

  default void appliedConfig(Block self){
    self.config(Integer.class, (T e, Integer i) -> {
      if(consumers().size > 1){
        if(canSelect()) e.recipeSelected(true);
        e.reset();
        if(e.consumeCurrent() == i || i == -1){
          e.consumeCurrent(-1);
          e.recipeSelected(false);
        }else e.consumeCurrent(i);
      }
    });
    self.configClear((T e) -> {
      e.recipeSelected(false);
      e.consumeCurrent(0);
    });
  }

  default void parseConfigObjects(T e, Object obj){
  }

  @Annotations.MethodEntry(entryMethod = "setStats", context = "stats -> stats")
  default void setStatsConsume(Stats stats){
    Block self = (Block)this;
    stats.add(Stat.size, "@x@", self.size, self.size);
    stats.add(Stat.health, self.health, StatUnit.none);
    if(self.canBeBuilt()){
      stats.add(Stat.buildTime, self.buildTime / 60, StatUnit.seconds);
      stats.add(Stat.buildCost, StatValues.items(false, self.requirements));
    }

    if(self.instantTransfer){
      stats.add(Stat.maxConsecutive, 2, StatUnit.none);
    }

    if(self.hasLiquids) self.stats.add(Stat.liquidCapacity, self.liquidCapacity, StatUnit.liquidUnits);

    if(self.hasItems && self.itemCapacity > 0)
      self.stats.add(Stat.itemCapacity, (float)self.itemCapacity, StatUnit.items);

    if(!optionalCons().isEmpty()){
      stats.add(UncStat.optionalInputs, t -> {
        t.left().row();
        for(BaseConsumers<T> con : optionalCons()){
          t.table(SglDrawConst.grayUIAlpha, ta -> {
            ta.left().defaults().left();
            ProducerBlockComp.buildRecipe(ta, con, null);
          }).growX().fillY().pad(6).left().margin(10);
          t.row();
        }
      });
    }
  }

  @Annotations.MethodEntry(entryMethod = "outputsItems", override = true)
  default boolean outputsItemsFactory(){
    Block self = (Block)this;
    return self.hasItems && outputItems();
  }

  @Annotations.MethodEntry(entryMethod = "setBars")
  default void setBarsFactory(){
    Block self = (Block)this;
    self.addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
  }
}
