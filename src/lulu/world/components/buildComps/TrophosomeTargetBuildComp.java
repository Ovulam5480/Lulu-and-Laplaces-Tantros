package lulu.world.components.buildComps;

import arc.struct.*;
import lulu.world.components.blockComps.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

public interface TrophosomeTargetBuildComp extends BuildCompBase, Posc{
  default TrophosomeTargetBlockComp blockAsTarget(){
    return (TrophosomeTargetBlockComp)getBlock();
  }

  @Annotations.BindField(value = "requires", initialize = "new arc.struct.Seq<>()")
  default Seq<Unit> requires(){
    return null;
  }

  @Annotations.BindField("requires")
  default void requires(Seq<Unit> value){
  }

  @Annotations.MethodEntry(entryMethod = "acceptPayload", override = true, paramTypes = {"mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"})
  default boolean acceptPayload(Building source, Payload payload){
    return acceptTrophosome(payload.content());
  }

  boolean canAcceptPayload(UnlockableContent type);

  int getPayloadCount(UnlockableContent type);

  int getPayloadCapacity(UnlockableContent type);

  default boolean acceptTrophosome(UnlockableContent type){
    if(!canAcceptPayload(type)){
      return false;
    }

    return getPayloadCount(type) + requires().count(u -> u.type == type) < getPayloadCapacity(type);
  }

  default void handleTrophosomeRequire(Unit unit){
    requires().add(unit);
  }

  default void removeTrophosomeRequire(Unit unit){
    requires().remove(unit);
  }
}
