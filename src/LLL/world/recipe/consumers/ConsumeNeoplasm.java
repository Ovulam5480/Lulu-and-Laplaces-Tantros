package LLL.world.recipe.consumers;

import LLL.packet.resources.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.world.meta.*;

public class ConsumeNeoplasm extends BaseConsume{
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
  public void merge(BaseConsume other){
    if(other instanceof ConsumeNeoplasm con){
      amount += con.amount;
    }
  }

  @Override
  public void consume(ConsumerBuildc entity){
  }

  @Override
  public void update(ConsumerBuildc entity){
    entity.neoplasm().removeClamp(amount);
  }

  @Override
  public void display(Stats stats){

  }

  @Override
  public void build(ConsumerBuildc entity, Table table){

  }

  @Override
  public float efficiency(ConsumerBuildc entity){
    return entity.neoplasm().has(amount) ? 1 : 0;
  }

  @Override
  public Seq<Content> filter(){
    return null;
  }
}
