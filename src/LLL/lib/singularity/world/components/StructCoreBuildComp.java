package LLL.lib.singularity.world.components;

import universecore.world.blocks.chains.*;

public interface StructCoreBuildComp extends StructBuildComp{
  default StructCoreComp getStructCore(){
    return getBlock(StructCoreComp.class);
  }

  @Override
  default void chainsAdded(ChainsContainer old){
  }
}
