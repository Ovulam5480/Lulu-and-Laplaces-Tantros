package lulu.world.components.blockComps;

import lulu.ctype.*;
import universecore.annotations.*;

public interface PowerChannelBlockComp extends PowerChannelComp, BlockComp{
  @Annotations.MethodEntry(entryMethod = "init")
  default void initPowerChannel(){
    self().hasPower = true;
  }
}
