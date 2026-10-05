package LLL.world.blocks.working;

import mindustry.*;
import universecore.annotations.*;

//边长超过16的方块
public interface LargeSizeBlock{

  @Annotations.MethodEntry(entryMethod = "init")
  default void initLargeSize(){
    int max = Vars.maxBlockSize;
  }
}
