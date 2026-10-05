package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.lib.gdxAI.btree.*;
import LLL.world.blocks.module.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.world.*;

public class GrowOwnedBlocksSelect<T extends NeoplasmBuildModule> extends GrowLeafTask{
  public Block beReplace;
  public Seq<Tile> tiles = new Seq<>();
  public Seq<Tile> allTiles = new Seq<>();
  public Boolf<T> filter;
  public int maxCapital = 50;
  public static Seq<Tile> empty = new Seq<>();

  public GrowOwnedBlocksSelect(Block block, Block beReplace, Boolf<T> filter){
    this.block = block;
    this.beReplace = beReplace;
    this.filter = filter;
  }

  public void selectFrom(){
    tiles.clear();

    if(allTiles.isEmpty()){
      for(Tile tile : neuron.cortex.ownedBlocks.get(beReplace, empty)){
        if(filter.get((T)tile.build)){
          allTiles.add(tile);
        }
      }
    }else{
      int max = Math.min(maxCapital, allTiles.size - 1);
      for(int i = 0; i < max; i++){
        tiles.add(allTiles.get(i));
      }
      allTiles.removeRange(0, max);
    }
  }

  @Override
  public Status grow(){
    if(tiles.isEmpty()){
      selectFrom();
    }

    int max = 0;
    while(max < 5 && tiles.size > 0){
      Tile tile = tiles.pop();
      if(tile.build != null && getObject().tryPlaceBlock(tile, block, tile.build.rotation)){
        return Status.SUCCEEDED;
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
    Draw.color(Color.acid, 0.7f);
    for(Tile tile : allTiles){
      Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
    }
  }
}