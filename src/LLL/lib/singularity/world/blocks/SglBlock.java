package LLL.lib.singularity.world.blocks;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.*;
import LLL.lib.singularity.world.consumers.*;
import LLL.lib.singularity.world.draw.*;
import LLL.lib.singularity.world.modules.*;
import LLL.lib.singularity.world.particles.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import universecore.annotations.*;
import universecore.components.*;
import universecore.components.blockcomp.*;
import universecore.util.*;
import universecore.util.handler.*;
import universecore.world.consumers.*;
import universecore.world.meta.*;
import universecore.world.particles.models.*;

import static mindustry.Vars.*;

/**
 * 此mod的基础方块类型，对block添加了完善的consume系统，并拥有中子能的基础模块
 */
@Annotations.ImplEntries
public class SglBlock extends Block implements ConsumerBlockComp, PostAtlasGenerator{
  public static final int BASE_EXBLOSIVE_ENERGY = 128;
  public boolean autoSelect = false;
  public boolean canSelect = true;

  public boolean outputItems;

  public final Seq<BaseConsumers> consumers = new Seq<>();

  public DrawBlock draw = new DrawDefault();

  /**
   * 这是一个指针，用于标记当前编辑的consume
   */
  public SglConsumers consume;
  /**
   * 独立的物品栏，为true所有物品具有独立的容量，否则共用物品空间
   */
  public boolean independenceInventory = true;
  /**
   * 独立的液体储罐，为true所有液体具有独立的容量，否则共用液体空间
   */
  public boolean independenceLiquidTank = true;
  /**
   * 控制在多种可选输入都可用时是全部可用还是其中优先级最高的一种使用
   */
  public boolean oneOfOptionCons = true;

  public Cons<SglBuilding> initialed;
  public Cons<SglBuilding> updating;

  /**
   * 是否显示液体槽
   */
  public boolean displayLiquid = true;

  public String liquidsStr = Iconc.liquidWater + Core.bundle.get("fragment.bars.liquids");
  public String recipeIndfo = Core.bundle.get("fragment.buttons.selectPrescripts");

  public SglBlock(String name){
    super(name);
    update = true;
    consumesPower = false;
    appliedConfig();
    config(byte[].class, (SglBuilding e, byte[] code) -> {
      if(code.length == 0) return;
      parseConfigObjects(e, DataPackable.readObject(code, e));
    });
  }

  public void appliedConfig(){
    config(Integer.class, (SglBuilding e, Integer i) -> {
      if(consumers().size > 1){
        if(canSelect) e.recipeSelected = true;
        e.reset();
        if(e.recipeCurrent == i || i == -1){
          e.recipeCurrent = -1;
          e.recipeSelected = false;
        }else e.recipeCurrent = i;
      }
    });
    configClear((SglBuilding e) -> {
      e.recipeSelected = false;
      e.recipeCurrent = 0;
    });
  }

  public void parseConfigObjects(SglBuilding e, Object obj){
  }

  @Override
  public BaseConsumers newConsume(){
    consume = new SglConsumers(false);
    this.consumers().add(consume);

    return consume;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends ConsumerBuildComp> BaseConsumers newOptionalConsume(Cons2<T, BaseConsumers> validDef, Cons2<Stats, BaseConsumers> displayDef){
    consume = new SglConsumers(true){{
      this.optionalDef = (Cons2<ConsumerBuildComp, BaseConsumers>)validDef;
      this.display = displayDef;
    }};
    this.optionalCons().add(consume);
    return consume;
  }

  @Override
  public void init(){
    if(consumers().size > 1){
      configurable = true;
      saveConfig = true;
    }

    Seq<BaseConsumers> consume = new Seq<>();
    if(consumers().size > 0) consume.addAll(consumers());
    if(optionalCons().size > 0) consume.addAll(optionalCons());
    for(BaseConsumers cons : consume){
      if(cons.get(SglConsumeType.item) != null){
        hasItems = true;
        if(cons.craftTime == 0) cons.time(90f);
      }
      hasLiquids |= cons.get(SglConsumeType.liquid) != null;
      hasPower |= consumesPower |= cons.get(SglConsumeType.power) != null;
    }

    for(BaseConsumers consumer : consumers()){
      acceptsPayload |= consumer.get(ConsumeType.payload) != null;
    }

    super.init();
  }

  @Override
  public void load(){
    super.load();
    draw.load(this);
  }

  @Override
  public void setStats(){
    stats.add(Stat.size, "@x@", size, size);
    stats.add(Stat.health, health, StatUnit.none);
    if(canBeBuilt()){
      stats.add(Stat.buildTime, buildTime / 60, StatUnit.seconds);
      stats.add(Stat.buildCost, StatValues.items(false, requirements));
    }

    if(instantTransfer){
      stats.add(Stat.maxConsecutive, 2, StatUnit.none);
    }

    if(this.hasLiquids) this.stats.add(Stat.liquidCapacity, this.liquidCapacity, StatUnit.liquidUnits);

    if(this.hasItems && this.itemCapacity > 0)
      this.stats.add(Stat.itemCapacity, (float)this.itemCapacity, StatUnit.items);

    if(!optionalCons().isEmpty()){
      stats.add(UncStat.optionalInputs, t -> {
        t.left().row();
        for(BaseConsumers con : optionalCons()){
          t.table(SglDrawConst.grayUIAlpha, ta -> {
            ta.left().defaults().left();
            FactoryBlockComp.buildRecipe(ta, con, null);
          }).growX().fillY().pad(6).left().margin(10);
          t.row();
        }
      });
    }
  }

  @Override
  public boolean outputsItems(){
    return hasItems && outputItems;
  }

  @Override
  public void setBars(){
    addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
  }

  @Override
  public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
    draw.drawPlan(this, plan, list);
  }

  @Override
  public void getRegionsToOutline(Seq<TextureRegion> out){
    draw.getRegionsToOutline(this, out);
  }

  @Override
  public TextureRegion[] icons(){
    return draw.finalIcons(this);
  }

  @Override
  public void postLoad(){
    if(draw instanceof DrawAtlasGenerator gen) gen.postLoad(this);
  }

  @Override
  public void createIcons(MultiPacker packer){
    super.createIcons(packer);
    if(draw instanceof DrawAtlasGenerator gen) gen.generateAtlas(this, packer);
  }

  @Annotations.ImplEntries
  public class SglBuilding extends Building implements ConsumerBuildComp, ExtraVariableComp{
    private static final Seq<Liquid> temp = new Seq<>();
    private static final FieldHandler<PlacementFragment> fieldHandler = new FieldHandler<>(PlacementFragment.class);

    public SglConsumeModule consumer;

    public boolean recipeSelected;

    protected final Seq<LiquidStack> displayLiquids = new Seq<>();

    @Annotations.FieldKey("consumeCurrent")
    public int recipeCurrent = -1;
    public int lastRecipe;
    public boolean updateRecipe;

    public int select;

    public float activation;
    public float activateRecover;

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);

      liquids = new SglLiquidModule();

      if(consumers().size == 1) recipeCurrent = 0;

      consumer = new SglConsumeModule(this);

      return this;
    }

    @Override
    public SglLiquidModule liquids(){
      return (SglLiquidModule)liquids;
    }

    @Override
    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
      super.init(tile, team, shouldAdd, rotation);
      if(initialed != null){
        initialed.get(this);
      }
      return this;
    }

    @Override
    public void onControlSelect(Unit player){
      super.onControlSelect(player);
      FieldHandler.setValueDefault(Vars.ui.hudfrag.blockfrag, "lastDisplayState", null);
    }

    @Override
    public boolean shouldConsume(){
      return super.shouldConsume() && ConsumerBuildComp.super.shouldConsume();
    }


    public float efficiency(){
      return consEfficiency();
    }

    public void dumpLiquid(){
      liquids.each((l, n) -> dumpLiquid(l));
    }

    @Override
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

    @Override
    public void drawStatus(){
      if(this.block.enableDrawStatus && this.block().consumers().size > 0){
        float multiplier = block.size > 1 ? 1.0F : 0.64F;
        float brcx = this.tile.drawx() + (float)(this.block.size * 8) / 2.0F - 8 * multiplier / 2;
        float brcy = this.tile.drawy() - (float)(this.block.size * 8) / 2.0F + 8 * multiplier / 2;
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

    @Override
    public boolean consumeValid(){
      return enabled && ConsumerBuildComp.super.consumeValid();
    }

    @Override
    public BlockStatus status(){
      return !enabled ? BlockStatus.logicDisable : consumer.status();
    }

    @Override
    public void consume(){
      consumer.trigger();
    }

    public boolean updateValid(){
      return true;
    }

    @Override
    public float consEfficiency(){
      return consumer.hasConsume() ? consumer.consEfficiency : 1;
    }

    @Override
    public void update(){
      updateRecipe = false;
      if(!recipeSelected && autoSelect && consumer.hasConsume() && (consumer.current == null || !consumer.valid())){
        int f = -1;
        for(BaseConsumers ignored : consumers()){
          int n = select % consumers().size;
          if(consumer.valid(n) && consumers().get(n).selectable.get() == BaseConsumers.Visibility.usable){
            f = n;
            break;
          }
          select = (select + 1) % consumers().size;
        }

        if(recipeCurrent != f && f >= 0){
          recipeCurrent = f;
        }
      }

      if(lastRecipe != recipeCurrent) updateRecipe = true;
      lastRecipe = recipeCurrent;

      if(updateRecipe) fieldHandler.setValue(ui.hudfrag.blockfrag, "wasHovered", false);

      super.update();

      if(updating != null) updating.get(this);

      activateRecover = Mathf.approachDelta(activateRecover, 0.005f, 0.0001f);
      activation = Mathf.approachDelta(activation, 0, activateRecover);

      if(Mathf.chanceDelta(0.1f * activation)){
        SglFx.neutronWeaveMicro.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4), SglDrawConst.fexCrystal);
      }

      if(activation >= 0.5f){
        damageContinuousPierce(maxHealth * Math.min(activation - 0.5f, 0.5f) / 60);

        if(Mathf.chanceDelta(0.1f * Mathf.maxZero(activation - 0.5f))){
          Angles.randLenVectors(System.nanoTime(), 1, 2, 3.5f,
            (x, y) -> SglParticleModels.floatParticle.create(this.x, this.y, SglDrawConst.fexCrystal, x, y, 2.6f).setVar(RandDeflectParticle.STRENGTH, 0.4f)
          );
        }
      }
    }

    public void updateDisplayLiquid(){
      displayLiquids.clear();
      temp.clear();
      if(recipeCurrent >= 0 && consumer.current != null && consumer.current.get(SglConsumeType.liquid) != null){
        for(LiquidStack stack : consumer.current.get(SglConsumeType.liquid).consLiquids){
          temp.add(stack.liquid);
        }
      }
      liquids.each((key, val) -> {
        if(!temp.contains(key) && val > 0.1f) displayLiquids.add(new LiquidStack(key, val));
      });
    }

    public void reset(){
      if(items != null) items.clear();
      if(liquids != null) liquids.clear();
    }

    public Object config(){
      return recipeCurrent;
    }

    @Override
    public void displayBars(Table bars){
      super.displayBars(bars);

      //显示流体存储量
      if(hasLiquids && displayLiquid) updateDisplayLiquid();
      if(!displayLiquids.isEmpty()){
        bars.top().left().add(liquidsStr).left().padBottom(0);
        bars.row();
        for(LiquidStack stack : displayLiquids){
          bars.add(new Bar(
            () -> stack.liquid.localizedName,
            () -> stack.liquid.barColor != null ? stack.liquid.barColor : stack.liquid.color,
            () -> Math.min(liquids.get(stack.liquid) / block().liquidCapacity, 1f)
          )).growX();
          bars.row();
        }
      }

      if(recipeCurrent == -1 || consumer.current == null) return;

      bars.defaults().grow().margin(0).padTop(3).padBottom(3);
      bars.add(Iconc.download + Core.bundle.get("fragment.bars.consume")).left().padBottom(0);
      bars.row();

      buildConsumerBars(bars);
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

    public boolean acceptAll(ConsumeType<?> type){
      return autoSelect && (!canSelect || !recipeSelected);
    }

    @Override
    public boolean acceptItem(Building source, Item item){
      return source.interactable(this.team) && hasItems
        && ((source == this && consumer.current != null && consumer.current.selfAccess(ConsumeType.item, item))
        || !(consumer.hasConsume() || consumer.hasOptional())
        || filter().filter(this, SglConsumeType.item, item, acceptAll(SglConsumeType.item)))
        && (independenceInventory ? items.get(item) : items.total()) < getMaximumAccepted(item);
    }

    @Override
    public boolean acceptLiquid(Building source, Liquid liquid){
      return source.interactable(this.team) && hasLiquids
        && ((source == this && consumer.current != null && consumer.current.selfAccess(ConsumeType.liquid, liquid))
        || !(consumer.hasConsume() || consumer.hasOptional())
        || filter().filter(this, SglConsumeType.liquid, liquid, acceptAll(SglConsumeType.liquid)))
        && (independenceLiquidTank ? liquids.get(liquid) : ((SglLiquidModule)liquids).total()) <= getMaximumAccepted(liquid) - 0.0001f;
    }

    public float getMaximumAccepted(Liquid liquid){
      return block.liquidCapacity;
    }

    @Override
    public void draw(){
      draw.draw(this);
      drawStatus();
      drawActivation();
    }

    @Override
    public void drawLight(){
      draw.drawLight(this);
    }


    public SglBlock block(){
      return SglBlock.this;
    }

//    @Override
//    public float activeSoundVolume() {
//      return warmup()*loopSoundVolume;
//    }
//
//    @Override
//    public boolean shouldActiveSound() {
//      return shouldConsume();
//    }

    @Override
    public byte version(){
      return 3;
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.i(select);
      write.f(activation);

      write.i(recipeCurrent);
      if(consumer != null) consumer.write(write);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      if(revision >= 2) select = read.i();
      if(revision >= 3) activation = read.f();

      recipeCurrent = read.i();
      if(consumer != null) consumer.read(read, revision <= 2);
    }
  }
}
