package lulu.world.components.blockComps;

import lulu.content.extensions.*;
import arc.struct.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

public interface TrophosomeTargetBlockComp{
  @Annotations.BindField(value = "priority")
  default int priority(){
    return 0;
  }

  @Annotations.BindField("priority")
  default void priority(int value){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initTargetBlock(){
    Block block = (Block)this;
    block.acceptsPayload = true;

    Seq<BlockFlag> flags = Seq.with(block.flags.array);
    flags.add(OvulamBlockFlags.trophosomeTarget);

    block.flags = EnumSet.of(flags.toArray());
  }
}
