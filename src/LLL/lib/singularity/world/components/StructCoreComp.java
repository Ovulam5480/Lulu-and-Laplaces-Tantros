package LLL.lib.singularity.world.components;

import LLL.lib.singularity.world.blocks.structure.*;
import arc.struct.*;
import universecore.annotations.*;

public interface StructCoreComp extends StructBlockComp{
  @Annotations.BindField(value = "structures", initialize = "new arc.struct.Seq<>()")
  default Seq<BlockStructure> structures(){
    return null;
  }

  default void addStruct(BlockStructure structure){
    if(structures().contains(structure)) return;
    structures().add(structure);
  }
}
