package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.lib.gdxAI.btree.*;
import LLL.world.neoplasm.effect.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class GrowFunction extends GrowLeafTask{
  public Function function;
  public int max;

  public GrowFunction(Block block, int max, Function function){
    this.block = block;
    this.max = max;
    this.function = function;
  }

  @Override
  public Status grow(){
    for(int i = 0; i < max; i++){
      int value = function.get(neuron, i);

      Tile tile = Vars.world.tile(value);
      if(tile.block() == block) continue;

      if(getObject().checkStructureD4(tile)){
        if(getObject().tryPlaceBlock(tile, block)){
          return Status.FAILED;
        }
      }
    }

    return Status.SUCCEEDED;
  }

  @Override
  public Status defaultStatus(){
    if(neuron.cortex == null) return Status.FAILED;

    for(int i = 0; i < max; i++){
      int value = function.get(neuron, i);
      Tile tile = Vars.world.tile(value);

      if(tile.block() != block && neuron.cortex.validPlace(block, neuron.team, tile.x, tile.y, 0)){
        return Status.FAILED;
      }
    }

    return Status.SUCCEEDED;
  }

  @Override
  protected Task<NeoplasmBehavior> copyTo(Task<NeoplasmBehavior> task){
    return task;
  }

  @Override
  public void write(Writes write){
  }

  @Override
  public void read(Reads read){
  }

  @Override
  public void draw(){
    for(int i = 0; i < max; i++){
      int value = function.get(neuron, i);
      Tile tile = Vars.world.tile(value);

      if(tile.block() != block && neuron.cortex.validPlace(block, neuron.team, tile.x, tile.y, 0)){
        Draw.color(Color.acid, 0.7f);
      }else{
        Draw.color(Pal.remove, 0.7f);
      }

      Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
    }
  }

  public interface Function{
    int get(NeoplasmNeuron.NeoplasmNeuronBuild build, int i);
  }
}
