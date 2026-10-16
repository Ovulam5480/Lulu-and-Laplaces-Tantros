package lulu.world.modules;

import lulu.world.blocks.*;
import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
import arc.func.*;
import arc.util.io.*;
import mindustry.world.modules.*;

import java.util.*;

public class ChainsModule extends BlockModule implements ExtraVariableComp{
  public ChainsBuildComp entity;
  public ChainsContainer container;

  public ChainsModule(ChainsBuildComp entity){
    this.entity = entity;
  }

  public ChainsContainer newContainer(){
    ChainsContainer old = entity.chains().container;

    entity.chains().container = new ChainsContainer();
    entity.containerCreated(old);

    entity.chains().container.add(entity);

    return entity.chains().container;
  }

  public void each(Cons<ChainsBuildComp> cons){
    for(ChainsBuildComp other : container.all){
      cons.get(other);
    }
  }

  @Override
  public Map<String, Object> extra(){
    return container.extra();
  }

  /**
   * @see ChainsModule#setVar(String, Object)
   * @deprecated 请使用setVar(String, Object)
   */
  @Deprecated
  public void putVar(String key, Object obj){
    container.setVar(key, obj);
  }

  @Override
  public void write(Writes write){
  }
}
