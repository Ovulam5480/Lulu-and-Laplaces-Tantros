package LLL.world.recipe.consumers;

import LLL.entities.gen.*;
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

public class ConsumeLiquids<T extends Building & ConsumerBuildc<T>> extends ConsumeLiquidBaseR<T>{
  private static final ObjectMap<Liquid, LiquidStack> TMP = new ObjectMap<>();

  public boolean portion = false;

  public ConsumeLiquids(LiquidStack[] liquids){
    this.consLiquids = liquids;
  }

  @Override
  public void buildIcons(Table table){
    buildLiquidIcons(table, consLiquids, false, displayLim);
  }

  public void portion(){
    this.portion = true;
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumeLiquids<T> cons){
      TMP.clear();
      for(LiquidStack stack : consLiquids){
        TMP.put(stack.liquid, stack);
      }

      for(LiquidStack stack : cons.consLiquids){
        TMP.get(stack.liquid, () -> new LiquidStack(stack.liquid, 0)).amount += stack.amount;
      }

      consLiquids = TMP.values().toSeq().sort(Comparator.comparingInt(a -> a.liquid.id)).toArray(LiquidStack.class);
      return;
    }
    throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public void consume(T entity){
    if(portion) for(LiquidStack stack : consLiquids){
      entity.liquids.remove(stack.liquid, stack.amount * 60 * multiple(entity));
    }
  }

  @Override
  public void update(T entity){
    if(!portion) for(LiquidStack stack : consLiquids){
      entity.liquids.remove(stack.liquid, stack.amount * parent.delta(entity) * multiple(entity));
    }
  }

  @Override
  public void display(Stats stats){
    stats.add(Stat.input, table -> {
      table.row();
      table.table(t -> {
        t.defaults().left().fill().padLeft(6);
        t.add(Core.bundle.get("misc.liquid") + ":");
        for(LiquidStack stack : consLiquids){
          t.add(StatValues.displayLiquid(stack.liquid, stack.amount * 60, true));
        }
      }).left().padLeft(5);
    });
  }

  @Override
  public void build(T entity, Table table){
    for(LiquidStack stack : consLiquids){
      table.add(new ReqImage(stack.liquid.uiIcon,
        () -> entity.liquids != null && entity.liquids.get(stack.liquid) > 0)).padRight(8);
    }
    table.row();
  }

  @Override
  public float efficiency(T entity){
    if(entity.liquids == null) return 0;
    if(portion){
      for(LiquidStack stack : consLiquids){
        if(entity.liquids.get(stack.liquid) < stack.amount * multiple(entity) * 60) return 0;
      }
      return 1;
    }else{
      float min = 1;

      for(LiquidStack stack : consLiquids){
        min = Math.min(entity.liquids.get(stack.liquid) / (stack.amount * multiple(entity)), min);
      }

      if(min < 0.0001f) return 0;
      return Mathf.clamp(min);
    }
  }

  @Override
  public Seq<Content> filter(){
    return Seq.with(consLiquids).map(s -> s.liquid);
  }
}
