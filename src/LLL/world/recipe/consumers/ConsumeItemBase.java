package LLL.world.recipe.consumers;

import LLL.packet.resources.*;
import arc.scene.ui.layout.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public abstract class ConsumeItemBase extends BaseConsume{
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
