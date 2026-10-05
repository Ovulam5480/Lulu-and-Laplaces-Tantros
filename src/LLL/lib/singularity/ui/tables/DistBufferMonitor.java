package LLL.lib.singularity.ui.tables;

import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.distribution.buffers.*;
import arc.struct.*;

public class DistBufferMonitor extends Monitor{
  private final Seq<BaseBuffer<?, ?, ?>> targetBuffer = new Seq<>();

  public DistBufferMonitor(int maxCount){

  }

  @Override
  public void startMonit(DistributeNetwork distNetwork){
    if(distNetwork.netStructValid()){
      for(BaseBuffer<?, ?, ?> buffer : targetBuffer){
        buffer.startCalculate(false);
      }
    }
  }

  @Override
  public void endMonit(DistributeNetwork distNetwork){
    if(distNetwork.netStructValid()){
      for(BaseBuffer<?, ?, ?> buffer : targetBuffer){
        buffer.startCalculate(true);
      }
    }
  }
}
