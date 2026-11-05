package lulu.world.components.buildComps;

import lulu.ctype.*;
import lulu.world.components.blockComps.*;
import lulu.world.modules.*;

public interface PowerChannelBuildComp extends PowerChannelEntityComp, BuildCompBase{
  default PowerChannelBlockComp getPowerChannelBlock(){
    return (PowerChannelBlockComp)getBlock();
  }

  @Override
  default PowerChannelComp getPowerChannelComp(){
    return getPowerChannelBlock();
  }

  @Override
  default PowerChannelModule power(){
    return (PowerChannelModule)getBuilding().power;
  }

  default void createPowerChannelBuild(){
    getBuilding().power = new PowerChannelModule();
    getBuilding().power.graph.add(getBuilding());
  }

  @Override
  default float getPowerStored(int id){
    return power().get(id);
  }

  @Override
  default float getPowerCapacity(int id){
    PowerChannelBlockComp c = getPowerChannelBlock();

    if(c.separatePowerCapacity()){
      return c.powerCapacity() - power().get(id);
    }else {
      return c.powerCapacity() - power().sum();
    }
  }

  @Override
  default float getTotalPowerCapacity(int id){
    PowerChannelBlockComp c = getPowerChannelBlock();

    if(c.separatePowerCapacity()){
      return c.powerCapacity();
    }else {
      return c.powerCapacity() - power().sum() + power().get(id);
    }
  }

  @Override
  default float getDelta(){
    return getBuilding().edelta();
  }

  @Override
  default boolean shouldConsumePower(){
    return getPowerChannelComp().consumesPowerChannel();
  }

  @Override
  default boolean shouldProducePower(){
    return getPowerChannelComp().producesPowerChannel();
  }
}
