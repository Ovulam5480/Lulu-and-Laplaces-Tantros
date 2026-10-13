package LLL.world.recipe.consumers;

import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.world.buildComp.module.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

public class ConsumeNeoplasm<T extends Building & ConsumerBuildc<T> & NeoplasmStorageComp> extends BaseConsume<T>{
  public float amount;

  public ConsumeNeoplasm(float amount){
    this.amount = amount;
  }

  @Override
  public ResourceType type(){
    return ResourceType.neoplasm;
  }

  @Override
  public void buildIcons(Table table){

  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumeNeoplasm<T> con){
      amount += con.amount;
    }
  }

  @Override
  public void consume(T entity){
  }

  @Override
  public void update(T entity){
    entity.neoplasm().removeClamp(amount);
  }

  @Override
  public void display(Stats stats){

  }

  @Override
  public void build(T entity, Table table){

  }

  @Override
  public float efficiency(T entity){
    return entity.neoplasm().has(amount) ? 1 : 0;
  }

  @Override
  public Seq<Content> filter(){
    return null;
  }
}
