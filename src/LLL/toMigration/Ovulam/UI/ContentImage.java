package LLL.toMigration.Ovulam.UI;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;

import static mindustry.core.UI.*;


public class ContentImage extends Stack{
  public float cellSize = 48;

  public ContentImage(TextureRegion region, float amount, boolean completely){
    float decimal = amount - Mathf.floor(amount);

    add(new Table(o -> {
      o.left();
      o.add(new Image(region)).size(Vars.iconMed).scaling(Scaling.fit);
    }));

    if(amount == 0) return;

    add(new Table(t -> {
      t.left().bottom();
      t.add((formatAmount((long)amount, decimal)) + (completely ? "" : "/")).style(Styles.outlineLabel);
    }));
  }

  public String formatAmount(long number, float decimal){
    long mag = Math.abs(number);
    String sign = number < 0 ? "-" : "";

    if(mag >= 1_000_000_000){
      return sign + Strings.fixed(mag / 1_000_000_000f, 1) + "[gray]" + billions + "[]";
    }else if(mag >= 1_000_000){
      return sign + Strings.fixed(mag / 1_000_000f, 1) + "[gray]" + millions + "[]";
    }else if(mag >= 10_000){
      return number / 1000 + "[gray]" + thousands + "[]";
    }else if(mag >= 1000){
      return sign + Strings.fixed(mag / 1000f, 1) + "[gray]" + thousands + "[]";
    }else if(mag >= 10){
      return Mathf.ceil(number + decimal) + "";
    }else{
      return decimal == 0 ? number + "" : sign + Strings.fixed(mag + decimal, 1) + "[]";
    }
  }

  public ContentImage(ItemStack stack){
    this(stack.item.uiIcon, stack.amount, true);
  }

  public ContentImage(LiquidStack stack, boolean completely){
    this(stack.liquid.uiIcon, stack.amount, completely);
  }

  public ContentImage(PayloadStack stack){
    this(stack.item.uiIcon, stack.amount, true);
  }

  public ContentImage(float power){
    this(Icon.power.getRegion(), power, false);
  }

  //todo timeImage
}
