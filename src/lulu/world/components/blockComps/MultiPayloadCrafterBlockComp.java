package lulu.world.components.blockComps;

import lulu.entities.gen.*;
import lulu.world.move.*;
import mindustry.gen.*;
import mindustry.world.*;
import universecore.annotations.*;

public interface MultiPayloadCrafterBlockComp<T extends Building & ProducerBuildc<T>> extends MultiPayloadBlockComp, ProducerBlockComp<T>{
  //改变配方是否清除自身携带的载荷
  @Annotations.BindField(value = "changeClear", initialize = "false")
  default boolean changeClear(){
    return false;
  }

  @Annotations.BindField("changeClear")
  default void changeClear(boolean value){
  }

  //待加工区的move, 只有一种距离, 这意味着, 建议配方用相同宽度的材料载荷
  @Annotations.BindField(value = "moveCache", initialize = "new lulu.world.move.MoveSize()")
  default MovePayload moveCache(){
    return null;
  }

  @Annotations.BindField("moveCache")
  default void moveCache(MovePayload value){
  }

  //输出区的move, 只有一种距离, 这意味着, 建议配方用相同宽度的产物载荷
  @Annotations.BindField(value = "moveProduct", initialize = "new lulu.world.move.MoveOut()")
  default MovePayload moveProduct(){
    return null;
  }

  @Annotations.BindField("moveProduct")
  default void moveProduct(MovePayload value){
  }

  @Annotations.BindField(value = "outMoverCapacityMulti", initialize = "2")
  default int outMoverCapacityMulti(){
    return 0;
  }

  @Annotations.BindField("outMoverCapacityMulti")
  default void outMoverCapacityMulti(int value){
  }

  @Override
  default Block self(){
    return (Block)this;
  }
}
