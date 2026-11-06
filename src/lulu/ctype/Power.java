package lulu.ctype;

import arc.graphics.*;
import lulu.content.OvulamContents;
import lulu.content.extensions.*;
import lulu.world.*;
import mindustry.Vars;
import mindustry.ctype.*;

public class Power extends UnlockableContent{
  public Color color;
  public float mixPenalty = 0.1f;
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

  public float applyPowerGraph(OvulamPowerGraph graph, float produce, float consume){
    return produce - consume;
  }

  public float applyPowerGraphMixed(OvulamPowerGraph graph, float produce, float consume){
    return produce - consume;
  }

  public static Power get(int id){
    return OvulamContents.power(id);
  }
}