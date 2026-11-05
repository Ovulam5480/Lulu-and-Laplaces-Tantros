package lulu.ctype;

import universecore.annotations.*;

public interface PowerChannelComp{
  @Annotations.BindField(value = "powerCapacity", initialize = "10000")
  default float powerCapacity(){
    return 0;
  }

  @Annotations.BindField("powerCapacity")
  default void powerCapacity(float capacity){
  }

  @Annotations.BindField("separatePowerCapacity")
  default boolean separatePowerCapacity(){
    return true;
  }

  @Annotations.BindField("separatePowerCapacity")
  default void separatePowerCapacity(boolean separate){
  }

  @Annotations.BindField("consumesPowerChannel")
  default boolean consumesPowerChannel(){
    return true;
  }

  @Annotations.BindField("consumesPowerChannel")
  default void consumesPowerChannel(boolean consumes){
  }

  @Annotations.BindField("producesPowerChannel")
  default boolean producesPowerChannel(){
    return true;
  }

  @Annotations.BindField("producesPowerChannel")
  default void producesPowerChannel(boolean produces){
  }

  @Annotations.BindField("bufferedPowerChannel")
  default boolean bufferedPowerChannel(){
    return true;
  }

  @Annotations.BindField("bufferedPowerChannel")
  default void bufferedPowerChannel(boolean buffered){
  }

  /** 该建筑电池可储存的电力类型集合，决定 PowerChannelModule 初始化哪些类型电池及其每类型容量（powerCapacity）。 */
  @Annotations.BindField("storablePowerTypes")
  default Power[] storablePowerTypes(){
    return null;
  }

  @Annotations.BindField("storablePowerTypes")
  default void storablePowerTypes(Power[] types){
  }
}
