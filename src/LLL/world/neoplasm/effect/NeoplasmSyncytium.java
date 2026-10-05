package LLL.world.neoplasm.effect;

import LLL.world.blocks.module.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmSyncytium extends NeoplasmGanglion implements NeoplasmSyncytiumBlockModule{
  public NeoplasmSyncytium(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmSyncytiumBuild extends NeoplasmGanglionBuild implements NeoplasmSyncytiumBuildModule{
  }
}
