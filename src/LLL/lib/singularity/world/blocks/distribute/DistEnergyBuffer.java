package LLL.lib.singularity.world.blocks.distribute;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.components.distnet.*;
import arc.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.ui.*;

public class DistEnergyBuffer extends DistEnergyEntry{

  public DistEnergyBuffer(String name){
    super(name);
  }

  @Override
  public void setBars(){
    super.setBars();
    addBar("energyBuffered", (DistEnergyBufferBuild e) -> new Bar(
      () -> Core.bundle.format("bar.energyBuffered",
        e.matrixEnergyBuffered >= 1000 ? UI.formatAmount((long)e.matrixEnergyBuffered) : Strings.autoFixed(e.matrixEnergyBuffered, 1),
        matrixEnergyCapacity >= 1000 ? UI.formatAmount((long)matrixEnergyCapacity) : Strings.autoFixed(matrixEnergyCapacity, 1)),
      () -> SglDrawConst.matrixNet,
      () -> e.matrixEnergyBuffered / matrixEnergyCapacity
    ));
  }

  public class DistEnergyBufferBuild extends DistEnergyEntryBuild{
    @Override
    public boolean linkable(DistElementBuildComp other){
      return false;
    }
  }
}
