package lulu.world.recipe.consumers;

import lulu.ctype.*;
import lulu.entities.gen.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public abstract class ConsumeItemBase<T extends Building & ConsumerBuildc<T>> extends BaseConsume<T>{
  public ItemStack[] consItems;
  public int displayLim = 4;

  public static void buildItemIcons(Table table, ItemStack[] items, boolean or, int limit){
    int count = 0;
    for(ItemStack stack : items){
      count++;
      if(count > 0 && or) table.add("/").set(Cell.defaults()).fill();
      if(limit >= 0 && count > limit){
        table.add("...");
        break;
      }

      table.add(StatValues.stack(stack));
    }
  }

  @Override
  public ResourceType type(){
    return ResourceType.item;
  }
}
