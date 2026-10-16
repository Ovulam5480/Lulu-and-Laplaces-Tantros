package lulu.world.neoplasm.effect;

import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
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
