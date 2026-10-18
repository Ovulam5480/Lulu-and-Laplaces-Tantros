package lulu.content.resourceTypes;

import arc.graphics.*;
import lulu.content.extensions.*;
import mindustry.ctype.*;

public class Power extends UnlockableContent{
  public Color color;
  public Power(String name){
    this(name, Color.white);
  }

  public Power(String name, Color color){
    super(name);
    this.color = color;
    alwaysUnlocked = true;
  }

  @Override
  public ContentType getContentType(){
    return OvulamContentType.power.value;
  }

  @Override
  public void load(){
    //fullIcon = uiIcon = Icon.power.getRegion();
  }
}