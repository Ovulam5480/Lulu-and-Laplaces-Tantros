package lulu.world.components.buildEntityComps;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.type.*;
import lulu.world.components.blockComps.*;
import lulu.world.draw.payload.*;
import lulu.world.modules.*;
import lulu.world.move.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static arc.Core.*;

@Annotations.EntityComponent
abstract class MultiPayloadCrafterBuildComp<T extends Building & MultiPayloadCrafterBuildc<T>> implements ProducerBuildc<T>, MultiPayloadBuildc{
  //输入到载荷内作为材料的载荷
  public Seq<PositionPayload> inputPayloads = new Seq<>();
  //作为材料的载荷(inputPayloads)当中, 用于当前工作的载荷
  public Seq<PositionPayload> craftPayloads = new Seq<>();
  //输出的产物载荷
  public ObjectMap<PositionPayload, Integer> outputPayloads = new ObjectMap<>();

  public FrameBuffer inputFrameBuffer = new FrameBuffer();
  public FrameBuffer craftFrameBuffer = new FrameBuffer();
  public FrameBuffer outputFrameBuffer = new FrameBuffer();

  public float cachePayloadsAlpha = 1f;
  public float craftPayloadsAlpha = 1f;
  public float outputPayloadsAlpha = 1f;

  public Seq<PositionPayload> temp = new Seq<>();

  @Annotations.Import
  Block block;
  @Annotations.Import
  PositionPayloadModule positionPayloads;

  private static final ObjectIntMap<UnlockableContent> counter = new ObjectIntMap<>();

  public MultiPayloadCrafterBlockComp<T> getMultiPayloadCrafterBlock(){
    return (MultiPayloadCrafterBlockComp)block;
  }

  public void buildConfiguration(Table table){
    buildConfigurationMultiPayloadCrafter(table);
  }

  public void buildConfigurationMultiPayloadCrafter(Table table){
    table.table(sliders -> {
      sliders.slider(0, 1f, 0.01f, cachePayloadsAlpha, f -> cachePayloadsAlpha = f);
      sliders.row();
      sliders.slider(0, 1f, 0.01f, craftPayloadsAlpha, f -> craftPayloadsAlpha = f);
      sliders.row();
      sliders.slider(0, 1f, 0.01f, outputPayloadsAlpha, f -> outputPayloadsAlpha = f);
    });
  }

  public void draw(){
    drawMultiPayloadCrafter();
  }

  public void drawMultiPayloadCrafter(){
    inputFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    craftFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    outputFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

    //渲染非加工区的输入载荷
    if(cachePayloadsAlpha > 0){
      Seq<PositionPayload> in = inputPayloads.select(pp -> !craftPayloads.contains(pp));
      drawAlphaPayloads(inputFrameBuffer, in, cachePayloadsAlpha);
    }

    //渲染加工区材料载荷 或者 加工效果
    if(craftPayloadsAlpha > 0){
      if(efficiency() > 0){
        drawAlphaPayloadManagers(craftFrameBuffer, currentConsumerPayload().drawCrafting, craftPayloadsAlpha, true);
        drawAlphaPayloadManagers(craftFrameBuffer, currentProducerPayload().drawCrafting, craftPayloadsAlpha, false);
      }else drawAlphaPayloads(craftFrameBuffer, craftPayloads, craftPayloadsAlpha);
    }

    //总是渲染输出载荷
    if(outputPayloadsAlpha > 0){
      drawAlphaPayloads(outputFrameBuffer, outputPayloads.keys().toSeq(), outputPayloadsAlpha);
    }
  }

  //渲染载荷
  public void drawAlphaPayloads(FrameBuffer buffer, Seq<PositionPayload> payloads, float alpha){
    Draw.draw(Layer.blockOver, () -> {
      buffer.begin(Color.clear);
      payloads.each(PositionPayload::draw);
      buffer.end();

      Draw.alpha(alpha);
      Tmp.tr1.set(Draw.wrap(buffer.getTexture()));
      Tmp.tr1.flip(false, true);

      //为什么是 4/镜头缩放, 我也不知道, 我的直觉告诉我的
      Draw.scl(4 / Vars.renderer.getDisplayScale());
      Draw.rect(Tmp.tr1, camera.position.x, camera.position.y);
      Draw.scl();
    });
  }

  //渲染载荷加工效果
  public void drawAlphaPayloadManagers(FrameBuffer buffer, DrawRecipePayload drawCrafting, float alpha, boolean isInput){
    Draw.draw(Layer.blockOver, () -> {
      buffer.begin(Color.clear);

      if(isInput){
        drawCrafting.draw(null, self(), 1 - progress());
      }else{
        drawCrafting.draw(null, self(), progress());
      }

      buffer.end();

      Draw.alpha(alpha);
      Tmp.tr1.set(Draw.wrap(buffer.getTexture()));
      Tmp.tr1.flip(false, true);

      Draw.scl(4 / Vars.renderer.getDisplayScale());
      Draw.rect(Tmp.tr1, camera.position.x, camera.position.y);
      Draw.scl();
    });
  }

  @Annotations.Replace(2)
  public boolean acceptPayload(Building source, Payload payload){
    return acceptPayloadMultiPayloadCrafterBuild(source, payload);
  }

  public ConsumeMultiPayload<T> currentConsumerPayload(){
    return consumer().current.get(ResourceType.payload);
  }

  public ProduceMultiPayload<T> currentProducerPayload(){
    return producer().current.get(ResourceType.payload);
  }

  public boolean acceptPayloadMultiPayloadCrafterBuild(Building source, Payload payload){
    //当前配方不需要载荷, 或者不需要该载荷
    if(!getConsumerBlock().filter().filter(self(), ResourceType.payload, payload.content(), false)) return false;

    ConsumeMultiPayload<T> currentConsumerPayload = currentConsumerPayload();
    if(currentConsumerPayload != null){
      //第一倍的载荷输入到moveIn,所以总是能够输入
      //以外的载荷输入到moveCache

      int material = 0;
      for(PayloadStack stack : currentConsumerPayload.payloads){
        if(stack.item == payload.content()){
          material = stack.amount;
          break;
        }
      }

      int place = material + getMultiPayloadCrafterBlock().moveCache().maxCapacity(block);

      for(PayloadStack ps : currentConsumerPayload.payloads){
        place -= Math.max(positionPayloads.payloads.get(ps.item) - (ps.item == payload.content() ? 0 : ps.amount), 0);
      }
      return place > 0;
    }else{
      return false;
    }
  }

  @Override
  public void update(){
    checkPositionPayloads();
    moveOutPayloads();
    moveInPayloads();
    dumpOutputs();
    if(updateRecipe() && getMultiPayloadCrafterBlock().changeClear()){
      positionPayloads().payloads.payloads.each(pp -> Fx.breakBlock.at(pp.x(self()), pp.y(self()), pp.payload.size() / 8f));
      positionPayloads().payloads.clear();
      outputPayloads.clear();
    }
  }

  //输出载荷 的最小索引
  public int getMinimumIndex(){
    Seq<Integer> integers = outputPayloads.values().toSeq();
    for(int i = 0; i < integers.size; i++){
      if(!integers.contains(i)) return i;
    }
    return integers.size;
  }

  public int getOutMoverCapacity(){
    return getMultiPayloadCrafterBlock().moveProduct().maxCapacity(block) * getMultiPayloadCrafterBlock().outMoverCapacityMulti();
  }

  //区分建筑内的载荷是输入(输入 和 缓存)还是输出
  public void checkPositionPayloads(){
    inputPayloads.clear();
    craftPayloads.clear();

    if(!consumer().valid){
      //配方中不存在输入载荷时，所有载荷都应该输出
      temp.selectFrom(positionPayloads.payloads.payloads, p -> !outputPayloads.containsKey(p));
    }else{
      //否则将所以非输入的载荷设为输出
      for(PositionPayload pp : positionPayloads.payloads.payloads){
        //如果该 载荷 包含在 加工载荷(使用moveIn) 中
        if(getConsumerBlock().filter().filter(self(), ResourceType.payload, pp.payload.content(), false)){
          inputPayloads.add(pp);

          //判断数量，如果加工载荷能容纳该载荷，则加入该载荷
          int amount = counter.get(pp.content(), 0);
          int required = 0;

          for(PayloadStack stack : currentConsumerPayload().payloads){
            if(stack.item == pp.content()){
              required = stack.amount;
              break;
            }
          }

          if(amount < required){
            craftPayloads.add(pp);
            counter.increment(pp.content(), 1);
          }
        }
      }
      //非输入载荷的载荷, 都应该输出
      temp.selectFrom(positionPayloads.payloads.payloads, p -> !inputPayloads.contains(p) && !outputPayloads.containsKey(p));
    }
    temp.each(pp -> outputPayloads.put(pp, getMinimumIndex()));
    temp.clear();
    counter.clear();
  }

  public int sideAmount(){
    return (block.size - 1) / 2;
  }

  //输入的载荷优先填充moveIn，然后填充moveCache
  //不以moveInMover的容量去判断moveIn是否填满，加工区容纳一次加工所需要的所以载荷
  public void moveInPayloads(){
    ConsumeMultiPayload<T> consumeMultiPayload = currentConsumerPayload();

    //输入判断，所有非输入的载荷都应该输出
    int i = 0;
    for(PositionPayload pp : craftPayloads){
      counter.increment(pp.content(), 1);
      MovePayload movePayload = consumeMultiPayload.movePayloads.get(pp.content());

      if(movePayload == null){
        pp.targetPosition = setTargetPosition(i, getMultiPayloadBlock().moveInMover());
      }else{
        pp.targetPosition = setTargetPosition(counter.get(pp.content()), movePayload);
      }

      counter.increment(pp.content(), 1);
      i++;
    }
    counter.clear();

    int place = 0;
    for(PositionPayload pp : inputPayloads){
      int index = inputPayloads.indexOf(pp);
      //如果载荷属于加工区载荷
      if(!craftPayloads.contains(pp)){
        pp.targetPosition = setTargetPosition(index - place, getMultiPayloadCrafterBlock().moveCache());
      }else{
        place++;
      }
      updatePayload(pp);
    }
  }

  //输出载荷
  public void moveOutPayloads(){
    outputPayloads.each((pp, integer) -> {
      //如果载荷的索引 大于 sideAmount()，并且输出载荷不包含 （索引 减去 sideAmount()）的值
      //该载荷“前方”有空位，改变索引移动到空位
      if(integer >= sideAmount() && !outputPayloads.containsValue(integer - sideAmount(), true)){
        integer -= sideAmount();
      }

      //如果 输出区存在空间, 索引改为空间的索引(查找合适的位置)
      if(integer >= getOutMoverCapacity()) integer = Math.min(integer, getMinimumIndex());
        //否则更新载荷位置
      else updatePayload(pp);

      outputPayloads.put(pp, integer);
      pp.targetPosition = setTargetPosition(integer, getMultiPayloadCrafterBlock().moveProduct());
    });
  }

  public void dumpOutputs(){
    outputPayloads.each((pp, integer) -> {
      //如果载荷位于边缘 并且载荷已经到达目标位置
      if(integer < sideAmount() && pp.hasArrived()){
        //如果能够输出到某种建筑, 移除该载荷
        if(dumpPositionPayload(pp)){
          temp.add(pp);
        }else if(pp.payload.dump()){//如果(单位)载荷自己输出
          //todo 单位输出时 并且 面前有建筑时，应当给堵上
          temp.add(pp);
          positionPayloads.payloads.payloads.remove(pp);
        }
      }
    });
    temp.each(pp -> outputPayloads.remove(pp));
    temp.clear();
  }
}
