package LLL.lib.singularity.world.modules;

import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.distribution.request.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.world.modules.*;

public class DistributeModule extends BlockModule{
  public final DistElementBuildComp entity;
  public final IntSeq distNetLinks = new IntSeq();

  public DistRequestBase lastAssign;
  public DistributeNetwork network;

  public DistributeModule(DistElementBuildComp entity){
    this.entity = entity;
  }

  public void setNet(){
    setNet(null);
  }

  public void setNet(DistributeNetwork net){
    if(net == null){
      new DistributeNetwork().add(entity);
    }else network = net;
    if(network.netStructValid()) entity.networkValided();
  }

  public void assign(DistRequestBase request){
    assign(request, true);
  }

  public void assign(DistRequestBase request, boolean init){
    assign(request, init ? network : null);
  }

  public void assign(DistRequestBase request, DistributeNetwork initializer){
    if(network.netStructValid()){
      DistCoreModule core = core().distCore();
      core.receive(request);
      if(initializer != null) request.init(initializer);
      lastAssign = request;
    }
  }

  public DistNetworkCoreComp core(){
    return network.getCore();
  }

  @Override
  public void read(Reads read){

  }

  @Override
  public void write(Writes write){

  }
}
