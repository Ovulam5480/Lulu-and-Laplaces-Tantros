package LLL.lib.singularity.world.components.distnet;

import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.util.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

public interface DistElementBlockComp{
  @Annotations.BindField(value = "topologyUse", initialize = "1")
  default int topologyUse(){
    return 0;
  }

  @Annotations.BindField("matrixEnergyUse")
  default float matrixEnergyUse(){
    return 0;
  }

  @Annotations.BindField("matrixEnergyCapacity")
  default float matrixEnergyCapacity(){
    return 0;
  }

  @Annotations.BindField("isNetLinker")
  default boolean isNetLinker(){
    return false;
  }

  @Annotations.MethodEntry(entryMethod = "setStats", context = "stats -> stats")
  default void setDistNetStats(Stats stats){
    if(matrixEnergyUse() > 0) stats.add(SglStat.matrixEnergyUse,
      Strings.autoFixed(matrixEnergyUse() * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond"));
    if(matrixEnergyCapacity() > 0)
      stats.add(SglStat.matrixEnergyCapacity, matrixEnergyCapacity(), SglStatUnit.matrixEnergy);
    if(topologyUse() > 0) stats.add(SglStat.topologyUse, topologyUse());
  }
}
