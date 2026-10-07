package LLL.world.recipe.consumers;

import LLL.packet.resources.*;
import LLL.world.recipe.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.world.meta.*;

public abstract class BaseConsume{
  public BaseConsumers parent;
  public Floatf<ConsumerBuildc> consMultiplier;

  /**
   * 消耗的类型
   */
  public abstract ResourceType type();

  public boolean hasIcons(){
    return true;
  }

  public abstract void buildIcons(Table table);

  public abstract void merge(BaseConsume other);

  public abstract void consume(ConsumerBuildc entity);

  public abstract void update(ConsumerBuildc entity);

  public abstract void display(Stats stats);

  public abstract void build(ConsumerBuildc entity, Table table);

  public void buildBars(ConsumerBuildc entity, Table bars){
  }

  public abstract float efficiency(ConsumerBuildc entity);

  public abstract Seq<Content> filter();

  public float multiple(ConsumerBuildc entity){
    return (consMultiplier == null ? 1 : consMultiplier.get(entity)) * entity.consMultiplier();
  }

  public BaseConsume setMultiple(Floatf<ConsumerBuildc> multiple){
    consMultiplier = multiple;
    return this;
  }
}
