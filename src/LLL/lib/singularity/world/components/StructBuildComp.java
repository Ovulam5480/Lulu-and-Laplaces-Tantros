package LLL.lib.singularity.world.components;

import universecore.annotations.*;
import universecore.components.blockcomp.*;

public interface StructBuildComp extends ChainsBuildComp{
  @Annotations.BindField("structCore")
  default StructCoreBuildComp core(){
    return null;
  }

  @Annotations.BindField("structCore")
  default void core(StructCoreBuildComp core){
  }
}
