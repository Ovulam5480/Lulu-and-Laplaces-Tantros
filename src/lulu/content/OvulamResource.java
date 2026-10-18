package lulu.content;

import lulu.content.resourceTypes.*;
import mindustry.graphics.*;

public class OvulamResource{
  public static Power power, surge, pulse, drain;

  public static void load(){
    power = new Power("power", Pal.lancerLaser);
    surge = new Power("surge", Pal.accent);
    pulse = new Power("pulse", Pal.heal);
    drain = new Power("drain", Pal.sap);
  }
}
