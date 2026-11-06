package lulu.world;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import lulu.content.OvulamContents;
import lulu.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.blocks.power.*;
import mindustry.world.consumers.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

import java.util.*;

public class OvulamPowerGraph extends PowerGraph{
  private static final FieldAccessor<PowerGraph, PowerGraphUpdater> entityField = Reflection.accessField(PowerGraph.class, PowerGraphUpdater.class, "entity");
  private static boolean covered = false;
  public static Seq<PowerGraphUpdaterc> covereds = new Seq<>();

  public Seq<PowerChannelEntityComp> allComps = new Seq<>();

  public Seq<PowerChannelEntityComp>[] typeProducers;
  public Seq<PowerChannelEntityComp>[] typeConsumers;
  public Seq<PowerChannelEntityComp>[] typeBatteries;
  public WindowedMean[] powerBalances;

  public float[] lastPowerProduceds;
  public float[] lastPowerNeededs;
  public float[] lastPowerCapacitys;
  public float[] lastPowerStoreds;
  public float[] lastTotalPowerCapacitys;


  static{
    Events.run(EventType.Trigger.afterGameUpdate, () -> {
      if(covered){
        for(PowerGraphUpdaterc entity : Groups.powerGraph){
          if(!(entity.graph() instanceof OvulamPowerGraph)){
            covereds.add(entity);
          }
        }

        for(PowerGraphUpdaterc entity : covereds){
          if(entity.graph() instanceof OvulamPowerGraph) continue;
          PowerGraph graph = new OvulamPowerGraph();
          graph.reflow(entity.graph().all.first());
          graph.update();
        }

        covered = false;
        covereds.clear();
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

    int n = OvulamContents.powers().size;
    typeProducers = new Seq[n];
    typeConsumers = new Seq[n];
    typeBatteries = new Seq[n];
    powerBalances = new WindowedMean[n];
    for(int i = 0; i < n; i++){
      typeProducers[i] = new Seq<>(false, 4);
      typeConsumers[i] = new Seq<>(false, 4);
      typeBatteries[i] = new Seq<>(false, 4);
      powerBalances[i] = new WindowedMean(60);
    }
    lastPowerProduceds = new float[n];
    lastPowerNeededs = new float[n];
    lastPowerCapacitys = new float[n];
    lastPowerStoreds = new float[n];
    lastTotalPowerCapacitys = new float[n];
  }

//  @Override
//  public float getLastScaledPowerIn(){
//    return lastScaledPowerIn;
//  }
//
//  @Override
//  public float getLastScaledPowerOut(){
//    return lastScaledPowerOut;
//  }

  @Override
  public float getLastCapacity(){
    return lastTotalPowerCapacitys[0];
  }

  @Override
  public float getPowerBalance(){
    return powerBalances[0].rawMean();
  }

  @Override
  public boolean hasPowerBalanceSamples(){
    return powerBalances[0].hasEnoughData();
  }

  @Override
  public float getLastPowerNeeded(){
    return lastPowerNeededs[0];
  }

  @Override
  public float getLastPowerProduced(){
    return lastPowerProduceds[0];
  }

  @Override
  public float getLastPowerStored(){
    return lastPowerStoreds[0];
  }

  @Override
  public float getSatisfaction(){
    if(Mathf.zero(lastPowerProduceds[0])){
      return 0f;
    }else if(Mathf.zero(lastPowerNeededs[0])){
      return 1f;
    }
    return Mathf.clamp(lastPowerProduceds[0] / lastPowerNeededs[0]);
  }

  public float sum(float[] arr){
    float sum = 0f;
    for(float v : arr) sum += v;
    return sum;
  }

  public float min(float[] arr){
    float min = 2;
    for(float v : arr){
      if(v < min)min = v;
    }
    return min;
  }

  @Override
  public float getPowerProduced(){
    return sum(lastPowerProduceds);
  }

  @Override
  public float getPowerNeeded(){
    return sum(lastPowerNeededs);
  }

  @Override
  public float getBatteryStored(){
    return sum(lastPowerStoreds);
  }

  @Override
  public float getBatteryCapacity(){
    return sum(lastPowerCapacitys);
  }

  @Override
  public float getTotalBatteryCapacity(){
    return sum(lastTotalPowerCapacitys);
  }

  @Override
  public void update(){
    boolean cheating = false;

    if(consumers.isEmpty()){
      for(Seq<PowerChannelEntityComp> consumers : typeConsumers){
        if(!consumers.isEmpty() && consumers.first() instanceof Teamc tc && tc.cheating()){
          cheating = true;
          break;
        }
      }
    }else if(consumers.first().cheating()){
      cheating = true;
    }

    if(cheating){
      all.each(b -> b.power.status = 1f);
      Arrays.fill(lastPowerProduceds, 1f);
      Arrays.fill(lastPowerNeededs, 1f);
      return;
    }

    for(int i = 0; i < typeProducers.length; i++){
      lastPowerProduceds[i] = 0f;
      for(PowerChannelEntityComp b : typeProducers[i]){
        lastPowerProduceds[i] += b.shouldProducePower() ? b.getPowerProduce(i) * b.getDelta() : 0;
      }

      lastPowerNeededs[i] = 0f;
      for(PowerChannelEntityComp b : typeConsumers[i]){
        lastPowerNeededs[i] += b.shouldConsumePower() ? b.getPowerConsume(i)  * b.getDelta() : 0;
      }

      lastPowerCapacitys[i] = 0f;
      lastPowerStoreds[i] = 0f;
      lastTotalPowerCapacitys[i] = 0f;
      for(PowerChannelEntityComp b : typeBatteries[i]){
        lastPowerCapacitys[i] += b.getPowerCapacity(i);
        lastPowerStoreds[i] += b.getPowerStored(i);
        lastTotalPowerCapacitys[i] += b.getTotalPowerCapacity(i);
      }
    }

    for(Building b : producers){
      lastPowerProduceds[0] += b.getPowerProduction() * b.delta();
    }

    for(Building b : consumers){
      ConsumePower consumePower = b.block.consPower;
      if(b.shouldConsumePower){
        lastPowerNeededs[0] += consumePower.requestedPower(b) * b.delta();
      }
    }

    for(Building b : batteries){
      lastPowerStoreds[0] += b.power.status * b.block.consPower.capacity;
      lastPowerCapacitys[0] += (1f - b.power.status) * b.block.consPower.capacity;
      lastTotalPowerCapacitys[0] += b.block.consPower.capacity;
    }

    //todo lastScaledPowerIn/Out

    for(int i = 0; i < typeProducers.length; i++){
      float powerProduced = lastPowerProduceds[i];
      float powerNeeded = lastPowerNeededs[i];

      boolean charged = false;

      float margin = OvulamContents.power(i).applyPowerGraph(this, powerProduced, powerNeeded);
      //todo energyDelta -> Diode二极管
      float energyDelta = 0;
      powerBalances[i].add((margin + energyDelta) / Time.delta);
      energyDelta = 0f;

      if(!Mathf.zero(margin)){
        if(margin > 0){
          charged = true;
          powerProduced -= chargeBatteries(margin, i);
        }else{
          float powerBatteryUsed = useBatteries(-margin, i);
          powerProduced += powerBatteryUsed;
          lastPowerProduceds[i] += powerBatteryUsed;
        }
      }
      distributePower(powerNeeded, powerProduced, charged, i);
    }
    allComps.each(c -> c.power().status = min(c.power().typeStatus));
  }

  @Override
  public void distributePower(float needed, float produced, boolean charged){
    this.distributePower(needed, produced, charged, 0);
  }

  public void distributePower(float needed, float produced, boolean charged, int id){
    float coverage = Mathf.zero(needed)
      && Mathf.zero(produced)
      && !charged
      && Mathf.zero(lastPowerStoreds[id]) ? 0f : (Mathf.zero(needed) ? 1f : Math.min(1, produced / needed));

    if(id == 0){
      for(Building b : consumers){
        ConsumePower cons = b.block.consPower;
        if(cons.buffered){
          if(!Mathf.zero(cons.capacity)){
            b.power.status = Mathf.clamp(Mathf.lerp(b.power.status, 1, coverage * b.delta()));
          }
        }else{
          if(b.shouldConsumePower){
            b.power.status = coverage;
          }else{
            b.power.status = Math.min(1, produced / (needed + cons.usage * b.delta()));
            if(Float.isNaN(b.power.status)){
              b.power.status = 0f;
            }
          }
        }
      }
    }

    for(PowerChannelEntityComp b : typeBatteries[id]){
      if(b.getPowerChannelComp().bufferedPowerChannel()){
        float chargeRate = b.getPowerConsume(id) * coverage * b.getDelta();
        b.power().typeCapacity[id] = Mathf.clamp(b.power().typeCapacity[id] + chargeRate, 0f, b.getTotalPowerCapacity(id));
      }else {
        if(b.shouldConsumePower()){
          b.power().typeStatus[id] = coverage;
        }else {
          b.power().typeStatus[id] = Math.min(1, produced / (needed + b.getPowerConsume(id) * b.getDelta()));
          if(Float.isNaN(b.power().typeStatus[id])){
            b.power().typeStatus[id] = 0f;
          }
        }
      }
    }
  }

  @Override
  public float useBatteries(float needed){
    return this.useBatteries(needed, 0);
  }

  public float useBatteries(float needed, int id){
    float stored = lastPowerStoreds[id];
    if(Mathf.equal(stored, 0f)) return 0f;

    float used = Math.min(stored, needed);
    float consumedPowerPercentage = Math.min(1.0f, needed / stored);
    if(id == 0){
      for(Building battery : batteries){
        if(battery.enabled){
          battery.power.status *= (1f - consumedPowerPercentage);
        }
      }
    }

    for(PowerChannelEntityComp c : typeBatteries[id]){
      c.power().typeCapacity[id] *= (1f - consumedPowerPercentage);
    }

    return used;
  }

  @Override
  public float chargeBatteries(float excess){
    return this.chargeBatteries(excess, 0);
  }

  public float chargeBatteries(float excess, int id){
    float capacity = lastPowerCapacitys[id];
    float chargedPercent = Math.min(excess/capacity, 1f);
    if(Mathf.equal(capacity, 0f)) return 0f;

    if(id == 0){
      for(Building battery : batteries){
        if(battery.enabled && battery.block.consPower.capacity > 0f){
          battery.power.status += (1f - battery.power.status) * chargedPercent;
        }
      }
    }

    for(PowerChannelEntityComp c : typeBatteries[id]){
      c.power().typeCapacity[id] += c.getPowerCapacity(id) * chargedPercent;
    }

    return Math.min(excess, capacity);
  }

  @Override
  public void add(Building build){
    if(build == null || build.power == null) return;

    if(build instanceof PowerChannelEntityComp p){
      add(p);
    }else if(build.power.graph != this || !build.power.init){
      if(build.power.graph != null && build.power.graph != this){
        PowerGraphUpdater updater = entityField.get(build.power.graph);
        if(updater != null) updater.remove();
      }

      build.power.graph = this;
      build.power.init = true;
      all.add(build);
      addGraph(build.power.graph);

      if(build.block.outputsPower && build.block.consumesPower && !build.block.consPower.buffered){
        producers.add(build);
        consumers.add(build);
      }else if(build.block.outputsPower && build.block.consumesPower){
        batteries.add(build);
      }else if(build.block.outputsPower){
        producers.add(build);
      }else if(build.block.consumesPower && build.block.consPower != null){
        consumers.add(build);
      }
    }
  }

  public void add(PowerChannelEntityComp p){
    if(p.power().graph != null && p.power().graph != this){
      PowerGraphUpdater updater = entityField.get(p.power().graph);
      if(updater != null) updater.remove();
    }

    p.power().graph = this;
    p.power().init = true;
    allComps.add(p);
    addGraph(p.power().graph);

    Power[] types = p.getPowerChannel();
    boolean outputs = p.getPowerChannelComp().producesPowerChannel();
    boolean consumes = p.getPowerChannelComp().consumesPowerChannel();
    boolean buffered = p.getPowerChannelComp().bufferedPowerChannel();

    for(Power power : types){
      int type = power.id;

      if(outputs && consumes){
        if(buffered){
          typeBatteries[type].add(p);
        }else {
          typeProducers[type].add(p);
          typeConsumers[type].add(p);
        }
      }else {
        if(outputs) typeProducers[type].add(p);
        if(consumes) typeConsumers[type].add(p);
        if(buffered) typeBatteries[type].add(p);
      }
    }
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
