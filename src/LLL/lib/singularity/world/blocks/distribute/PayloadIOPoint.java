package LLL.lib.singularity.world.blocks.distribute;

import LLL.lib.singularity.world.components.*;
import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import mindustry.ctype.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class PayloadIOPoint extends IOPoint{
  public PayloadIOPoint(String name){
    super(name);

    outputsPayload = acceptsPayload = true;
  }

  @Override
  public void setupRequestFact(){

  }

  @Annotations.ImplEntries
  public class PayloadIOPointBuild extends IOPointBuild implements PayloadBuildComp{

    @Override
    protected void transBack(){

    }

    @Override
    protected void resourcesSiphon(){

    }

    @Override
    protected void resourcesDump(){

    }

    @Override
    public boolean valid(DistMatrixUnitBuildComp unit, GridChildType type, Content content){
      return false;
    }

  }
}
