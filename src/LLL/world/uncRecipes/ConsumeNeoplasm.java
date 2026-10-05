package LLL.world.uncRecipes;

import LLL.content.extensions.*;
import LLL.world.blocks.module.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import universecore.world.consumers.*;

public class ConsumeNeoplasm<T extends Building & NeoplasmOrganModule> extends BaseConsume<T>{
  public float amount;

  public ConsumeNeoplasm(float amount){
    this.amount = amount;
  }

  @Override
  public ConsumeType<?> type(){
    return OvulamConsumeTypes.neoplasm;
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
