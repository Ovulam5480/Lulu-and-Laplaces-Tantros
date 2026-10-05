package LLL.lib.singularity.world.components.distnet;

import universecore.annotations.*;

public interface DistMatrixUnitComp{
  @Annotations.BindField("bufferCapacity")
  default int bufferCapacity(){
    return 0;
  }
}
