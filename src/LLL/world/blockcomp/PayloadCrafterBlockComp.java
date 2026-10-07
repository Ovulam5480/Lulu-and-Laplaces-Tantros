package LLL.world.blockcomp;

import mindustry.world.meta.*;
import universecore.annotations.*;

public interface PayloadCrafterBlockComp extends ProducerBlockComp{
  @Annotations.BindField(value = "itemCapacityMulti", initialize = "2")
  default float itemCapacityMulti(){
    return 0;
  }

  @Annotations.BindField(value = "payloadCapacity", initialize = "1")
  default int payloadCapacity(){
    return 0;
  }

  @Annotations.BindField(value = "payloadSpeed", initialize = "0.7f")
  default float payloadSpeed(){
    return 0;
  }

  @Annotations.BindField(value = "payloadRotateSpeed", initialize = "5f")
  default float payloadRotateSpeed(){
    return 0;
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
