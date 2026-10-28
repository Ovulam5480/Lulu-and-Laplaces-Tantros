package lulu.world.blocks;

import lulu.content.*;
import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
import arc.func.*;
import lulu.world.recipe.*;
import lulu.world.recipe.producers.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class FrameBlock extends Block implements PayloadCrafterBlockComp<PayloadCrafterBuildBuilding>{
  public Block replacedBlock = Blocks.copperWallLarge;
  public Block targetBlock = Blocks.copperWallLarge;
  private Prov<Block> replacedBlockProv;
  private Prov<Block> targetBlockProv;

  public float requiredProgress = 0.5f;
  public float requiredProgressRound = 0.05f;

  private static ConstructBlock.ConstructBuild toReplaced;

  public FrameBlock(String name){
    super(name);
    instantBuild = true;
    itemCapacityMulti(1);
    payloadCapacityMulti(1);
  }

  @SafeVarargs
  public static FrameBlock registerFrameList(Block replaced, Block target, Cons<BaseConsumers<PayloadCrafterBuildBuilding>>...buildList){
    Block lastBlock = target;

    for(int i = 0; i < buildList.length; i++){
      int j = buildList.length - i;
      Block lastTarget = lastBlock;
      lastBlock = new FrameBlock(target.name + "-frame-block-" + j){{
        newConsume();
        buildList[j - 1].get(consume());
        newProduce();
        produce().add(new ProduceValid<>());

        boolean first = j == 1;
        buildVisibility = first ? OvulamBuildVisibility.frameBuilder : BuildVisibility.hidden;
        if(first){
          this.replacedBlock = replaced;
        }
        targetBlock = lastTarget;
      }};
    }

    return (FrameBlock)lastBlock;
  }

  @Override
  public void init(){
    if(replacedBlock == null) replacedBlock = replacedBlockProv.get();
    if(targetBlock == null) targetBlock = replacedBlockProv.get();
    if(targetBlock != null){
      size = targetBlock.size;
      category = targetBlock.category;
    }

    super.init();
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
      toReplaced = null;
    }
  }

  public class FrameBlockBuild extends PayloadCrafterBuildBuilding{
    @Override
    public void craftTrigger(){
      tile.setBlock(targetBlock, team);
      placeEffect.at(x, y, size);
    }
  }

}
