package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.graphics.*;
import LLL.lib.gdxAI.btree.*;
import LLL.util.struct.*;
import LLL.world.neoplasm.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

public class GrowItemFilter extends GrowLeafTask{
  public GridIntMap<NeoplasmVessel.NeoplasmVesselBuild> itemFlows = new GridIntMap<>();
  public Boolf<Item> filter;

  private int saved;

  public GrowItemFilter(Block block, Boolf<Item> filter){
    this.block = block;
    this.filter = filter;

    Events.on(VesselFlowTrigger.class, e -> {
      if(!shouldNetworkUpdate()) return;

      if(neuron == e.vessel.neuron()){
        if(filter.get(e.item) && e.add){
          itemFlows.put(e.vessel);
        }else{
          itemFlows.remove(e.vessel);
        }
      }
    });
  }

  @Override
  public Status grow(){
    int max = 5;
    while(max > 0 && !itemFlows.map.isEmpty()){
      NeoplasmVessel.NeoplasmVesselBuild build = itemFlows.random().value;
      if(getObject().tryPlaceBlock(build.tile, block)){
        itemFlows.remove(build);
        return Status.SUCCEEDED;
      }

      max--;
    }

    return Status.FAILED;
  }

  @Override
  public void addToWrite(Seq<Entityc> toWrite){
    itemFlows.each((x, y, b) -> {
      toWrite.add(b);
    });
  }

  @Override
  public void getFromRead(Queue<Entityc> toRead){
    while(saved-- > 0){
      itemFlows.put((Posc)toRead.first());
      toRead.removeFirst();
    }
  }

  @Override
  public void write(Writes write){
    write.i(itemFlows.size());
  }

  @Override
  public void read(Reads read){
    saved = read.i();
  }

  @Override
  protected Task<NeoplasmBehavior> copyTo(Task<NeoplasmBehavior> task){
    GrowItemFilter growTask = (GrowItemFilter)task;
    growTask.itemFlows.map.putAll(itemFlows.map);

    return growTask;
  }

  @Override
  public void resetTask(){
    super.resetTask();
    itemFlows.clear();
  }

  @Override
  public void draw(){
    Draw.color(Color.acid, 0.7f);
    OvulamDraw.eachCameraTiles(t -> {
      if(itemFlows.containsKey(t.x, t.y)){
        Fill.rect(t.worldx(), t.worldy(), 8, 8);
      }
    });
  }

  public static class VesselFlowTrigger{
    public NeoplasmVessel.NeoplasmVesselBuild vessel;
    public Item item;
    public boolean add;

    public void set(NeoplasmVessel.NeoplasmVesselBuild vessel, Item item, boolean add){
      this.vessel = vessel;
      this.item = item;
      this.add = add;
    }
  }
}
