package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.lib.gdxAI.btree.*;
import LLL.type.*;
import LLL.world.neoplasm.effect.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.*;

public abstract class GrowLeafTask extends LeafTask<NeoplasmBehavior> implements Visible{
  public NeoplasmNeuron.NeoplasmNeuronBuild neuron;

  protected float timer = 0;
  protected float interval = 1;
  protected Func<NeoplasmBehavior, Float> dynamicIntervalFunc;
  protected boolean isDynamicInterval = false;

  public Block block;
  public boolean hasBlockLimit = false;
  public int blockLimit = 99999;

  public void init(NeoplasmNeuron.NeoplasmNeuronBuild neuron){
    this.neuron = neuron;
    initVisible();
  }

  @Override
  public String description(){
    return "目标方块: " + block.localizedName;
  }

  @Override
  public void initVisible(){
    neuron.visibles.add(this);
  }

  public void setInterval(float interval){
    this.interval = interval;
    isDynamicInterval = false;
  }

  public void setInterval(Func<NeoplasmBehavior, Float> intervalFunc){
    this.dynamicIntervalFunc = intervalFunc;
    isDynamicInterval = true;
  }

  @Override
  public void reset(){
    super.reset();
  }

  public void handleStructureChange(Tile tile, boolean add){
  }

  public abstract Status grow();

  @Override
  public Status execute(){
    float currentInterval;

    if(isDynamicInterval){
      currentInterval = dynamicIntervalFunc.get(getObject());
    }else{
      currentInterval = interval;
    }

    timer += Time.delta;
    if(timer > currentInterval && neuron.items.has(block.requirements)){
      timer = 0;

      if(checkBlockLimit()){
        return Status.SUCCEEDED;
      }
      return grow();
    }

    return defaultStatus();
  }

  public Status defaultStatus(){
    return Status.FAILED;
  }

  public boolean checkBlockLimit(){
    return hasBlockLimit
      && neuron.cortex.ownedBlocks.containsKey(block)
      && neuron.cortex.ownedBlocks.get(block).size >= blockLimit;
  }

  public void write(Writes write){
    write.f(timer);
  }

  public void read(Reads read){
    timer = read.f();
  }

  public void addToWrite(Seq<Entityc> toWrite){

  }

  public void getFromRead(Queue<Entityc> toRead){

  }

  public static boolean shouldNetworkUpdate(){
    return !Vars.net.active() || Vars.net.server();
  }
}
