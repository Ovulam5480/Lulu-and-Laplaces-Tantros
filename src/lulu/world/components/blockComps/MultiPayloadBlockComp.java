package lulu.world.components.blockComps;

import lulu.world.move.*;
import mindustry.world.*;
import universecore.annotations.*;

public interface MultiPayloadBlockComp extends PayloadBlockComp{
  @Annotations.BindField(value = "moveInMover", initialize = "new lulu.world.move.MoveDefault()")
  default MovePayload moveInMover(){
    return null;
  }

  @Annotations.BindField("moveInMover")
  default void moveInMover(MovePayload value){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initMultiPayloadBlock(){
    self().update = true;
    self().sync = true;
    self().acceptsPayload = true;
    self().rotate = true;
  }

  default Block self(){
    return (Block)this;
  }
}
