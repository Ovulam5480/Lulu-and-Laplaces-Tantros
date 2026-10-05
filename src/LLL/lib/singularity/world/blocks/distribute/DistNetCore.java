package LLL.lib.singularity.world.blocks.distribute;

import LLL.lib.singularity.world.blocks.distribute.netcomponents.*;
import LLL.lib.singularity.world.components.distnet.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.meta.*;
import LLL.lib.singularity.world.modules.*;
import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;
import universecore.util.*;

import static mindustry.Vars.*;

public class DistNetCore extends NetPluginComp implements DistMatrixUnitComp{
  public float requestEnergyCost = 0.1f;

  public DistNetCore(String name){
    super(name);
    topologyUse = 0;
    isNetLinker = true;

    computingPower = 8;
    topologyCapacity = 8;
    bufferSize = ObjectMap.of(
      DistBufferType.itemBuffer, 256,
      DistBufferType.liquidBuffer, 256
    );
  }

  @Override
  public void setStats(){
    super.setStats();
    stats.add(SglStat.computingPower, computingPower * 60, StatUnit.perSecond);
    stats.add(SglStat.topologyCapacity, topologyCapacity);
    stats.remove(SglStat.matrixEnergyUse);
    stats.add(SglStat.matrixEnergyUse,
      Strings.autoFixed(matrixEnergyUse * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perSecond") + " + "
        + Strings.autoFixed(requestEnergyCost * 60, 2) + SglStatUnit.matrixEnergy.localized() + Core.bundle.get("misc.perRequest") + Core.bundle.get("misc.perSecond")
    );
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

  @Annotations.ImplEntries
  public class DistNetCoreBuild extends NetPluginCompBuild implements DistNetworkCoreComp{
    DistCoreModule distCore;

    Seq<CoreNeighbourComponent.CoreNeighbourComponentBuild> proximityComps = new Seq<>();

    @Override
    public void onProximityUpdate(){
      super.onProximityUpdate();

      netLinked.removeAll(proximityComps);

      proximityComps.clear();
      for(Building building : proximity){
        if(building instanceof CoreNeighbourComponent.CoreNeighbourComponentBuild comp) proximityComps.add(comp);
      }

      netLinked.addAll(proximityComps);
    }

    @Override
    public void updateNetLinked(){
      super.updateNetLinked();

      netLinked.addAll(proximityComps);
    }

    @Override
    public void priority(int priority){
      matrixGrid().priority = priority;
      distributor.network.priorityModified(this);
    }

    @Override
    public void networkValided(){
      matrixGrid().clear();
    }

    @Override
    public BlockStatus status(){
      return distCore.requestTasks.isEmpty() ? BlockStatus.noInput : super.status();
    }

    @Override
    public Building create(Block block, Team team){
      distCore = new DistCoreModule(this);
      super.create(block, team);
      initBuffers();
      items = getBuffer(DistBufferType.itemBuffer).generateBindModule();
      liquids = getBuffer(DistBufferType.liquidBuffer).generateBindModule();

      priority(-65536);
      return this;
    }

    @Override
    public void drawSelect(){
      super.drawSelect();
      Lines.stroke(1f, Pal.accent);
      Cons<Building> outline = b -> {
        for(int i = 0; i < 4; i++){
          Point2 p = Geometry.d8edge[i];
          float offset = -Math.max(b.block.size - 1, 0) / 2f * tilesize;
          Draw.rect("block-select", b.x + offset * p.x, b.y + offset * p.y, i * 90);
        }
      };
      outline.get(this);
      proximityComps.each(outline);
    }

    @Override
    public float matrixEnergyConsume(){
      return matrixEnergyUse + requestEnergyCost * distCore.lastProcessed;
    }

    @Override
    public void ioPointConfigBackEntry(IOPointComp ioPoint){
      //no action
    }

    @Override
    public boolean tileValid(Tile tile){
      return true;
    }

    @Override
    public void drawValidRange(){
      //no action
    }
  }
}
