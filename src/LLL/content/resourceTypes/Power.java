package LLL.content.resourceTypes;

import LLL.content.extensions.*;
import mindustry.ctype.*;

public class Power extends UnlockableContent{
  public Power(String name){
    super(name);
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