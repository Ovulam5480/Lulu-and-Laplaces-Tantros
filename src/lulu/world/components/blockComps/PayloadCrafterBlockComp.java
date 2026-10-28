package lulu.world.components.blockComps;

import arc.math.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

public interface PayloadCrafterBlockComp<T extends Building & ProducerBuildc<T>> extends ProducerBlockComp<T>, PayloadBlockComp{
  @Annotations.BindField(value = "itemCapacityMulti", initialize = "2")
  default float itemCapacityMulti(){
    return 0;
  }

  @Annotations.BindField("itemCapacityMulti")
  default void itemCapacityMulti(float value){
  }

  @Annotations.BindField(value = "payloadCapacityMulti", initialize = "2")
  default float payloadCapacityMulti(){
    return 0;
  }

  @Annotations.BindField("payloadCapacityMulti")
  default void payloadCapacityMulti(float value){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initPayloadCrafter(){
    self().outputFacing = true;
    self().group = BlockGroup.payloads;
    self().envEnabled |= Env.space | Env.underwater;

    if(consume() != null && consume().get(ResourceType.payload) != null){
      self().acceptsPayload = true;

      consume().forAll(c -> {
        if(c instanceof ConsumePayload<T> cp){
          int total = 0;
          for(PayloadStack ps : cp.payloads){
            total += ps.amount;
          }
          payloadCapacity(Math.max(Mathf.floor(total * payloadCapacityMulti()), payloadCapacity()));
        }
      });
    }

    if(produce() != null && produce().get(ResourceType.payload) != null){
      self().outputsPayload = true;
      self().rotate = true;

      produce().forAll(c -> {
        if(c instanceof ProducePayload<T> cp){
          int total = 0;
          for(PayloadStack ps : cp.payloads){
            total += ps.amount;
          }
          payloadCapacity(Math.max(Mathf.floor(total + payloadCapacityMulti()), payloadCapacity()));
        }
      });
    }
  }
}
