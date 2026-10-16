package LLL.world.components.buildComps;

import LLL.lib.multiblock.extend.*;
import LLL.world.neoplasm.effect.*;
import mindustry.gen.*;

public interface NeoplasmSyncytiumBuildComp extends MultiBlockEntity, NeoplasmBuildComp{
  @Override
  default void suicide(){
    for(Building building : linkEntities()){
      if(building instanceof NeoplasmBuildComp m){
        m.suicide();
      }
    }
    NeoplasmBuildComp.super.suicide();
  }

  @Override
  default void configureNeuron(Unit builder, Object value){
    NeoplasmBuildComp.super.configureNeuron(builder, value);

    if(value instanceof NeoplasmNeuron.NeoplasmNeuronBuild && !linkCreated()){
      createdMultiBlock();
    }

    neuron().cortex.placeIgnoreCore = true;
  }
}
