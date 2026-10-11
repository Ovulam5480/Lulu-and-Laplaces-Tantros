package LLL.world.recipe.consumers;

import LLL.entities.gen.*;
import LLL.packet.resources.*;
import LLL.world.recipe.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public abstract class BaseConsume<T extends Building & ConsumerBuildc>{
  public BaseConsumers<T> parent;
  public Floatf<T> consMultiplier;

  /**
   * 消耗的类型
   */
  public abstract ResourceType type();

  public boolean hasIcons(){
    return true;
  }

  public abstract void buildIcons(Table table);

  public abstract void merge(BaseConsume<T> other);

  public abstract void consume(T entity);

  public abstract void update(T entity);

  public abstract void display(Stats stats);

  public abstract void build(T entity, Table table);

  public void buildBars(T entity, Table bars){
  }

  public abstract float efficiency(T entity);

  public abstract Seq<Content> filter();

  public float multiple(T entity){
    return (consMultiplier == null ? 1 : consMultiplier.get(entity)) * entity.consMultiplier();
  }

  public BaseConsume<T> setMultiple(Floatf<T> multiple){
    consMultiplier = multiple;
    return this;
  }
}
