package lulu.dev;

import arc.*;
import lulu.*;
import lulu.content.tantros.*;
import lulu.ui.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.net.*;
import mindustry.ui.dialogs.*;

public final class DevStartup{
  private DevStartup(){
  }

  public static void loadSave(String name){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      Vars.ui.load.runLoadSave(Vars.control.saves.getSaveSlots().find(s -> s.getName().equals(name)));
    });
  }

  public static void showTantrosDialog(){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      LuluMod.tantrosDialog.show();
    });
  }

  public static void showResearchsDialog(){
    Events.on(EventType.ClientLoadEvent.class, e -> {
      new SglTechTreeDialog().show();
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

  public static void onLoaded(Runnable runnable){
    Events.on(EventType.ClientLoadEvent.class, e -> runnable.run());
  }
}
