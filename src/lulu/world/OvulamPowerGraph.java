package lulu.world;

import arc.*;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.world.blocks.power.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class OvulamPowerGraph extends PowerGraph{
  private static final FieldAccessor<PowerGraph, PowerGraphUpdater> entityField = Reflection.accessField(PowerGraph.class, PowerGraphUpdater.class, "entity");
  private static boolean covered = false;

  static {
    Events.run(EventType.Trigger.afterGameUpdate, () -> {
      if(covered){
        for(PowerGraphUpdaterc entity : Groups.powerGraph){
          if(entity.graph() instanceof OvulamPowerGraph) continue;
          new OvulamPowerGraph().addGraph(entity.graph());
        }
        covered = false;
      }
    });
  }

  public OvulamPowerGraph(){
    this(false);
  }

  public OvulamPowerGraph(boolean noEntity){
    super(true);
    if(!noEntity){
      PowerGraphUpdater entity = new OvulamPowerGraphUpdater();
      entity.graph = this;
      entityField.set(this, entity);
    }
  }

  @Override
  public void addGraph(PowerGraph graph) {
    if (graph != this) {
      PowerGraphUpdater entity = entityField.get(graph);

      if (entity != null) {
        entity.remove();
      }

      for(Building tile : graph.all) {
        add(tile);
      }

      checkAdd();
    }
  }

  public static class OvulamPowerGraphUpdater extends PowerGraphUpdater{
    @Override
    public void remove(){
      super.remove();
      covered = true;
    }
  }
}
