package lulu.world.recipe.consumers;

import lulu.ctype.*;
import lulu.entities.gen.*;
import arc.*;
import arc.func.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/*仅仅保存消耗参数，能量消耗本身实际应用仍为默认consumes*/
public class ConsumePower<T extends Building & ConsumerBuildc<T>> extends BaseConsume<T>{
  public float usage;
  public float capacity;
  public boolean buffered;

  public boolean showIcon;

  Seq<ConsumePower<T>> others = new Seq<>(ConsumePower.class);

  public ConsumePower(float usage, float capacity){
    this.usage = usage;
    this.buffered = capacity > 0;
    this.capacity = capacity;
  }

  public static void buildPowerImage(Table table, float usage){
    table.stack(
      new Table(o -> {
        o.left();
        o.add(new Image(Icon.power)).size(32f).scaling(Scaling.fit);
      }),
      new Table(t -> {
        t.left().bottom();
        t.add(usage * 60 >= 1000 ? UI.formatAmount((long)(usage * 60)) + "/s" : (int)(usage * 60) + "/s").style(Styles.outlineLabel);
        t.pack();
      })
    );
  }

  @Override
  public ResourceType type(){
    return ResourceType.power;
  }

  @Override
  public boolean hasIcons(){
    return showIcon;
  }

  @Override
  public void buildIcons(Table table){
    if(showIcon){
      buildPowerImage(table, usage);
    }
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumePower<T> cons){
      buffered |= cons.buffered;
      capacity += cons.capacity;

      others.add(cons);
      return;
    }
    throw new IllegalArgumentException("only merge consume with same type");
  }

  public float requestedPower(T entity){
    float res = usage;
    for(ConsumePower other : others.items){
      if(other == null) continue;
      res += other.requestedPower(entity);
    }
    return res;
  }

  @Override
  public void buildBars(T entity, Table bars){
    Boolp buffered = () -> entity.block.consPower.buffered;
    Floatp capacity = () -> entity.block.consPower.capacity;
    bars.add(new Bar(
      () -> buffered.get() ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * capacity.get()) ?
        "<ERROR>" : (int)(entity.power.status * capacity.get())) : Core.bundle.get("bar.power"),
      () -> Pal.powerBar,
      () -> Mathf.zero(entity.block.consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status)).growX();
    bars.row();
  }

  @Override
  public void build(T tile, Table table){
  }

  @Override
  public void update(T entity){
  }

  @Override
  public float efficiency(T entity){
    if(entity.power == null) return 0;
    if(buffered){
      return entity.power.status > 0 ? 1 : 0;
    }else{
      return entity.power.status;
    }
  }

  @Override
  public void display(Stats stats){
    stats.add(Stat.powerUse, usage * 60f, StatUnit.powerSecond);
  }

  @Override
  public void consume(T entity){
  }

  @Override
  public Seq<Content> filter(){
    return null;
  }
}
