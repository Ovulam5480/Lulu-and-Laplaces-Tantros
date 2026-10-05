package LLL.lib.singularity.world.blocks;

import LLL.lib.singularity.ui.fragments.notification.*;
import LLL.lib.singularity.world.blocks.distribute.*;
import LLL.lib.singularity.world.blocks.distribute.matrixGrid.MatrixGridBlock.MatrixGridBuild.*;
import LLL.lib.singularity.world.blocks.distribute.matrixGrid.MatrixGridCore.MatrixGridCoreBuild.*;
import arc.util.*;
import arc.util.pooling.*;
import universecore.util.*;

public class BytePackAssign{
  public static void assignAll(){
    try{
      DataPackable.assignType(TargetConfigure.typeID, param -> new TargetConfigure());
      DataPackable.assignType(LinkPair.typeID, param -> Pools.obtain(LinkPair.class, LinkPair::new));
      DataPackable.assignType(PosCfgPair.typeID, param -> Pools.obtain(PosCfgPair.class, PosCfgPair::new));

      Notification.Note.assign();
      Notification.Warning.assign();
    }catch(Throwable e){
      Log.err("some error happened, may fatal, details: ");
      Log.err(e);
    }
  }
}
