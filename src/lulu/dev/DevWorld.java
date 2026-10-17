package lulu.dev;

import mindustry.*;
import mindustry.type.*;
import mindustry.world.modules.*;

public final class DevWorld{
  private DevWorld(){
  }

  public static void disableSectorWaves(Planet planet){
    planet.sectors.each(sector -> {
      sector.info.waves = false;
      sector.info.attack = false;
    });
  }

  public static void fillCoreItems(){
    ItemModule items = Vars.player.team().core().items;
    Vars.content.items().each(item -> items.set(item, 1_000_000));
  }

  public static void setInfiniteResources(boolean enabled){
    Vars.state.rules.infiniteResources = enabled;
  }
}
