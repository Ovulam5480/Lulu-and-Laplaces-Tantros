package LLL.lib.singularity.world.blocks.distribute.netcomponents;

import LLL.lib.singularity.world.blocks.distribute.*;
import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import universecore.util.*;

public class CoreNeighbourComponent extends DistNetBlock{
  public int topologyCapaity = 0;
  public int computingPower = 0;

  public ObjectMap<DistBufferType<?>, Integer> bufferSize = new ObjectMap<>();

  public CoreNeighbourComponent(String name){
    super(name);
    topologyUse = 0;
    isNetLinker = false;
  }

  @Override
  public void setStats(){
    super.setStats();
    if(topologyCapaity > 0) stats.add(SglStat.topologyCapacity, topologyCapaity);
    if(computingPower > 0) stats.add(SglStat.computingPower, computingPower * 60, StatUnit.perSecond);
    if(bufferSize.size > 0){
      stats.add(SglStat.bufferSize, t -> {
        t.defaults().left().fillX().padLeft(10);
        t.row();
        for(ObjectMap.Entry<DistBufferType<?>, Integer> entry : bufferSize){
          if(entry.value <= 0) continue;
          t.add(Core.bundle.get("content." + entry.key.targetType().name() + ".name") + ": " + NumberStrify.toByteFix(entry.value, 2));
          t.row();
        }
      });
    }
  }

  public class CoreNeighbourComponentBuild extends DistNetBuild{
    @Override
    public boolean linkable(DistElementBuildComp other){
      return false;
    }

    @Override
    public void updateNetLinked(){
      super.updateNetLinked();
      for(Building building : proximity){
        if(building instanceof DistNetworkCoreComp core){
          netLinked.add(core);
        }
      }
    }
  }
}
