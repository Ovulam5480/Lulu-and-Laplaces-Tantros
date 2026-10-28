package lulu.world;

import arc.*;
import arc.struct.*;
import lulu.content.extensions.*;
import lulu.world.components.buildComps.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.blocks.power.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class OvulamPowerGraph extends PowerGraph{
  private static final FieldAccessor<PowerGraph, PowerGraphUpdater> entityField = Reflection.accessField(PowerGraph.class, PowerGraphUpdater.class, "entity");
  private static boolean covered = false;
  public IntFloatMap powerTypes;
  public Seq<Building>[] typeProducers;

  static{
    Events.run(EventType.Trigger.afterGameUpdate, () -> {
      if(covered){
        for(PowerGraphUpdaterc entity : Groups.powerGraph){
          if(entity.graph() instanceof OvulamPowerGraph) continue;
          PowerGraph graph = new OvulamPowerGraph();
          graph.addGraph(entity.graph());
          //todo graph.update?
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

    typeProducers = new Seq[Vars.content.getBy(OvulamContentType.power.value).size];
    for(int i = 0; i < typeProducers.length; i++){
      typeProducers[i] = new Seq<>(false, 4);
    }
  }

  @Override
  public float getPowerProduced(){
    float powerProduced = 0f;
    var items = producers.items;
    for(int i = 0; i < producers.size; i++){
      var producer = items[i];
      powerProduced += producer.getPowerProduction() * producer.delta();
    }
    return powerProduced;
  }

  @Override
  public float getPowerNeeded(){
    float powerNeeded = 0f;
    var items = consumers.items;
    for(int i = 0; i < consumers.size; i++){
      var consumer = items[i];
      var consumePower = consumer.block.consPower;
      if(consumer.shouldConsumePower){
        powerNeeded += consumePower.requestedPower(consumer) * consumer.delta();
      }
    }
    return powerNeeded;
  }

  @Override
  public void update(){
    for(Seq<Building> seq : typeProducers){
      if(!seq.isEmpty())seq.clear();
    }

    for(Building producer : producers){
      int id = 0;
      if(producer instanceof PowerTypeBuildComp ptb){
        id = ptb.getPowerType().id;
      }
      typeProducers[id].add(producer);
    }
    super.update();
  }

  @Override
  public void add(Building build){
    super.add(build);
  }

  @Override
  public void addGraph(PowerGraph graph){
    if(graph != this){
      PowerGraphUpdater entity = entityField.get(graph);

      if(graph.getClass() == OvulamPowerGraph.class && graph.all.size > all.size){
        graph.addGraph(this);
        return;
      }

      if(entity != null) entity.remove();

      for(Building tile : graph.all){
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
