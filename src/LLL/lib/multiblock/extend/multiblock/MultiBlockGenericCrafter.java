package LLL.lib.multiblock.extend.multiblock;

import LLL.lib.multiblock.extend.*;
import mindustry.world.blocks.production.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class MultiBlockGenericCrafter extends GenericCrafter implements MultiBlock{
  public MultiBlockGenericCrafter(String name){
    super(name);

    hasItems = true;
    hasLiquids = true;

    rotate = true;
    rotateDraw = true;
    quickRotate = false;
    allowDiagonal = false;
  }

  @Override
  public void setBars(){
    super.setBars();
    if(outputLiquid == null && (outputLiquids == null || outputLiquids.length == 0)){
      removeBar("liquid");
    }
  }

  @Annotations.ImplEntries
  public class MultiBlockCrafterBuild extends GenericCrafterBuild implements MultiBlockEntity{
  }
}
