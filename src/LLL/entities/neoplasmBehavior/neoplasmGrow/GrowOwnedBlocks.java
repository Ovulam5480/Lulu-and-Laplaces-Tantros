package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.lib.gdxAI.btree.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.world.*;

public class GrowOwnedBlocks extends GrowLeafTask{
  public Block beReplace;

  public GrowOwnedBlocks(Block block, Block beReplace){
    this.block = block;
    this.beReplace = beReplace;
  }

  @Override
  public Status grow(){
    Seq<Tile> tiles = neuron.cortex.ownedBlocks.get(beReplace);
    if(tiles == null) return Status.FAILED;

    int max = 0;
    while(max < 5 && tiles.size > 0){
      Tile tile = tiles.first();
      if(getObject().tryPlaceBlock(tile, block, tile.build.rotation)){
        return Status.SUCCEEDED;
      }else{
        tiles.remove(0);
        tiles.add(tile);
      }

      max++;
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
    return task;
  }

  @Override
  public void draw(){
  }

  @Override
  public void initVisible(){
  }
}
