package LLL.lib.singularity.world.blocks.distribute;

import LLL.lib.singularity.graphic.*;
import arc.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class DistEnergyManager extends DistNetBlock{
  public DistEnergyManager(String name){
    super(name);

    matrixEnergyCapacity = 2048;
    isNetLinker = true;
  }

  @Override
  public void setBars(){
    super.setBars();
    addBar("energyBuffered", (DistEnergyManagerBuild e) -> new Bar(
      () -> Core.bundle.format("bar.energyBuffered",
        e.matrixEnergyBuffered >= 1000 ? UI.formatAmount((long)e.matrixEnergyBuffered) : Strings.autoFixed(e.matrixEnergyBuffered, 1),
        matrixEnergyCapacity >= 1000 ? UI.formatAmount((long)matrixEnergyCapacity) : Strings.autoFixed(matrixEnergyCapacity, 1)),
      () -> SglDrawConst.matrixNet,
      () -> e.matrixEnergyBuffered / matrixEnergyCapacity
    ));
  }

  public class DistEnergyManagerBuild extends DistNetBuild{
    @Override
    public void updateNetLinked(){
      super.updateNetLinked();
      for(Building building : proximity){
        if(building instanceof DistEnergyEntry.DistEnergyEntryBuild entry){
          netLinked.add(entry);
        }
      }
    }

    @Override
    public void onProximityUpdate(){
      super.onProximityUpdate();

      updateNetLinked();
    }
  }
}
