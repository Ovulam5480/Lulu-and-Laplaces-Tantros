package LLL.world.recipe;

import LLL.packet.resources.*;
import LLL.util.uncUtil.*;
import LLL.world.recipe.consumers.*;
import arc.func.*;
import arc.scene.event.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.meta.*;

/**
 * 消耗列表，记录一个消耗的包含生产时间，可选等在内的所有信息
 *
 * @author EBwilson
 */

public class BaseConsumers{
  /**
   * 是否为可选消耗项
   */
  public final boolean optional;
  protected final ObjectMap<ResourceType, BaseConsume> cons = new ObjectMap<>();
  protected final ObjectMap<ResourceType, ObjectSet<Content>> filter = new ObjectMap<>();
  protected final ObjectMap<ResourceType, ObjectSet<Content>> otherFilter = new ObjectMap<>();
  protected final ObjectMap<ResourceType, ObjectSet<Content>> selfAccess = new ObjectMap<>();
  /**
   * 该值控制生产消耗的时间
   */
  public float craftTime = 60;
  /**
   * 是否在统计栏显示消耗所需时间
   */
  public boolean showTime = false;
  public boolean optionalAlwaysValid = true;
  /**
   * 可选列表可用时将随更新执行的目标函数
   */
  public Cons2<ConsumerBuildc, BaseConsumers> optionalDef = (entity, cons) -> {
  };
  /**
   * 在统计信息显示自定义内容的函数
   */
  public Cons2<Stats, BaseConsumers> display = (stats, cons) -> {
  };
  /**
   * 是否用自定义的display覆盖默认的条目显示
   */
  public boolean customDisplayOnly;
  public Prov<Visibility> selectable = () -> Visibility.usable;
  public Floatf<ConsumerBuildc> consDelta = e -> e.delta() * e.consEfficiency();
  /**
   * 本消耗的可用控制器
   */
  public Seq<Boolf<ConsumerBuildc>> valid = new Seq<>();
  /**
   * 消耗触发器，在消耗的trigger()方法执行时触发
   */
  public Seq<Cons<ConsumerBuildc>> triggers = new Seq<>();

  public BaseConsumers(boolean optional){
    this.optional = optional;
  }

  public void initFilter(){
    filter.clear();
    for(ObjectMap.Entry<ResourceType, BaseConsume> entry : cons){
      Seq<Content> cont = entry.value.filter();
      if(cont != null) filter.get(entry.key, ObjectSet::new).addAll(cont);
    }
    for(ObjectMap.Entry<ResourceType, ObjectSet<Content>> entry : otherFilter){
      filter.get(entry.key, ObjectSet::new).addAll(entry.value);
    }
  }

  public void addToFilter(ResourceType type, Content content){
    otherFilter.get(type, ObjectSet::new).add(content);
  }

  public void addSelfAccess(ResourceType type, Content content){
    selfAccess.get(type, ObjectSet::new).add(content);
  }

  public BaseConsumers time(float time){
    this.craftTime = time;
    showTime = time > 0;
    return this;
  }

  public BaseConsumers overdriveValid(boolean valid){
    this.consDelta = valid ?
      e -> e.delta() * e.consEfficiency() :
      e -> Time.delta * e.consEfficiency();
    return this;
  }

  public BaseConsumers setConsDelta(Floatf<ConsumerBuildc> delta){
    this.consDelta = delta;
    return this;
  }

  public BaseConsumers consValidCondition(Boolf<ConsumerBuildc> cond){
    this.valid.add(cond);
    return this;
  }

  public BaseConsumers setConsTrigger(Cons<ConsumerBuildc> cond){
    this.triggers.add(cond);
    return this;
  }

  public float delta(ConsumerBuildc entity){
    return optional ? entity.delta() * entity.optionalConsEff(this) * Math.max(1, entity.consEfficiency()) : consDelta.get(entity);
  }

  public ConsumeItems item(Item item, int amount){
    return items(new ItemStack(item, amount));
  }

  public ConsumeItems items(ItemStack... items){
    return add(new ConsumeItems(items));
  }

  public ConsumeLiquids liquid(Liquid liquid, float amount){
    return liquids(new LiquidStack(liquid, amount));
  }

  public ConsumeLiquids liquids(LiquidStack... liquids){
    return add(new ConsumeLiquids(liquids));
  }

  public ConsumePower power(float usage){
    return add(new ConsumePower(usage, 0));
  }

  public ConsumePower power(float usage, float capacity){
    return add(new ConsumePower(usage, capacity));
  }

  public ConsumePower powerCond(float usage, float capacity, Boolf<ConsumerBuildc> cons){
    return add(new ConsumePower(usage, capacity){
      @Override
      public float requestedPower(ConsumerBuildc entity){
        return cons.get(entity) ? super.requestedPower(entity) : 0f;
      }
    });
  }

  public ConsumePower powerDynamic(Floatf<ConsumerBuildc> cons, float capacity, Cons<Stats> statBuilder){
    return add(new ConsumePower(0, capacity){
      @Override
      public float requestedPower(ConsumerBuildc entity){
        return cons.get(entity);
      }

      @Override
      public void display(Stats stats){
        statBuilder.get(stats);
      }
    });
  }

  public ConsumePower powerBuffer(float usage, float capacity){
    return add(new ConsumePower(usage, capacity));
  }

  public <N extends BaseConsume> N add(N consume){
    BaseConsume c = cons.get(consume.type());
    if(c == null){
      cons.put(consume.type(), consume);
      consume.parent = this;
      return consume;
    }else c.merge(consume);
    return (N)c;
  }

  public <N extends BaseConsume> N get(ResourceType type){
    return (N)cons.get(type);
  }

  public Seq<BaseConsume> all(Seq<BaseConsume> out){
    for(ResourceType type : ResourceType.all){
      BaseConsume c = cons.get(type);
      if(c != null) out.add(c);
    }

    return out;
  }

  public void forAll(Cons<BaseConsume> ccons){
    for(ResourceType type : ResourceType.all){
      BaseConsume c = cons.get(type);
      if(c != null) ccons.get(c);
    }
  }

  public void remove(ResourceType type){
    cons.remove(type);
  }

  public void display(Stats stats){
    if(cons.size > 0 && !customDisplayOnly){
      if(showTime) stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
      forAll(c -> c.display(stats));
    }

    display.get(stats, this);
  }

  public boolean filter(ResourceType type, Content content){
    return filter.get(type, Empties.nilSetO()).contains(content);
  }

  public boolean selfAccess(ResourceType type, Content content){
    return selfAccess.get(type, Empties.nilSetO()).contains(content);
  }

  public enum Visibility{
    usable(Touchable.enabled),
    unusable(Touchable.disabled),
    hidden(Touchable.disabled);

    public final Touchable buttonValid;

    Visibility(Touchable touchable){
      buttonValid = touchable;
    }
  }
}
