package LLL.content;

import LLL.content.resourceTypes.*;
import LLL.type.resourceStacks.*;

public class OvulamResource{
  public static Power power, surge, pulse;

  public static void load(){
    power = new Power("power");
    surge = new Power("surge");
    pulse = new Power("pulse");

    PowerResourceStack.powers.each(Power::load);
  }
}
