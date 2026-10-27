package lulu.world.components.buildEntityComps;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.lib.singularity.graphic.*;
import lulu.world.components.blockComps.*;
import lulu.world.modules.*;
import lulu.world.recipe.*;
import lulu.world.recipe.consumers.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

import static lulu.world.components.blockComps.ConsumerBlockComp.*;
import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class ConsumerBuildComp<T extends Building & ConsumerBuildc<T>> implements OvulamBuildingc{
  @Annotations.Import
  LiquidModule liquids;
  @Annotations.Import
  ItemModule items;
  @Annotations.Import
  Team team;
  @Annotations.Import
  Block block;
  @Annotations.Import
  float x, y, health, maxHealth;
  @Annotations.Import
  Tile tile;
  @Annotations.Import
  boolean enabled = true;

  BaseConsumeModule<T> consumer;

  int consumeCurrent;

  private static final Seq<Liquid> tempLiquids = new Seq<>();
  private static final FieldAccessor<PlacementFragment, Boolean> wasHoveredAccessor = Reflection.accessField(PlacementFragment.class, boolean.class, "wasHovered");

  public boolean recipeSelected;

  protected final Seq<LiquidStack> displayLiquids = new Seq<>();
  public int lastRecipe;
  public boolean updateRecipe;

  public int select;

  public float activation;
  public float activateRecover;

  @Override
  public void afterCreate(){
    createConsumer();
  }

  public void createConsumer(){
    liquids = new SglLiquidModule();

    if(getConsumerBlock().recipeConsumers().size == 1) consumeCurrent = 0;

    consumer = new BaseConsumeModule<>(self());
  }

  @Annotations.Replace
  @Override
  public void update(){
    updateConsumer();
  }

  public void updateConsumer(){
    consumer.update();
  }

  @Override
  public void updateBefore(){
    updateConsumerBefore();
  }

  public void updateConsumerBefore(){
    Seq<BaseConsumers<T>> consumers = getConsumerBlock().recipeConsumers();
    getConsumerBlock().recipeConsumers();

    updateRecipe = false;
    if(!recipeSelected && getConsumerBlock().autoSelect() && consumer.hasConsume() && (consumer.current == null || !consumer.valid())){
      int f = -1;
      for(BaseConsumers<T> ignored : consumers){
        int n = select % consumers.size;
        if(consumer.valid(n) && consumers.get(n).selectable.get() == BaseConsumers.Visibility.usable){
          f = n;
          break;
        }
        select = (select + 1) % consumers.size;
      }

      if(consumeCurrent != f && f >= 0){
        consumeCurrent = f;
      }
    }

    if(lastRecipe != consumeCurrent) updateRecipe = true;
    lastRecipe = consumeCurrent;

    if(updateRecipe) wasHoveredAccessor.set(ui.hudfrag.blockfrag, false);
  }

  @Override
  public void updateAfter(){
    updateConsumerAfter();
  }

  public void updateConsumerAfter(){
    activateRecover = Mathf.approachDelta(activateRecover, 0.005f, 0.0001f);
    activation = Mathf.approachDelta(activation, 0, activateRecover);

    if(activation >= 0.5f){
      damageContinuousPierce(maxHealth * Math.min(activation - 0.5f, 0.5f) / 60);
    }
  }

  /**
   * 获得该块的ConsumerBlock
   */
  public ConsumerBlockComp<T> getConsumerBlock(){
    return (ConsumerBlockComp<T>)block;
  }

  /**
   * 这个方块当前是否应该对消耗列表执行消耗
   */
  @Annotations.Replace(1)
  public boolean shouldConsume(){
    return shouldConsumeConsumer();
  }

  public boolean shouldConsumeConsumer(){
    return consumer != null && consumeCurrent != -1;
  }

  /**
   * 这个方块当前是否应该对可选消耗列表执行消耗
   */
  public boolean shouldConsumeOptions(){
    return shouldConsumeOptionsConsumer();
  }

  public boolean shouldConsumeOptionsConsumer(){
    return shouldConsume() && consumer.hasOptional();
  }

  public boolean updateValid(){
    return true;
  }

  @Annotations.Replace(1)
  public void consume(){
    consumer.trigger();
  }

  @Annotations.Replace(1)
  public boolean consumeValid(){
    return consumeValidConsumer();
  }

  public boolean consumeValidConsumer(){
    return enabled && (consumer == null || !consumer.hasConsume() || consumer.valid());
  }

  @Annotations.Replace(1)
  public float consEfficiency(){
    return consEfficiencyConsumer();
  }

  public float consEfficiencyConsumer(){
    return consumer.hasConsume() ? consumer.consEfficiency : 1;
  }

  public float efficiency(){
    return consEfficiency();
  }

  public float consMultiplier(){
    return 1;
  }

  public float optionalConsEff(BaseConsumers<T> consumers){
    return optionalConsEffConsumer(consumers);
  }

  public float optionalConsEffConsumer(BaseConsumers<T> consumers){
    return consumer.getOptionalEff(consumers);
  }

  @Annotations.Replace(1)
  public BlockStatus status(){
    return statusConsumer();
  }

  public BlockStatus statusConsumer(){
    return !enabled ? BlockStatus.logicDisable : consumer.status();
  }

  public boolean acceptAll(ResourceType type){
    return getConsumerBlock().autoSelect() && (!getConsumerBlock().canSelect() || !recipeSelected);
  }

  @Annotations.Replace(1)
  public boolean acceptItem(Building source, Item item){
    return acceptItemConsumer(source, item);
  }

  public boolean acceptItemConsumer(Building source, Item item){
    return source.interactable(team) && block.hasItems
      && ((source == self() && consumer.current != null && consumer.current.selfAccess(ResourceType.item, item))
      || !(consumer.hasConsume() || consumer.hasOptional())
      || getConsumerBlock().filter().filter(self(), ResourceType.item, item, acceptAll(ResourceType.item)))
      && (getConsumerBlock().independenceInventory() ? items.get(item) : items.total()) < getMaximumAccepted(item);
  }

  @Annotations.Replace(1)
  public boolean acceptLiquid(Building source, Liquid liquid){
    return acceptLiquidConsumer(source, liquid);
  }

  public boolean acceptLiquidConsumer(Building source, Liquid liquid){
    return source.interactable(team) && block.hasLiquids
      && ((source == self() && consumer.current != null && consumer.current.selfAccess(ResourceType.liquid, liquid))
      || !(consumer.hasConsume() || consumer.hasOptional())
      || getConsumerBlock().filter().filter(self(), ResourceType.liquid, liquid, acceptAll(ResourceType.liquid)))
      && (getConsumerBlock().independenceLiquidTank() ? liquids.get(liquid) : ((SglLiquidModule)liquids).total()) <= getMaximumAccepted(liquid) - 0.0001f;
  }

  @Annotations.Replace(1)
  public float getMaximumAccepted(Liquid liquid){
    return block.liquidCapacity;
  }

  public abstract int getMaximumAccepted(Item item);

  @Annotations.Replace(1)
  public SglLiquidModule liquids(){
    return (SglLiquidModule)liquids;
  }

  @Annotations.Replace(1)
  public void dumpLiquid(){
    liquids.each((l, n) -> dumpLiquid(l));
  }

  public void dumpLiquid(Liquid liquid){
  }

  public void updateDisplayLiquid(){
    displayLiquids.clear();
    tempLiquids.clear();
    if(consumeCurrent >= 0 && consumer.current != null && consumer.current.get(ResourceType.liquid) != null){
      for(LiquidStack stack : consumer.current.<ConsumeLiquidBaseR<T>>get(ResourceType.liquid).consLiquids){
        tempLiquids.add(stack.liquid);
      }
    }
    liquids.each((key, val) -> {
      if(!tempLiquids.contains(key) && val > 0.1f) displayLiquids.add(new LiquidStack(key, val));
    });
  }

  public void displayBars(Table table){
    //显示流体存储量
    if(block.hasLiquids && getConsumerBlock().displayLiquid()) updateDisplayLiquid();
    if(!displayLiquids.isEmpty()){
      table.top().left().add(liquidsStr).left().padBottom(0);
      table.row();
      for(LiquidStack stack : displayLiquids){
        table.add(new Bar(
          () -> stack.liquid.localizedName,
          () -> stack.liquid.barColor != null ? stack.liquid.barColor : stack.liquid.color,
          () -> Math.min(liquids.get(stack.liquid) / block.liquidCapacity, 1f)
        )).growX();
        table.row();
      }
    }

    if(consumeCurrent == -1 || consumer.current == null) return;

    table.defaults().grow().margin(0).padTop(3).padBottom(3);
    table.add(Iconc.download + Core.bundle.get("fragment.bars.consume")).left().padBottom(0);
    table.row();

    buildConsumerBars(table);
  }

  public void buildConsumerBars(Table bars){
    if(consumer.current != null){
      consumer.current.forAll(o -> o.buildBars(self(), bars));
    }
  }

  @Annotations.Replace(1)
  public void displayConsumption(Table table){
    Runnable rebuild = () -> {
      Table t = new Table();
      consumer.build(t);

      table.clear();
      table.defaults().padTop(3).padBottom(3);
      table.left();

      SnapshotSeq<Element> array = t.getChildren();
      Element[] items = array.begin();
      for(int i = 0, n = array.size; i < n; i++){
        Element child = items[i];

        table.add(child);
        if((i + 1) % 6 == 0){
          table.row();
        }
      }
      array.end();
    };
    rebuild.run();

    if(consumer != null) table.update(() -> {
      if(updateRecipe){
        rebuild.run();
      }
    });
  }

  @Annotations.Replace(1)
  public void onControlSelect(Unit player){
    Reflection.accessField(PlacementFragment.class, Object.class, "lastDisplayState").set(ui.hudfrag.blockfrag, null);
  }

  @Annotations.Replace(1)
  public Object config(){
    return consumeCurrent;
  }

  @Annotations.Replace(1)
  public void drawStatus(){
    if(block.enableDrawStatus && getConsumerBlock().recipeConsumers().size > 0){
      float multiplier = block.size > 1 ? 1.0F : 0.64F;
      float brcx = tile.drawx() + (float)(block.size * 8) / 2.0F - 8 * multiplier / 2;
      float brcy = tile.drawy() - (float)(block.size * 8) / 2.0F + 8 * multiplier / 2;
      Draw.z(71.0F);
      Draw.color(Pal.gray);
      Fill.square(brcx, brcy, 2.5F * multiplier, 45.0F);
      Draw.color(status().color);
      Fill.square(brcx, brcy, 1.5F * multiplier, 45.0F);
      Draw.color();
    }
  }

  public void drawActivation(){
    if(activation <= 0.001f) return;

    int size = block.size;

    float lerp = Interp.pow2.apply(activation);
    Draw.color(SglDrawConst.fexCrystal);
    Draw.alpha(0.6f * lerp);
    Fill.circle(x, y, Mathf.lerp(size / 2f, size, lerp) * tilesize);

    GlyphLayout layout = GlyphLayout.obtain();
    layout.setText(Fonts.outline, Core.bundle.get("infos.overloadWarn"));

    float w = layout.width * 0.185f;
    float h = layout.height * 0.185f;

    layout.free();
    Draw.color(Color.darkGray, 0.6f);
    Fill.quad(
      x - w / 2 - 2, y + size * tilesize / 2f + h + 2,
      x - w / 2 - 2, y + size * tilesize / 2f - 2,
      x + w / 2 + 2, y + size * tilesize / 2f - 2,
      x + w / 2 + 2, y + size * tilesize / 2f + h + 2
    );

    Fonts.outline.draw(Core.bundle.get("infos.overloadWarn"), x, y + size * tilesize / 2f + h, Color.crimson, 0.185f, false, Align.center);

    Draw.z(Layer.effect);
    Lines.stroke(1.5f * lerp, SglDrawConst.fexCrystal);
    SglDraw.arc(x, y, Mathf.lerp(size / 2f, size, lerp) * tilesize, 360 * activation, Time.time * 1.5f);
  }

  /**
   * 具有输出物品的方块返回可能输出的物品，没有则返回null
   */
  public Seq<Item> outputItems(){
    return null;
  }

  /**
   * 具有输出液体的方块返回可能输出的液体，没有则返回null
   */
  public Seq<Liquid> outputLiquids(){
    return null;
  }

  public void reset(){
    resetConsumer();
  }

  public void resetConsumer(){
    if(items != null) items.clear();
    if(liquids != null) liquids.clear();
  }
}
