package lulu.world.components.blockComps;

import universecore.annotations.*;

public interface PayloadBlockComp{
  @Annotations.BindField(value = "payloadCapacity", initialize = "1")
  default int payloadCapacity(){
    return 0;
  }

  @Annotations.BindField("payloadCapacity")
  default void payloadCapacity(int value){
  }

  @Annotations.BindField(value = "acceptIgnoreCapacity", initialize = "false")
  default boolean acceptIgnoreCapacity(){
    return false;
  }

  @Annotations.BindField("acceptIgnoreCapacity")
  default void acceptIgnoreCapacity(boolean acceptIgnoreCapacity){
  }


  @Annotations.BindField(value = "payloadSpeed", initialize = "0.7f")
  default float payloadSpeed(){
    return 0;
  }

  @Annotations.BindField("payloadSpeed")
  default void payloadSpeed(float value){
  }

  @Annotations.BindField(value = "payloadRotateSpeed", initialize = "5f")
  default float payloadRotateSpeed(){
    return 0;
  }

  @Annotations.BindField("payloadRotateSpeed")
  default void payloadRotateSpeed(float value){
  }
}
