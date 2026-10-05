package LLL.lib.seam;

import LLL.lib.seam.core.*;
import LLL.lib.seam.world.blocks.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;

public class Seam{
  public static final Seq<SubWorld> worlds = new Seq<>();
  public static final Seq<SubWorldMonitor.SubWorldMonitorBuild> monitors = new Seq<>();

  public Seam(){
    Log.info("[Seam] Loaded.");
  }

  //todo
  public void loadContent(){
    //SeamBlocks.load();
  }

  public void init(){
    Log.info("[Seam] Foundation initialized. Ready to create SubWorlds.");

    Events.run(Trigger.preDraw, () -> {
      for(SubWorldMonitor.SubWorldMonitorBuild b : monitors) b.renderSubWorld();
    });

    Events.run(Trigger.update, () -> {
      for(SubWorld w : worlds) w.update();
    });
  }

  public static SubWorld createWorld(){
    SubWorld world = new SubWorld();
    worlds.add(world);
    return world;
  }

  public static void removeWorld(SubWorld world){
    if(worlds.remove(world)){
      world.dispose();
    }
  }
}