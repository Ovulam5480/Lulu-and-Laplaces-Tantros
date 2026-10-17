package lulu.dev;

import lulu.*;
import lulu.content.tantros.*;
import mindustry.*;
import mindustry.net.*;
import mindustry.ui.dialogs.*;

public final class DevStartup{
  private DevStartup(){
  }

  public static void loadFirstSave(){
    Vars.ui.load.runLoadSave(Vars.control.saves.getSaveSlots().first());
  }

  public static void showTantrosDialog(){
    LuluMod.tantrosDialog.show();
  }

  public static void setDebugSectorSelection(boolean enabled){
    PlanetDialog.debugSelect = enabled;
  }

  public static void playNewTantrosSector(int id){
    Vars.control.playNewSector(null, LaplacesTantros.tantros.sectors.find(s -> s.id == id), new WorldReloader());
  }
}
