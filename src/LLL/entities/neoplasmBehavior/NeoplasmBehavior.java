package LLL.entities.neoplasmBehavior;

import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.lib.gdxAI.btree.*;
import LLL.lib.multiblock.extend.*;
import LLL.util.struct.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.*;
import LLL.world.neoplasm.effect.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

//瘤液行为, 用于放置器官, 血管蔓延不属于瘤液行为
public class NeoplasmBehavior{
  protected BehaviorTree<NeoplasmBehavior> tree;
  protected Seq<GrowLeafTask> tasks = new Seq<>();
  public final NeoplasmNeuron.NeoplasmNeuronBuild neuron;
  private static final Seq<Tile> tempTiles = new Seq<>();

  private static final Seq<Tile> empty = new Seq<>();

  public NeoplasmBehavior(NeoplasmNeuron.NeoplasmNeuronBuild neuron, NeoplasmTrees.NeoplasmTree neoplasmTree){
    this.neuron = neuron;

    tree = neoplasmTree.tree;
    tree.setObject(this);

    tasks = neoplasmTree.allTasks;
    tasks.each(t -> t.init(neuron));
  }

  public void update(){
    tree.step();
  }

  public float getItem(Item item){
    return neuron.items.get(item);
  }

  public float getItemFract(Item item){
    return (float)neuron.items.get(item) / neuron.block.itemCapacity;
  }

  public int getOwnerBlock(Block block){
    if(neuron.cortex == null){
      return 0;
    }

    return neuron.cortex.ownedBlocks.get(block, empty).size;
  }

  public void handleStructureChange(NeoplasmVessel.NeoplasmVesselBuild vessel, int x, int y, boolean add){
    for(GrowLeafTask task : tasks){
      task.handleStructureChange(vessel.getTile(), add);
    }
  }

  public boolean tryPlaceBlock(Tile tile, Block toPlace){
    return tryPlaceBlock(tile, toPlace, 0);
  }

  public boolean tryPlaceBlock(Tile tile, Block toPlace, int rotation){
    tile.getLinkedTilesAs(toPlace, tempTiles);
    if(tempTiles.contains(t -> (t.build instanceof NeoplasmBuildModule m && !m.neuronValid()))){
      return false;
    }

    if(neuron.cortex.validPlace(toPlace, neuron.team, tile.x, tile.y, 0)){
      tile.getLinkedTilesAs(toPlace, t -> {
        if(t.build instanceof NeoplasmBuildModule m){
          m.suicide();
        }
      });

      if(toPlace instanceof MultiBlock mb){
        mb.linkTiles(tile.x, tile.y, toPlace.size, 0).each(t -> {
          if(t.build instanceof NeoplasmBuildModule m){
            m.suicide();
          }
        });
      }
      Call.constructFinish(tile, toPlace, null, (byte)rotation, neuron.team, neuron);

      neuron.items.remove(toPlace.requirements);//todo 应该在前额叶内委托?
      return true;
    }
    return false;
  }

  public void suicide(Tile tile){
    if(tile.build instanceof NeoplasmBuildModule m){
      m.suicide();
    }
  }

  public boolean checkStructureD4(Tile tile){
    GridIntMap<Integer> map = neuron.cortex.structure;
    for(Point2 point2 : Geometry.d4){
      int nx = tile.x + point2.x;
      int ny = tile.y + point2.y;

      if(map.containsKey(nx, ny)){
        return true;
      }
    }

    return false;
  }


  public void writeTask(Writes write){
    tasks.each(task -> task.write(write));
  }

  public void readTask(Reads read){
    tasks.each(task -> task.read(read));
  }

  public void addToWrite(Seq<Entityc> toWrite){
    tasks.each(task -> task.addToWrite(toWrite));
  }

  public void getFromRead(Queue<Entityc> toRead){
    tasks.each(task -> task.getFromRead(toRead));
  }
}
