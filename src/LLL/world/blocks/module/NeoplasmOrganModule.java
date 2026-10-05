package LLL.world.blocks.module;

import LLL.lib.singularity.world.consumers.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

//需要对物品进行消耗的工厂模块, 休眠超过一定时间则凋亡为血管
public interface NeoplasmOrganModule extends NeoplasmBuildModule, FactoryBuildComp{
  @Annotations.BindField("degenerate")
  default void setDegenerate(float degenerate){
  }

  @Annotations.BindField("degenerate")
  default float degenerate(){
    return 0;
  }

  @Annotations.MethodEntry(entryMethod = "update")
  default void checkActivity(){
    if(neuron() != null){
      if(warmup() > 0.01f){
        setDegenerate(0);
      }else{
        float degenerate = degenerate() + Time.delta;

        if(degenerate > getDegenerateTime()){
          suicide();
        }else{
          setDegenerate(degenerate);
        }
      }
    }
  }

  default float getDegenerateTime(){
    return 600;
  }

  @Override
  default void updateItemDump(Building self, float delta){
    float timer = dumpTimer() + delta;
    if(timer > 1f && items().any()){
      Item todump = null;

      ObjectSet<Content> filter = getConsumerBlock().filter().allFilter.get(SglConsumeType.item);

      for(Item item : Vars.content.items()){
        if(items().has(item, filter != null && filter.contains(item) ? getBlock().itemCapacity - 3 : 1)){
          todump = item;
          break;
        }
      }

      if(todump != null){
        if(neuron() == null){
          self.dump(todump);
        }else{
          Building target = dumpTarget();

          if(target != null){
            if(target.acceptItem(self, todump)){
              target.handleItem(self, todump);
              items().remove(todump, 1);
            }
          }else{
            cortex().getItemDumpTarget(this);
          }
        }
      }
      timer = 0f;
    }
    setDumpTimer(timer);
  }

  @Override
  default void readSerialization(Reads read, byte revision){
    NeoplasmBuildModule.super.readSerialization(read, revision);
    setDegenerate(read.f());
  }

  @Override
  default void writeSerialization(Writes write){
    NeoplasmBuildModule.super.writeSerialization(write);
    write.f(degenerate());
  }
}
