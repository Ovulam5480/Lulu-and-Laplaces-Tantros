package lulu.world.components.blockComps;

import lulu.entities.gen.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

public interface PayloadCrafterBlockComp<T extends Building & ProducerBuildc<T>> extends ProducerBlockComp<T>{
  @Annotations.BindField(value = "itemCapacityMulti", initialize = "2")
  default float itemCapacityMulti(){
    return 0;
  }

  @Annotations.BindField("itemCapacityMulti")
  default void itemCapacityMulti(float value){
  }

  @Annotations.BindField(value = "payloadCapacity", initialize = "1")
  default int payloadCapacity(){
    return 0;
  }

  @Annotations.BindField("payloadCapacity")
  default void payloadCapacity(int value){
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

  @Annotations.MethodEntry(entryMethod = "init")
  default void initPayloadCrafter(){
    self().outputFacing = true;
    self().outputsPayload = true;
    self().rotate = true;
    self().group = BlockGroup.payloads;
    self().envEnabled |= Env.space | Env.underwater;
  }
}
