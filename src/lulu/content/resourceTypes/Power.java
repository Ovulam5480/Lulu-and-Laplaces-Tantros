package lulu.content.resourceTypes;

import arc.graphics.*;
import lulu.content.extensions.*;
import lulu.world.*;
import mindustry.ctype.*;

public class Power extends UnlockableContent{
  public Color color;
  public Power(String name){
    this(name, Color.white);
  }

  public Power(String name, Color color){
    super(name);
    this.color = color;
  }

  @Override
  public ContentType getContentType(){
    return OvulamContentType.power.value;
  }

  public float applyPowerGraph(OvulamPowerGraph graph, float amount){
    return amount;
  }

  public float applyPowerGraphMixed(OvulamPowerGraph graph, float amount){
    return amount;
  }
}