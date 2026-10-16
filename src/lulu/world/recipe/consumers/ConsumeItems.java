package lulu.world.recipe.consumers;

import lulu.entities.gen.*;
import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import java.util.*;

public class ConsumeItems<T extends Building & ConsumerBuildc<T>> extends ConsumeItemBase<T>{
  private static final ObjectMap<Item, ItemStack> TMP = new ObjectMap<>();

  public boolean showPerSecond = true;

  public ConsumeItems(ItemStack[] items){
    this.consItems = items;
  }

  @Override
  public void buildIcons(Table table){
    buildItemIcons(table, consItems, false, displayLim);
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumeItems<T> cons){
      TMP.clear();
      for(ItemStack stack : consItems){
        TMP.put(stack.item, stack);
      }

      for(ItemStack stack : cons.consItems){
        TMP.get(stack.item, () -> new ItemStack(stack.item, 0)).amount += stack.amount;
      }

      consItems = TMP.values().toSeq().sort(Comparator.comparingInt(a -> a.item.id)).toArray(ItemStack.class);
      return;
    }
    throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public void consume(T object){
    float f = multiple(object);
    for(ItemStack stack : consItems){
      int amount = stack.amount * ((int)Math.floor(f)) + Mathf.num(Math.random() < f % 1);
      object.items.remove(stack.item, amount);
    }
  }

  @Override
  public void update(T entity){
  }

  @Override
  public void display(Stats stats){
    stats.add(Stat.input, table -> {
      table.row();
      table.table(t -> {
        t.defaults().left().grow().fill().padLeft(6);
        t.add(Core.bundle.get("misc.item") + ":");
        for(ItemStack stack : consItems){
          t.add(showPerSecond ?
            StatValues.displayItem(stack.item, stack.amount, parent.craftTime, true) :
            StatValues.displayItem(stack.item, stack.amount, true));
        }
      }).left().padLeft(5);
    });
  }

  @Override
  public void build(T entity, Table table){
    for(ItemStack stack : consItems){
      int amount = (int)(stack.amount * multiple(entity));
      if(amount == 0 && !entity.consumer().valid()) amount = stack.amount;

      int n = amount;
      table.add(new ReqImage(StatValues.stack(stack),
        () -> entity.items != null && entity.items.has(stack.item, n))).padRight(8);
    }
    table.row();
  }

  @Override
  public float efficiency(T entity){
    if(entity.items == null) return 0;
    for(ItemStack stack : consItems){
      if(entity.items == null || entity.items.get(stack.item) < stack.amount * multiple(entity)) return 0;
    }
    return 1;
  }

  @Override
  public Seq<Content> filter(){
    return Seq.with(consItems).map(s -> s.item);
  }
}
