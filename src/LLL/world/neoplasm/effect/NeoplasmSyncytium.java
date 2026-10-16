package LLL.world.neoplasm.effect;

import LLL.world.components.blockComps.*;
import LLL.world.components.buildComps.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmSyncytium extends NeoplasmGanglion implements NeoplasmSyncytiumBlockComp{
  public NeoplasmSyncytium(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmSyncytiumBuild extends NeoplasmGanglionBuild implements NeoplasmSyncytiumBuildComp{
  }
}
