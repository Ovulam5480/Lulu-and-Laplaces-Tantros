package lulu.world.modules;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.recipe.*;
import lulu.world.recipe.consumers.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

/**
 * 生产者的消耗器模块，用于集中处理方块的材料需求等，提供了可选需求以及其特殊的触发器
 *
 * @author EBwilson
 */
public class BaseConsumeModule<T extends Building & ConsumerBuildc<T>> extends BlockModule{
  private static final float[] ZERO = {0};

  protected final T entity;
  protected final ObjectMap<BaseConsumers<T>, float[]> optProgress = new ObjectMap<>();
  protected final ObjectMap<BaseConsumers<T>, float[]> optEfficiency = new ObjectMap<>();

  public BaseConsumers<T> current;
  public BaseConsumers<T> optionalCurr;
  public boolean valid;

  public float consEfficiency, powerOtherEff;

  private float powerCons;

  public BaseConsumeModule(T entity){
    this.entity = entity;
    current = entity.getConsumerBlock().consumers() != null && entity.consumeCurrent() != -1 ?
      entity.getConsumerBlock().consumers().get(entity.consumeCurrent()) : null;
  }

  public void build(Table table){
    if(current != null) current.forAll(c -> c.build(entity, table));
  }

  public Seq<BaseConsumers<T>> get(){
    return entity.getConsumerBlock().consumers().as();
  }

  public Seq<BaseConsumers<T>> getOptional(){
    return entity.getConsumerBlock().optionalCons().as();
  }

  public BlockStatus status(){
    if(!entity.shouldConsume()){
      return BlockStatus.noOutput;
    }

    if(!valid){
      return BlockStatus.noInput;
    }

    return BlockStatus.active;
  }

  public boolean hasConsume(){
    return !get().isEmpty();
  }

  public boolean hasOptional(){
    return !getOptional().isEmpty();
  }

  public float getOptionalEff(BaseConsumers<T> consumers){
    return optEfficiency.get(consumers, ZERO)[0];
  }

  public float getPowerUsage(){
    return powerCons * current.get(ResourceType.power).multiple(entity) * powerOtherEff;
  }

  public void setCurrent(){
    current = entity.consumeCurrent() == -1 ? null : get().get(entity.consumeCurrent());
  }

  public void update(){
    setCurrent();
    powerCons = 0;
    if((!hasOptional() && !hasConsume())) return;

    valid = true;

    //只在选中消耗列表时才进行消耗更新
    if(current != null){
      boolean preValid = valid();

      for(Boolf<T> b : current.valid){
        valid &= b.get(entity);
      }
      consEfficiency = powerOtherEff = valid ? 1 : 0;
      current.forAll(c -> {
        float eff = c.efficiency(entity);
        if(c instanceof ConsumePower){
          powerCons += ((ConsumePower<T>)c).requestedPower(entity);
        }else powerOtherEff *= eff;

        consEfficiency *= eff;
        valid &= eff > 0.0001f;

        if(!valid){
          consEfficiency = 0;
          return;
        }

        if(preValid && entity.shouldConsume()){
          c.update(entity);
        }
      });
    }

    updateOptional();
  }

  public void updateOptional(){
    if(getOptional() != null){
      BaseConsumers<T> cons;
      boolean onlyOne = entity.getConsumerBlock().oneOfOptionCons();
      for(int id = 0; id < getOptional().size; id++){
        cons = getOptional().get(id);

        float[] optionalEff = {1};
        for(Boolf<T> b : cons.valid){
          optionalEff[0] *= b.get(entity) ? 1 : 0;
        }
        cons.forAll(c -> optionalEff[0] *= c.efficiency(entity));
        optEfficiency.get(cons, () -> new float[1])[0] = optionalEff[0];

        if(optionalEff[0] > 0.0001f){
          optionalCurr = cons;

          if(!entity.shouldConsumeOptions() || (!cons.optionalAlwaysValid && !valid)) continue;
          cons.forAll(c -> {
            c.update(entity);
            if(c instanceof ConsumePower<T> cp) powerCons += cp.requestedPower(entity);
          });

          if(cons.craftTime > 0){
            float[] arr = optProgress.get(cons, () -> new float[]{0});
            arr[0] += 1 / cons.craftTime * cons.delta(entity);
            while(arr[0] >= 1){
              arr[0] %= 1;
              triggerOpt(id);
            }
          }

          cons.optionalDef.get(entity, cons);
          if(onlyOne) break;
        }
      }
    }
  }

  public float consDelta(){
    return current == null ? 0 : current.delta(entity);
  }

  /**
   * 获取指定索引的消耗列表
   */
  public BaseConsumers<T> get(int index){
    return get().get(index);
  }

  /**
   * 获取指定索引处的可选消耗列表
   */
  public BaseConsumers<T> getOptional(int index){
    return index < getOptional().size ? getOptional().get(index) : null;
  }

  /**
   * 触发当前主要消耗项的trigger方法
   */
  public void trigger(){
    if(current != null){
      current.forAll(c -> c.consume(entity));
      for(Cons<T> trigger : current.triggers){
        trigger.get(entity);
      }
    }
  }

  /**
   * 触发一个可选消耗项的trigger方法
   */
  public void triggerOpt(int id){
    if(getOptional() != null && getOptional().size > id){
      BaseConsumers<T> cons = getOptional().get(id);
      cons.forAll(c -> c.consume(entity));
      for(Cons<T> trigger : cons.triggers){
        trigger.get(entity);
      }
    }
  }

  /**
   * 当前消耗列表除指定消耗项以外是否其他全部可用
   */
  public boolean excludeValid(ResourceType type){
    boolean[] temp = {true};
    current.forAll(c -> {
      if(c.type() == type) return;
      temp[0] &= c.efficiency(entity) > 0.0001f;
    });
    return temp[0];
  }

  /**
   * 当前消耗列表是否可用
   */
  public boolean valid(){
    return valid && entity.enabled;
  }

  /**
   * 当前消耗列表指定消耗项是否可用
   */
  public boolean valid(ResourceType type){
    return current.get(type) != null && current.get(type).efficiency(entity) > 0.0001;
  }

  /**
   * 指定的消耗列表是否可用
   */
  public boolean valid(int index){
    if(index >= get().size) return false;

    boolean[] valid = {true};

    get().get(index).forAll(c -> {
      if(c.efficiency(entity) < 0.0001f) valid[0] = false;
    });

    return valid[0];
  }

  @Override
  public void write(Writes write){
    write.bool(valid);
  }

  @Override
  public void read(Reads read){
    valid = read.bool();
  }
}
