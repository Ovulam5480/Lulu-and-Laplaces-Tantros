package LLL.world.neoplasm.units;

import LLL.ctype.*;
import LLL.world.blockcomp.*;
import LLL.world.buildComp.module.*;
import mindustry.ctype.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmMetamorphicCyst extends NeoplasmLarvaCyst implements TrophosomeTargetBlockComp{

  public NeoplasmMetamorphicCyst(String name){
    super(name);
    degenerateTime = 60 * 180;

    size = 5;
  }

  @Annotations.ImplEntries
  public class NeoplasmMetamorphicCystBuild extends NeoplasmLarvaCystBuild implements TrophosomeTargetBuildComp{
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
