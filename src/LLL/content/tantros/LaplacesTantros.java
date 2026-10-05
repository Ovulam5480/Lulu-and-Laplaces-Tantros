package LLL.content.tantros;

import arc.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class LaplacesTantros{
  public static Planet tantros;
  private static Sector loadoutFrom;

  public static void load(){
    tantros = new LaplacesTantrosPlanet();

    new SectorPreset("anchor", tantros, tantros.startSector){{
      requireUnlock = false;

      rules = r -> {
        r.env &= ~Env.underwater;
      };
    }};

    TantrosTechTree.load();
    registerEvents();
  }

  public static void registerEvents(){
    //todo 多人游戏测试
    Events.on(EventType.SectorLaunchLoadoutEvent.class, e -> {
      Sector sector = e.from;

      if(sector.id != tantros.startSector){
        loadoutFrom = sector;
      }
    });

    Events.on(EventType.SectorLaunchEvent.class, e -> {
      Sector sector = loadoutFrom;

      if(sector != null && sector.id != tantros.startSector){
        sector.info.items.clear();
        sector.info.hasCore = false;
        sector.info.production.clear();
        sector.saveInfo();
      }

      loadoutFrom = null;
    });
  }
}
