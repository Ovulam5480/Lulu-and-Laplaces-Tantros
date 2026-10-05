package LLL.lib.singularity.world.distribution.request;

import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.distribution.buffers.*;
import arc.struct.*;
import mindustry.type.*;

public class ReadUnitRequest extends DistRequestBase{
  protected final UnitBuffer destination;
  protected UnitBuffer source;

  protected final Seq<PayloadStack> reqUnits;
  protected boolean all;

  public ReadUnitRequest(DistElementBuildComp sender, UnitBuffer buffer){
    super(sender);
    all = true;
    destination = buffer;
    reqUnits = null;
  }

  public ReadUnitRequest(DistElementBuildComp sender, UnitBuffer buffer, Seq<PayloadStack> require){
    super(sender);
    all = false;
    destination = buffer;
    reqUnits = require;
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
    boolean res = false;

    if(all){

    }else{
      for(PayloadStack stack : reqUnits){
        UnitBuffer.UnitPacket packet = source.get(stack.item.id);

        if(packet == null || packet.isEmpty()) continue;
        int c = Math.min(stack.amount, source.getAmount((UnitType)stack.item));
        c = Math.min(c, destination.remainingCapacity());

        for(int i = 0; i < c; i++){
          destination.put(source.take());
        }

        res = true;
      }
    }

    return res;
  }

  @Override
  protected boolean afterHandleTask(){
    return false;
  }
}
