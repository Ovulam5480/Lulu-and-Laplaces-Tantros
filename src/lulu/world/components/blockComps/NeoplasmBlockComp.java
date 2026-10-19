package lulu.world.components.blockComps;

import lulu.*;
import lulu.content.*;
import lulu.content.extensions.*;
import lulu.world.components.buildComps.*;
import arc.*;
import arc.graphics.g2d.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public interface NeoplasmBlockComp{
  default Block getBlock(){
    return (Block)this;
  }

  @Annotations.BindField(value = "neoplasmHealScale", initialize = "8f")
  default float neoplasmHealScale(){
    return 0;
  }

  @Annotations.BindField("neoplasmHealScale")
  default void neoplasmHealScale(float value){
  }

  @Annotations.BindField(value = "neoplasmScaleSpeed", initialize = "0.1f")
  default float neoplasmScaleSpeed(){
    return 0;
  }

  @Annotations.BindField("neoplasmScaleSpeed")
  default void neoplasmScaleSpeed(float value){
  }

  @Annotations.BindField(value = "maxHealOnce", initialize = "3f")
  default float maxHealOnce(){
    return 0;
  }

  @Annotations.BindField(value = "maxHealOnce")
  default void maxHealOnce(float max){
  }

  //每秒
  @Annotations.BindField(value = "neoplasmAbsorbMulti", initialize = "1f")
  default float neoplasmAbsorbMulti(){
    return 0;
  }

  @Annotations.BindField("neoplasmAbsorbMulti")
  default void neoplasmAbsorbMulti(float value){
  }

  //每秒
  @Annotations.BindField(value = "neoplasmDecayMulti", initialize = "1f")
  default float neoplasmDecayMulti(){
    return 0;
  }

  @Annotations.BindField("neoplasmDecayMulti")
  default void neoplasmDecayMulti(float value){
  }

  @Annotations.BindField(value = "baseRegion")
  default TextureRegion baseRegion(){
    return null;
  }

  @Annotations.BindField(value = "baseRegion")
  default void baseRegion(TextureRegion region){
  }

  @Annotations.BindField(value = "requestStructure")
  default boolean requestStructure(){
    return false;
  }

  @Annotations.BindField(value = "requestStructure")
  default void requestStructure(boolean request){
  }

  @Annotations.BindField(value = "region")
  default TextureRegion region(){
    return null;
  }

  @Annotations.BindField(value = "region")
  default void region(TextureRegion region){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initNeoplasticBlock(){
    Block block = getBlock();

    block.hasItems = true;
    block.hasLiquids = true;//todo
    block.update = true;
    block.sync = true;
    block.drawCracks = false;
    block.createRubble = false;
    block.buildVisibility = BuildVisibility.sandboxOnly;
    block.category = OvulamCategory.neoplastic;
    block.group = OvulamBlockGroup.neoplastic;
    block.instantBuild = true;
    block.instantDeconstruct = true;
    block.placeEffect = Fx.none;
    block.rebuildable = false;
    block.breakEffect = block.destroyEffect = Fx.neoplasiaSmoke;
    block.replaceable = true;
    block.suppressable = true;

    block.placeSound = OvulamSounds.placeSounds.random();
    block.breakSound = block.destroySound = OvulamSounds.breakSounds.random();
    block.placePitchChange = block.breakPitchChange = false;

    //block.shownPlanets.addAll(Vars.content.planets());

    addNeoplasmBars(block);
    afterInit();
  }

  default void addNeoplasmBars(Block block){
    //block.addLiquidBar(Liquids.neoplasm);
    float cap = block.liquidCapacity;
    block.addBar("neoplasm", n -> new Bar(
      () -> String.format("%.2f", ((NeoplasmBuildComp)n).neoplasm().amount()),
      () -> Liquids.neoplasm.color,
      () -> ((NeoplasmBuildComp)n).neoplasm().amount() / cap));

    block.addBar("neoplasm-add", n -> new Bar(
      () -> String.format("%.2f", ((NeoplasmBuildComp)n).neoplasm().lastAdd),
      () -> Liquids.neoplasm.color,
      () -> ((NeoplasmBuildComp)n).neoplasm().lastAdd / cap));

    block.addBar("neoplasm-remove", n -> new Bar(
      () -> String.format("%.2f", ((NeoplasmBuildComp)n).neoplasm().lastRemove),
      () -> Liquids.neoplasm.color,
      () -> ((NeoplasmBuildComp)n).neoplasm().lastRemove / cap));

    block.addBar("neoplasm-remove", n -> new Bar(
      () -> String.format("%.2f", ((NeoplasmBuildComp)n).neoplasm().neoplasmMean.mean()),
      () -> Liquids.neoplasm.color,
      () -> ((NeoplasmBuildComp)n).neoplasm().neoplasmMean.mean() / cap));
  }

  default void afterInit(){

  }

  @Annotations.MethodEntry(entryMethod = "load", context = "name -> name")
  default void loadNeoplasmBlock(String name){
    baseRegion(Core.atlas.find(LuluMod.modName + "neoplasm-" + getBlock().size));
    region(Core.atlas.has(name) ? Core.atlas.find(name) : Core.atlas.find(name + "-icon"));
  }

  //需要手动调用, 并且方块的customShadow = true;
  default void drawScaledShadow(Tile tile){
    Draw.color(0f, 0f, 0f, BlockRenderer.shadowColor.a);
    Draw.scl(((NeoplasmBuildComp)tile.build).neoplasmScale());

    Drawf.squareShadow(tile.drawx(), tile.drawy(), getBlock().size * tilesize * 1.85f, 1);

    Draw.reset();
  }
}
