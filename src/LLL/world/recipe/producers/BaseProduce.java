package LLL.world.recipe.producers;

import LLL.packet.resources.*;
import LLL.world.recipe.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import mindustry.world.meta.*;

public abstract class BaseProduce{
  public Floatf<ProducerBuildc> prodMultiplier;

  public BaseProducers parent;
  public boolean blockWhenFull = true;

  /**
   * 产出资源类型
   */
  public abstract ResourceType type();

  public Color color(){
    return null;
  }

  public boolean hasIcons(){
    return true;
  }

  public abstract void buildIcons(Table table);

  public abstract void merge(BaseProduce other);

  public abstract void produce(ProducerBuildc entity);

  public abstract void update(ProducerBuildc entity);

  public abstract void display(Stats stats);

  public void buildBars(ProducerBuildc entity, Table bars){
  }

  public abstract boolean valid(ProducerBuildc entity);

  public void dump(ProducerBuildc entity){
  }

  public float multiple(ProducerBuildc entity){
    return (prodMultiplier == null ? 1 : prodMultiplier.get(entity)) * entity.prodMultiplier();
  }

  @SuppressWarnings("unchecked")
  public <N extends ProducerBuildc> BaseProduce setMultiple(Floatf<N> multiple){
    prodMultiplier = (Floatf<ProducerBuildc>)multiple;
    return this;
  }
}
