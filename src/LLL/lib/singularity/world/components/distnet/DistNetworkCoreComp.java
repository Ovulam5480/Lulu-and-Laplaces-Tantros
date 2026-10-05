package LLL.lib.singularity.world.components.distnet;

import LLL.lib.singularity.world.modules.*;
import universecore.annotations.*;

public interface DistNetworkCoreComp extends DistMatrixUnitBuildComp{
  @Annotations.BindField("distCore")
  default DistCoreModule distCore(){
    return null;
  }

  @Annotations.BindField("distCore")
  default void distCore(DistCoreModule value){
  }

  default boolean updateState(){
    return false;
  }

  @Annotations.MethodEntry(entryMethod = "updateTile")
  default void updateDistNetwork(){
    distCore().update();
  }
}
