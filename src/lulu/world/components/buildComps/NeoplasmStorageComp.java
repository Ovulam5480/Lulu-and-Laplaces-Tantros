package lulu.world.components.buildComps;

import lulu.world.modules.*;
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
