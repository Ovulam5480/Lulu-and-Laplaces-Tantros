package LLL.world.recipe.consumers;

import LLL.packet.resources.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.type.*;
import mindustry.ui.*;

public abstract class ConsumeLiquidBase extends BaseConsume{
  public LiquidStack[] consLiquids;
  public int displayLim = 4;

  public static void buildLiquidIcons(Table table, LiquidStack[] liquids, boolean or, int limit){
    int count = 0;
    for(LiquidStack stack : liquids){
      count++;
      if(count > 0 && or) table.add("/").set(Cell.defaults()).fillX();
      if(limit >= 0 && count > limit){
        table.add("...");
        break;
      }

      table.stack(
        new Table(o -> {
          o.left();
          o.add(new Image(stack.liquid.fullIcon)).size(32f).scaling(Scaling.fit);
        }),
        new Table(t -> {
          t.left().bottom();
          t.add(stack.amount * 60 >= 1000 ? UI.formatAmount((long)(stack.amount * 60)) + "/s" : Mathf.round(stack.amount * 600) / 10f + "/s").style(Styles.outlineLabel);
          t.pack();
        })
      );
    }
  }

  @Override
  public ResourceType type(){
    return ResourceType.liquid;
  }

  @Override
  public void buildBars(ConsumerBuildc entity, Table bars){
    for(LiquidStack stack : consLiquids){
      bars.add(new Bar(
        () -> stack.liquid.localizedName,
        () -> stack.liquid.barColor != null ? stack.liquid.barColor : stack.liquid.color,
        () -> Math.min(entity.liquids.get(stack.liquid) / entity.block.liquidCapacity, 1f)
      ));
      bars.row();
    }
  }
}
