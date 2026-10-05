package LLL.lib.singularity.ui.tables;

import LLL.lib.singularity.world.distribution.*;
import arc.scene.ui.layout.*;

public abstract class Monitor extends Table{
  public abstract void startMonit(DistributeNetwork distNetwork);

  public abstract void endMonit(DistributeNetwork distNetwork);
}
