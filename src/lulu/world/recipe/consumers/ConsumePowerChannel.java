package lulu.world.recipe.consumers;

import lulu.content.*;
import lulu.ctype.Power;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.components.buildComps.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

public class ConsumePowerChannel<T extends Building & ConsumerBuildc<T> & PowerChannelBuildComp> extends BaseConsume<T>{
  public ObjectFloatMap<Power> usages;
  public int displayLim = 4;

  public ConsumePowerChannel(ObjectFloatMap<Power> usages){
    this.usages = usages;
  }

  public ConsumePowerChannel(Power power, float usage){
    usages = new ObjectFloatMap<>();
    usages.put(power, usage);
  }

  public ConsumePowerChannel(Object... values){
    usages = new ObjectFloatMap<>();
    usages.putAll(values);
  }

  public static int totalPower(){
    return OvulamContents.powers().size;
  }

  @Override
  public ResourceType type(){
    return ResourceType.power;
  }

  @Override
  public void buildIcons(Table table){
    buildPowerChannelIcons(table, usages, false, displayLim);
  }

  public static void buildPowerChannelIcons(Table table, ObjectFloatMap<Power> powers, boolean or, int limit){
    int count = 0;
    for(ObjectFloatMap.Entry<Power> stack : powers){
      Power power = stack.key;
      float usage = stack.value;

      count++;
      if(count > 0 && or) table.add("/").set(Cell.defaults()).fillX();
      if(limit >= 0 && count > limit){
        table.add("...");
        break;
      }

      table.stack(
        new Table(o -> {
          o.left();
          o.add(new Image(power.fullIcon)).size(32f).scaling(Scaling.fit);
        }),
        new Table(t -> {
          t.left().bottom();
          t.add(usage * 60 >= 1000 ? UI.formatAmount((long)(usage * 60)) + "/s" : Mathf.round(usage * 600) / 10f + "/s").style(Styles.outlineLabel);
          t.pack();
        })
      );
    }
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumePowerChannel<T> cons){
      for(ObjectFloatMap.Entry<Power> stack : cons.usages){
        usages.increment(stack.key, 0, stack.value);
      }
      return;
    }
    throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public void buildBars(T entity, Table bars){
    if(entity.getPowerChannelComp().bufferedPowerChannel()){
      for(ObjectFloatMap.Entry<Power> stack : usages){
        Power power = stack.key;
        bars.add(new Bar(
          () -> power.localizedName,
          () -> power.color,
          () -> 1 - (entity.getPowerStored(power.id) / entity.getPowerCapacity(power.id))
        ));
        bars.row();
      }
    }else {
      bars.add(new Bar(
        () -> 11111 + "",
        () -> Pal.accent,
        () -> entity.power.status)
      );
    }
  }

  @Override
  public void build(T tile, Table table){
  }

  @Override
  public void update(T entity){
  }

  @Override
  public float efficiency(T entity){
    return entity.power().status;
  }

  @Override
  public void display(Stats stats){
  }

  @Override
  public void consume(T entity){
  }

  @Override
  public Seq<Content> filter(){
    return null;
  }
}
