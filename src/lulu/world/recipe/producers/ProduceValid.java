package lulu.world.recipe.producers;

import arc.scene.ui.layout.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public class ProduceValid<T extends Building & ProducerBuildc<T>> extends BaseProduce<T>{
  @Override
  public ResourceType type(){
    return ResourceType.nothing;
  }

  @Override
  public void buildIcons(Table table){}

  @Override
  public void merge(BaseProduce<T> other){}

  @Override
  public void produce(T entity){}

  @Override
  public void update(T entity){}

  @Override
  public void display(Stats stats){}

  @Override
  public boolean valid(T entity){
    return true;
  }
}
