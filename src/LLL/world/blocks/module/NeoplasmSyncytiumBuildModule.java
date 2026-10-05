package LLL.world.blocks.module;

import LLL.lib.multiblock.extend.*;
import LLL.world.neoplasm.effect.*;
import mindustry.gen.*;

public interface NeoplasmSyncytiumBuildModule extends MultiBlockEntity, NeoplasmBuildModule{
  @Override
  default void suicide(){
    for(Building building : linkEntities()){
      if(building instanceof NeoplasmBuildModule m){
        m.suicide();
      }
    }
    NeoplasmBuildModule.super.suicide();
  }

  @Override
  default void configureNeuron(Unit builder, Object value){
    NeoplasmBuildModule.super.configureNeuron(builder, value);

    if(value instanceof NeoplasmNeuron.NeoplasmNeuronBuild && !linkCreated()){
      createdMultiBlock();
    }

    neuron().cortex.placeIgnoreCore = true;
  }
}
