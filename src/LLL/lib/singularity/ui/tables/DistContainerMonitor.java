package LLL.lib.singularity.ui.tables;

import LLL.lib.singularity.world.distribution.*;

public class DistContainerMonitor extends Monitor{
  public DistContainerMonitor(int maxCount){

  }

  @Override
  public void startMonit(DistributeNetwork distNetwork){
    distNetwork.grids.forEach(MatrixGrid::startStatContainer);
  }

  @Override
  public void endMonit(DistributeNetwork distNetwork){
    distNetwork.grids.forEach(MatrixGrid::endStatContainer);
  }
}
