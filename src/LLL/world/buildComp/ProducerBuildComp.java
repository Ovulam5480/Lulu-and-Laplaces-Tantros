package LLL.world.buildComp;

import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
import LLL.world.modules.*;
import LLL.world.recipe.*;
import LLL.world.recipe.consumers.*;
import LLL.world.recipe.producers.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

@Annotations.EntityComponent
abstract class ProducerBuildComp<T extends Building & ProducerBuildc<T>> implements ConsumerBuildc<T>{
  @Annotations.Import
  int consumeCurrent;

  @Annotations.Import
  LiquidModule liquids;
  @Annotations.Import
  ItemModule items;

  @Annotations.Import
  Team team;
  @Annotations.Import
  float x, y, health, maxHealth;
  @Annotations.Import
  Tile tile;
  @Annotations.Import
  boolean enabled = true;
  @Annotations.Import
  BaseConsumeModule<T> consumer;
  @Annotations.Import
  Seq<LiquidStack> displayLiquids = new Seq<>();

  @Annotations.Import
  Block block;

  public float progress, totalProgress, warmup;
  public float powerProdEfficiency;
  public BaseProductModule<T> producer;

  private final Seq<Liquid> tempLiquid = new Seq<>();

  public Seq<Item> outputItems;
  public Seq<Liquid> outputLiquids;

  public Floatf<T> currBoost = e -> 1;
  public float real;
  public int mark;

  public ProducerBuildc<T> getProducerBuild(){
    return self();
  }

  public ProducerBlockComp<T> getProducerBlock(){
    return (ProducerBlockComp<T>)block;
  }

  /**
   * 当前选择的生产项目的索引
   */
  public int produceCurrent(){
    return consumeCurrent();
  }

  public float prodMultiplier(){
    return consMultiplier();
  }

  @Override
  public void update(){
    updateProducer();
    updateFactory();
  }

  public void updateProducer(){
    producer.update();
  }

  public void updateFactory(){
    /*当未选择配方时不进行更新*/
    if(produceCurrent() == -1 || producer.current == null){
      warmup = Mathf.lerpDelta(warmup, 0, getProducerBlock().stopSpeed());
      return;
    }

    if(shouldConsume() && consumeValid()){
      progress = progress + progressIncrease(consumer.current.craftTime);
      warmup = Mathf.lerpDelta(warmup, 1, getProducerBlock().warmupSpeed());

      onCraftingUpdate();
    }else{
      warmup = Mathf.lerpDelta(warmup, 0, getProducerBlock().stopSpeed());
    }

    totalProgress = (totalProgress + consumer.consDelta());

    while(progress >= 1){
      progress = progress - 1;
      consumer.trigger();
      producer.trigger();

      craftTrigger();
    }

    real = Mathf.lerpDelta(real, currBoost.get(self()), 0.05f);

    if(mark > 0 && --mark <= 0){
      currBoost = e -> 1;
    }

    if(updateRecipe() && producer.current != null){
      if(producer.current.get(ResourceType.item) != null)
        outputItems = new Seq<>(producer.current.<ProduceItems<T>>get(ResourceType.item).items).map(e -> e.item);
      if(producer.current.get(ResourceType.liquid) != null)
        outputLiquids = new Seq<>(producer.current.<ProduceLiquids<T>>get(ResourceType.liquid).liquids).map(e -> e.liquid);
    }

    for(ProducerBlockComp.Byproduct byproduct : getProducerBlock().byproducts().values()){
      dump(byproduct.item);
    }
  }

  @Override
  public void afterCreate(){
    createProducer();
  }


  public void createProducer(){
    producer = new BaseProductModule<>(self());
  }

  /**
   * 当前生产是否可用
   */
  public boolean productValid(){
    return productValidProducer();
  }

  public boolean productValidProducer(){
    return producer == null || producer.valid();
  }

  /**
   * 当前是否应当执行生产项更新
   */
  public boolean shouldProduct(){
    return shouldProductProducer();
  }

  public boolean shouldProductProducer(){
    return producer != null && produceCurrent() != -1;
  }

  public void buildProducerBars(Table bars){
    if(consumer.current != null){
      producer.current.forAll(o -> o.buildBars(self(), bars));
    }
  }

  /**
   * 当前机器的工作效率，0-1
   */
  public float workEfficiency(){
    return consEfficiency();
  }

  /**
   * 机器工作的销量增量，标准情况下是生产时间的倒数，乘以额外的增量返回
   *
   * @param baseTime 当前执行的消耗的基准耗时
   */
  public float progressIncrease(float baseTime){
    return 1 / baseTime * consumer.consDelta();
  }

  public void craftTrigger(){
    ProducerBlockComp<T> b = getProducerBlock();
    b.craftEffect().at(getX(), getY(), b.craftEffectColor());
    if(b.craftTrigger() != null) b.craftTrigger().get(self());
    if(b.craftedSound() != Sounds.none) b.craftedSound().at(x, y, 1, b.craftedSoundVolume());
  }

  public void onCraftingUpdate(){
    ProducerBlockComp<T> b = getProducerBlock();
    if(Mathf.chanceDelta(b.updateEffectChance())){
      b.updateEffect().at(getX() + Mathf.range(b.effectRange() * 4f), getY() + Mathf.range(b.effectRange() * 4), b.updateEffectColor());
    }

    if(b.crafting() != null) b.crafting().get(self());
  }

//    @Override
//    public float activeSoundVolume() {
//      return loopSoundVolume*workEfficiency();
//    }

  @Annotations.Replace(2)
  @Override
  public float consEfficiency(){
    return consEfficiencyProducer();
  }

  public float consEfficiencyProducer(){
    return real * consEfficiencyConsumer() * warmup;
  }

  @Annotations.Replace(2)
  @Override
  public boolean shouldConsume(){
    return shouldConsumeProducer();
  }

  public boolean shouldConsumeProducer(){
    return shouldConsumeConsumer() && productValid();
  }

  @Annotations.Replace(2)
  @Override
  public BlockStatus status(){
    return statusProducer();
  }

  public BlockStatus statusProducer(){
    if(getProducerBlock().autoSelect() && !getProducerBlock().canSelect() && consumeCurrent == -1)
      return BlockStatus.noInput;
    return statusConsumer();
  }

  @Annotations.Replace(2)
  public float getPowerProdEfficiency(){
    return getPowerProduction();
  }

  @Annotations.Replace(2)
  public float getPowerProduction(){
    return getPowerProductionProducer();
  }

  public float getPowerProductionProducer(){
    if(!block.outputsPower || producer.current == null || producer.current.get(ResourceType.power) == null) return 0;
    powerProdEfficiency = Mathf.num(shouldConsume() && consumeValid()) * consEfficiency() * producer.current.get(ResourceType.power).multiple(self());
    return producer.getPowerProduct() * powerProdEfficiency;
  }

  public abstract boolean dump(Item item);

  @Annotations.Replace(2)
  @Override
  public Seq<Item> outputItems(){
    if(consumeCurrent == -1) return null;
    return outputItems;
  }

  @Annotations.Replace(2)
  @Override
  public Seq<Liquid> outputLiquids(){
    if(consumeCurrent == -1) return null;
    return outputLiquids;
  }

  /**
   * 对生产进行处理，当产出者是Building时需要对产出项进行正确的统计，则当工厂handleItem并传入本身时，将此次添加的物品加入统计
   */
  public void handleItem(Building source, Item item){
    if(source == self()){
      source.produced(item);
    }
  }

  @Annotations.Replace(2)
  @Override
  public void updateDisplayLiquid(){
    if(!block.hasLiquids) return;
    displayLiquids.clear();

    tempLiquid.clear();
    if(consumeCurrent >= 0 && consumer.current != null){
      if(consumer.current.get(ResourceType.liquid) != null){
        for(LiquidStack stack : consumer.current.<ConsumeLiquidBaseR<T>>get(ResourceType.liquid).consLiquids){
          tempLiquid.add(stack.liquid);
        }
      }
    }
    if(consumeCurrent >= 0 && producer.current != null){
      if(producer.current.get(ResourceType.liquid) != null){
        for(LiquidStack stack : producer.current.<ProduceLiquids<T>>get(ResourceType.liquid).liquids){
          tempLiquid.add(stack.liquid);
        }
      }
    }
    liquids.each((key, val) -> {
      if(!tempLiquid.contains(key) && val > 0.1f) displayLiquids.add(new LiquidStack(key, val));
    });
  }

  @Override
  public void displayBars(Table bars){
    if(consumeCurrent == -1 || producer.current == null || consumer.current == null) return;

    Table tab = new Table();
    buildProducerBars(tab);

    if(tab.hasChildren()){
      bars.row();
      bars.add(Iconc.upload + Core.bundle.get("fragment.bars.product")).left().padBottom(0);

      SnapshotSeq<Element> el = tab.getChildren();
      Element[] items = el.begin();
      for(int i = 0, b = el.size; i < b; i++){
        bars.row();
        bars.add(items[i]);
      }
      el.end();

      bars.row();
      bars.image().color(Color.darkGray).growX().colspan(2).height(4).padTop(3).padBottom(3).padLeft(-14).padRight(-14);
    }
  }

  @Annotations.Replace(2)
  public void buildConfiguration(Table table){
    Seq<BaseProducers<T>> producers = getProducerBlock().producers();
    Seq<BaseConsumers<T>> consumers = getProducerBlock().consumers();

    if(getProducerBlock().producers().size > 1 && getProducerBlock().canSelect()){
      table.table(Tex.buttonTrans, prescripts -> {
        prescripts.defaults().grow().marginTop(0).marginBottom(0).marginRight(5).marginRight(5);

        prescripts.add(Core.bundle.get("fragment.buttons.selectPrescripts")).padLeft(5).padTop(5).padBottom(5);
        prescripts.row();

        prescripts.pane(buttons -> {
          for(int i = 0; i < producers.size; i++){
            int s = i;
            BaseProducers<T> p = producers.get(i);
            BaseConsumers<T> c = consumers.get(i);

            if(c.selectable.get() == BaseConsumers.Visibility.hidden) continue;

            buttons.left().button(t -> {
                t.left().defaults().left();
                ProducerBlockComp.buildRecipeSimple(c, p, t);
              }, Styles.underlineb, () -> configure(s))
              .touchable(() -> c.selectable.get().buttonValid)
              .update(b -> b.setChecked(consumeCurrent == s))
              .fillY().growX().left().margin(5).marginTop(8).marginBottom(8).pad(4)
              .get().addListener(new Tooltip(t -> t.table(Tex.paneLeft, detail -> ProducerBlockComp.buildRecipe(detail, c, p))));

            buttons.row();
          }
        }).fill().maxHeight(280);
      });

      table.row();
    }
  }

  public abstract void configure(Object value);

  @Annotations.Replace(2)
  @Override
  public double sense(LAccess sensor){
    return senseCrafter(sensor);
  }

  public double senseCrafter(LAccess sensor){
    if(sensor == LAccess.progress){
      return progress;
    }

    return senseBuilding(sensor);
  }

  public void write(Writes write){
    write.bool(recipeSelected());
  }

  public void read(Reads read, byte revision){
    recipeSelected(read.bool());
  }

  @Annotations.Replace(2)
  @Override
  public void reset(){
    resetProducer();
  }

  public void resetProducer(){
    resetConsumer();
    progress = 0;
  }
}
