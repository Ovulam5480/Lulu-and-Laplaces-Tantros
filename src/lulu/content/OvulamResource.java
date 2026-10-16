package lulu.content;

import lulu.content.resourceTypes.*;

public class OvulamResource{
  public static Power power, surge, pulse;

  public static void load(){
    power = new Power("power");
    surge = new Power("surge");
    pulse = new Power("pulse");
  }
}
