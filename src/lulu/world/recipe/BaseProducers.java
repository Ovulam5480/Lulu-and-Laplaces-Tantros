package lulu.world.recipe;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.recipe.producers.*;
import arc.func.*;
import arc.graphics.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import java.util.*;

/**
 * 产出列表，绑定一个消耗列表，在执行消耗的同时对应执行此生产列表，以实现工厂生产
 *
 * @author EBwilson
 */
public class BaseProducers<T extends Building & ProducerBuildc<T>>{
  final static Color TRANS = new Color(0, 0, 0, 0);

  protected final ObjectMap<ResourceType, BaseProduce<T>> prod = new ObjectMap<>();

  /**
   * 此生产项的颜色，这通常被用于确定绘制top等目的时快速选择颜色
   */
  public Color color = TRANS;
  /**
   * 后初始化的变量，不要手动更改，该变量绑定到与此生产匹配的消耗项
   */
  public BaseConsumers<T> cons;

  public BaseProducers<T> setColor(Color color){
    this.color = color;
    return this;
  }

  public ProduceItems<T> item(Item item, int amount){
    return items(new ItemStack(item, amount));
  }

  public ProduceItems<T> items(ItemStack... items){
    return add(new ProduceItems<>(items));
  }

  public ProduceLiquids<T> liquid(Liquid liquid, float amount){
    return liquids(new LiquidStack(liquid, amount));
  }

  public ProduceLiquids<T> liquids(LiquidStack... liquids){
    return add(new ProduceLiquids<>(liquids));
  }

  public ProducePower<T> power(float prod){
    return add(new ProducePower<>(prod));
  }

  public <N extends BaseProduce<T>> N add(N produce){
    BaseProduce<T> p = prod.get(produce.type());
    if(p == null){
      prod.put(produce.type(), produce);
      produce.parent = this;
      if(color == TRANS && produce.color() != null){
        color = produce.color();
      }
      return produce;
    }else p.merge(produce);
    return (N)p;
  }

  public <N extends BaseProduce<T>> N get(ResourceType type){
    return (N)prod.get(type);
  }

  public Seq<BaseProduce<T>> all(Seq<BaseProduce<T>> out){
    for(ResourceType type : ResourceType.all){
      BaseProduce<T> p = prod.get(type);
      if(p != null) out.add(p);
    }

    return out;
  }

  public void forAll(Cons<BaseProduce<T>> pcons){
    for(ResourceType type : ResourceType.all){
      BaseProduce<T> p = prod.get(type);
      if(p != null) pcons.get(p);
    }
  }

  public void remove(ResourceType type){
    prod.remove(type);
  }

  public void display(Stats stats){
    if(prod.size > 0){
      for(BaseProduce<T> p : prod.values().toSeq().sort(Comparator.comparingInt(a -> a.type().id))){
        p.display(stats);
      }
    }
  }
}
