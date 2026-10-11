package LLL.world.modules;

import LLL.entities.gen.*;
import LLL.packet.resources.*;
import LLL.world.recipe.*;
import LLL.world.recipe.producers.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.modules.*;

/**
 * 生产者的产出模块，用于集中处理方块的生产工作
 *
 * @author EBwilson
 */

public class BaseProductModule<T extends Building & ProducerBuildc> extends BlockModule{
  public final T entity;
  public BaseConsumeModule<T> consumer;
  public BaseProducers<T> current;
  public boolean valid;

  public BaseProductModule(T entity){
    this.entity = entity;
    consumer = entity.consumer();
    current = entity.produceCurrent() != -1 ? entity.getProducerBlock().producers().get(entity.produceCurrent()) : null;
  }

  public Seq<BaseProducers<T>> get(){
    return entity.getProducerBlock().producers();
  }

  public void trigger(){
    if(current != null) current.forAll(c -> c.produce(entity));
  }

  public float getPowerProduct(){
    if(current == null) return 0;
    return current.<ProducePower<T>>get(ResourceType.power).powerProduction * entity.consumer().powerOtherEff * (Mathf.num(entity.shouldConsume() && entity.consumeValid()) * ((BaseProduce)current.get(ResourceType.power)).multiple(entity));
  }

  public void setCurrent(){
    current = entity.consumeCurrent() == -1 ? null : get().get(entity.consumeCurrent());
  }

  public void update(){
    setCurrent();

    valid = true;
    //只在选择了生产列表时才进行产出更新
    if(current != null){
      setCurrent();
      boolean doprod = entity.consumeValid() && entity.shouldConsume() && entity.shouldProduct();
      boolean preValid = valid();

      boolean[] anyValid = {false};
      current.forAll(c -> {
        boolean v = c.valid(entity);
        anyValid[0] |= v;
        valid &= !c.blockWhenFull || v;
        if(doprod && preValid && v){
          c.update(entity);
        }
      });
      if(!anyValid[0]) valid = false;
    }

    //无论何时都向外导出产品
    doDump(entity);
  }

  public void doDump(T entity){
    if(current != null) current.forAll(c -> c.dump(entity));
  }

  public boolean valid(){
    return valid && entity.enabled;
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
