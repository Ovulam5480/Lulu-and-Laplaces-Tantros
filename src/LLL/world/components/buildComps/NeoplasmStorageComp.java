package LLL.world.components.buildComps;

import LLL.world.modules.*;
import universecore.annotations.*;

public interface NeoplasmStorageComp{
  @Annotations.BindField("neoplasm")
  default void neoplasm(NeoplasmLiquidModule neoplasm){
  }

  @Annotations.BindField("neoplasm")
  default NeoplasmLiquidModule neoplasm(){
    return null;
  }
}
