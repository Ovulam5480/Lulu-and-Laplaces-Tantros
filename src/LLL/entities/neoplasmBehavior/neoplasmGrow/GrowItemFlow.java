package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.graphics.*;
import LLL.lib.gdxAI.btree.*;
import LLL.util.struct.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

public class GrowItemFlow extends GrowLeafTask{
  public GridIntMap<IntMap<FloatQueue>> itemsTimer = new GridIntMap<>();
  public IntSeq canGrow = new IntSeq();

  public float interval;
  public IntIntMap itemFlow = new IntIntMap();

  public GrowItemFlow(Block block, float interval, ObjectIntMap<Item> flow){
    this.block = block;
    this.interval = interval;

    for(ObjectIntMap.Entry<Item> entry : flow){
      itemFlow.put(entry.key.id, entry.value);
    }

    Events.on(GrowItemFilter.VesselFlowTrigger.class, e -> {
      if(!shouldNetworkUpdate()
        || neuron != e.vessel.neuron()
        || !itemFlow.containsKey(e.item.id)) return;

      int id = e.item.id;
      float time = Time.time;
      int x = e.vessel.tileX(), y = e.vessel.tileY(), pos = e.vessel.pos();

      if(!e.add){
        canGrow.removeValue(e.vessel.pos());
        itemsTimer.get(x, y).remove(id);

        if(itemsTimer.get(x, y).isEmpty()){
          itemsTimer.remove(x, y);
        }
        return;
      }

      if(!itemsTimer.containsKey(x, y)){
        itemsTimer.put(x, y, new IntMap<>());
      }

      IntMap<FloatQueue> map = itemsTimer.get(x, y);
      if(!map.containsKey(id)){
        map.put(id, new FloatQueue());
      }

      FloatQueue queue = map.get(id);
      queue.addFirst(time);

      while(time - queue.last() > interval){
        queue.removeLast();
      }

      if(canGrow.contains(pos)){
        if(queue.size < itemFlow.get(id)){
          canGrow.removeValue(pos);
        }
      }else{
        boolean grow = true;
        for(IntIntMap.Entry entry : itemFlow){
          int itemId = entry.key;
          int amount = entry.value;

          if(!map.containsKey(itemId) || map.get(itemId).size < amount){
            grow = false;
            break;
          }
        }
        if(grow){
          canGrow.add(pos);
        }
      }
    });
  }

  @Override
  public Status grow(){
    int max = 5;
    while(max > 0 && !canGrow.isEmpty()){
      int pos = canGrow.random();

      Building building = Vars.world.build(pos);
      if(getObject().tryPlaceBlock(building.tile, block)){
        canGrow.removeValue(pos);
        return Status.SUCCEEDED;
      }

      max--;
    }

    return Status.FAILED;
  }

  @Override
  public void write(Writes write){

  }

  @Override
  public void read(Reads read){

  }


  @Override
  protected Task<NeoplasmBehavior> copyTo(Task<NeoplasmBehavior> task){
    return null;
  }

  @Override
  public void draw(){
    Draw.color(Color.acid, 0.7f);
    OvulamDraw.eachCameraTiles(tile -> {
      if(itemsTimer.containsKey(tile.x, tile.y)){
        Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
      }
    });
  }
}
