package LLL.world.neoplasm.units;

import LLL.packet.resources.*;
import LLL.world.blocks.module.*;
import LLL.world.blocks.module.block.*;
import mindustry.ctype.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmMetamorphicCyst extends NeoplasmLarvaCyst implements TrophosomeTargetBlockModule{

  public NeoplasmMetamorphicCyst(String name){
    super(name);
    degenerateTime = 60 * 180;

    size = 5;
  }

  @Annotations.ImplEntries
  public class NeoplasmMetamorphicCystBuild extends NeoplasmLarvaCystBuild implements TrophosomeTargetBuildModule{
    @Override
    public void suicide(){
      super.suicide();
      for(Payload payload : payloads().iterate()){
        payload.dump();
      }
    }

    @Override
    public boolean canAcceptPayload(UnlockableContent type){
      return filter().filter(this, ResourceType.payload, type, true);
    }

    @Override
    public int getPayloadCount(UnlockableContent type){
      return payloads().amountOf(type) + (inputting() != null && inputting().content() == type ? 1 : 0);
    }

    @Override
    public int getPayloadCapacity(UnlockableContent type){
      return accepts.get(type, 0) * 2;
    }
  }
}
