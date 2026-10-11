package LLL.world.recipe.producers;

import LLL.entities.gen.*;
import LLL.packet.resources.*;
import LLL.world.recipe.*;
import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public abstract class BaseProduce<T extends Building & ProducerBuildc>{
  public Floatf<T> prodMultiplier;

  public BaseProducers<T> parent;
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

  public abstract void merge(BaseProduce<T> other);

  public abstract void produce(T entity);

  public abstract void update(T entity);

  public abstract void display(Stats stats);

  public void buildBars(T entity, Table bars){
  }

  public abstract boolean valid(T entity);

  public void dump(T entity){
  }

  public float multiple(T entity){
    return (prodMultiplier == null ? 1 : prodMultiplier.get(entity)) * entity.prodMultiplier();
  }

  @SuppressWarnings("unchecked")
  public <N extends T> BaseProduce<T> setMultiple(Floatf<N> multiple){
    prodMultiplier = (Floatf<T>)multiple;
    return this;
  }
}
