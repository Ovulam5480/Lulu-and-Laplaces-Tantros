package lulu.world.recipe.producers;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.recipe.consumers.*;
import arc.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

public class ProducePower<T extends Building & ProducerBuildc<T>> extends BaseProduce<T>{
  public float powerProduction;
  public boolean showIcon = true;

  public ProducePower(float prod){
    powerProduction = prod;
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
      ConsumePower.buildPowerImage(table, powerProduction);
    }
  }

  @Override
  public void merge(BaseProduce<T> other){
    if(other instanceof ProducePower<T> cons){
      powerProduction += cons.powerProduction;
      return;
    }
    throw new IllegalArgumentException("only merge production with same type");
  }

  @Override
  public void produce(T entity){
    /*不在此更新能量生产*/
  }

  @Override
  public void update(T entity){
    /*此处不进行能量更新*/
  }

  @Override
  public void buildBars(T entity, Table bars){
    ResourceType powerResource = ResourceType.power;
    Floatp prod = () -> entity.powerProdEfficiency() * entity.producer().current.<ProducePower<T>>get(powerResource).powerProduction;
    Floatp cons = () -> {
      ConsumePower<T> cp;
      return entity.block.consumesPower && entity.consumer().current != null
        && (cp = entity.consumer().current.<ConsumePower<T>>get(powerResource)) != null ?
        cp.usage * cp.multiple(entity) : 0;
    };
    bars.add(new Bar(
      () -> Core.bundle.format("bar.poweroutput", Strings.fixed(Math.max(prod.get() - cons.get(), 0) * 60 * entity.timeScale(), 1)),
      () -> Pal.powerBar,
      entity::powerProdEfficiency
    )).growX();
    bars.row();
  }

  @Override
  public void display(Stats stats){
    stats.add(Stat.basePowerGeneration, powerProduction * 60.0f, StatUnit.powerSecond);
  }

  @Override
  public boolean valid(T entity){
    return entity.power != null;
  }
}
