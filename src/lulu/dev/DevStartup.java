package lulu.dev;

import arc.*;
import lulu.*;
import lulu.content.tantros.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.net.*;
import mindustry.ui.dialogs.*;

public final class DevStartup{
  private DevStartup(){
  }

  public static void loadFirstSave(){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      Vars.ui.load.runLoadSave(Vars.control.saves.getSaveSlots().first());
    });
  }

  public static void showTantrosDialog(){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      LuluMod.tantrosDialog.show();
    });
  }

  public static void setDebugSectorSelection(boolean enabled){
    PlanetDialog.debugSelect = enabled;
  }

  public static void playNewTantrosSector(int id){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      Vars.control.playNewSector(null, LaplacesTantros.tantros.sectors.find(s -> s.id == id), new WorldReloader());
    });
  }
}
