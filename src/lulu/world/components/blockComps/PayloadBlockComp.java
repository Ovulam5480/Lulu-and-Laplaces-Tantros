package lulu.world.components.blockComps;

import universecore.annotations.*;

public interface PayloadBlockComp{
  @Annotations.BindField(value = "payloadAmountCapacity", initialize = "1")
  default int payloadAmountCapacity(){
    return 0;
  }

  @Annotations.BindField("payloadAmountCapacity")
  default void payloadAmountCapacity(int value){
  }

  @Annotations.BindField(value = "payloadAreaCapacity", initialize = "512f")
  default float payloadAreaCapacity(){
    return 0;
  }

  @Annotations.BindField("payloadAreaCapacity")
  default void payloadAreaCapacity(float value){
  }

  @Annotations.BindField(value = "acceptIgnoreAmount", initialize = "false")
  default boolean acceptIgnoreAmount(){
    return false;
  }

  @Annotations.BindField("acceptIgnoreAmount")
  default void acceptIgnoreAmount(boolean acceptIgnoreAmountCapacity){
  }

  @Annotations.BindField(value = "acceptIgnoreArea", initialize = "false")
  default boolean acceptIgnoreArea(){
    return false;
  }

  @Annotations.BindField("acceptIgnoreArea")
  default void acceptIgnoreArea(boolean acceptIgnoreCapacity){
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
