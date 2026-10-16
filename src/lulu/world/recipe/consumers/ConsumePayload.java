package lulu.world.recipe.consumers;

import lulu.ctype.*;
import lulu.entities.gen.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

import java.util.*;

public class ConsumePayload<T extends Building & ConsumerBuildc<T>> extends BaseConsume<T>{
  private static final ObjectMap<UnlockableContent, PayloadStack> TMP = new ObjectMap<>();

  public int displayLim = 4;
  public PayloadStack[] payloads;

  public ConsumePayload(PayloadStack[] payloads){
    this.payloads = payloads;
  }

  public static void buildPayloadIcons(Table table, PayloadStack[] payloads, int displayLim){
    int count = 0;
    for(PayloadStack stack : payloads){
      count++;
      if(displayLim >= 0 && count > displayLim){
        table.add("...");
        break;
      }

      table.stack(
        new Table(o -> {
          o.left();
          o.add(new Image(stack.item.fullIcon)).size(32f).scaling(Scaling.fit);
        }),
        new Table(t -> {
          t.left().bottom();
          t.add(stack.amount + "").style(Styles.outlineLabel);
          t.pack();
        })
      );
    }
  }

  @Override
  public ResourceType type(){
    return ResourceType.payload;
  }

  @Override
  public void buildIcons(Table table){
    buildPayloadIcons(table, payloads, displayLim);
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumePayload<T> cons){
      TMP.clear();
      for(PayloadStack stack : payloads){
        TMP.put(stack.item, stack);
      }

      for(PayloadStack stack : cons.payloads){
        TMP.get(stack.item, () -> new PayloadStack(stack.item, 0)).amount += stack.amount;
      }

      payloads = TMP.values().toSeq().sort(Comparator.comparingInt(a -> a.item.id)).toArray(PayloadStack.class);
    }else throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public float efficiency(T build){
    for(PayloadStack stack : payloads){
      if(!build.getPayloads().contains(stack.item, stack.amount)){
        return 0f;
      }
    }
    return 1f;
  }

  @Override
  public void consume(T build){
    for(PayloadStack stack : payloads){
      build.getPayloads().remove(stack.item, stack.amount);
    }
  }

  @Override
  public void update(T entity){
  }

  @Override
  public void display(Stats stats){
    for(PayloadStack stack : payloads){
      stats.add(Stat.input, t -> {
        t.add(StatValues.stack(stack));
        t.add(stack.item.localizedName).padLeft(4).padRight(4);
      });
    }
  }

  @Override
  public void build(T build, Table table){
    PayloadSeq inv = build.getPayloads();

    table.table(c -> {
      int i = 0;
      for(var stack : payloads){
        c.add(new ReqImage(StatValues.stack(stack),
          () -> inv.contains(stack.item, stack.amount))).padRight(8);
        if(++i % 4 == 0) c.row();
      }
    }).left();
  }

  @Override
  public Seq<Content> filter(){
    return Seq.with(payloads).map(s -> s.item);
  }
}