package LLL.world.blocks.module;

import LLL.lib.singularity.world.components.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import universecore.annotations.*;

public interface TrophosomeTargetBuildModule extends PayloadBuildComp, Posc{
  default TrophosomeTargetBlockModule blockAsTarget(){
    return (TrophosomeTargetBlockModule)getBlock();
  }

  @Annotations.BindField(value = "requires", initialize = "new arc.struct.Seq<>()")
  default Seq<Unit> requires(){
    return null;
  }

  @Override
  default boolean acceptUnitPayload(Unit unit){
    return PayloadBuildComp.super.acceptUnitPayload(unit) && acceptTrophosome(unit.type);
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
