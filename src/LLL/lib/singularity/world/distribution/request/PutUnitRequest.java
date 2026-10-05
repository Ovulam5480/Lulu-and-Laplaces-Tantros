package LLL.lib.singularity.world.distribution.request;

import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.distribution.buffers.*;

public class PutUnitRequest extends DistRequestBase{
  UnitBuffer source;

  public PutUnitRequest(DistElementBuildComp sender){
    super(sender);
  }

  @Override
  public int priority(){
    return 128;
  }

  @Override
  public void init(DistributeNetwork target){
    super.init(target);
    source = target.getCore().getBuffer(DistBufferType.unitBuffer);
  }

  @Override
  protected boolean preHandleTask(){
    return false;
  }

  @Override
  protected boolean handleTask(){
    return false;
  }

  @Override
  protected boolean afterHandleTask(){
    return false;
  }
}
