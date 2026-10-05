package LLL.world.blocks;

import LLL.lib.singularity.world.blocks.product.*;
import arc.func.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

public class FrameBlock extends PayloadCrafter{
  public Block replacedBlock;
  public float requiredProgress = 0.5f;
  public float requiredProgressRound = 0.05f;

  private Prov<Block> replacedBlockProv;

  public static ConstructBlock.ConstructBuild toReplaced;

  public FrameBlock(String name, Prov<Block> replacedBlock){
    super(name);
    update = true;
    instantBuild = true;

    this.replacedBlockProv = replacedBlock;
  }

  public FrameBlock(String name, Block replacedBlock){
    super(name);
    update = true;
    instantBuild = true;

    this.replacedBlock = replacedBlock;
  }

  public FrameBlock(String name){
    this(name, () -> Blocks.air);
  }

  @Override
  public void init(){
    super.init();

    if(replacedBlock == null) replacedBlock = replacedBlockProv.get();
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return tile != null
      && tile.build instanceof ConstructBlock.ConstructBuild cb
      && cb.previous == replacedBlock
      && cb.progress >= requiredProgress - requiredProgressRound
      && cb.progress <= requiredProgress + requiredProgressRound;
  }

  @Override
  public boolean canReplace(Block other){
    return other instanceof ConstructBlock;
  }

  @Override
  public void placeBegan(Tile tile, Block previous, Unit builder){
    if(tile.build instanceof ConstructBlock.ConstructBuild cb){
      toReplaced = cb;
    }
  }

  @Override
  public void placeEnded(Tile tile, Unit builder, int rotation, Object config){
    if(toReplaced != null){

    }
    toReplaced = null;
  }

  public class FrameBlockBuild extends PayloadCrafterBuild{
    @Override
    public void craftTrigger(){

    }
  }
}
