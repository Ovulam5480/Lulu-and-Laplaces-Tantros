package LLL.world.neoplasm.effect;

import LLL.world.blockcomp.*;
import LLL.world.buildComp.module.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmSyncytium extends NeoplasmGanglion implements NeoplasmSyncytiumBlockComp{
  public NeoplasmSyncytium(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmSyncytiumBuild extends NeoplasmGanglionBuild implements NeoplasmSyncytiumBuildModule{
  }
}
