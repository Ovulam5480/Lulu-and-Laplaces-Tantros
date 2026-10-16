package LLL.world.components.buildComps;

import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.world.modules.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.annotations.*;

//需要对物品进行消耗的工厂模块, 休眠超过一定时间则凋亡为血管
public interface NeoplasmOrganComp<T extends Building & ProducerBuildc<T>> extends NeoplasmBuildComp, ProducerBuildc<T>{
  @Annotations.BindField("degenerate")
  default void degenerate(float degenerate){
  }

  @Annotations.BindField("degenerate")
  default float degenerate(){
    return 0;
  }

  @Override
  SglLiquidModule liquids();

  @Annotations.MethodEntry(entryMethod = "update")
  default void checkActivity(){
    if(neuron() != null){
      if(warmup() > 0.01f){
        degenerate(0);
      }else{
        float degenerate = degenerate() + Time.delta;

        if(degenerate > getDegenerateTime()){
          suicide();
        }else{
          degenerate(degenerate);
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
    if(timer > 1f && buildItems().any()){
      Item todump = null;

      ObjectSet<Content> filter = getConsumerBlock().filter().allFilter.get(ResourceType.item);

      for(Item item : Vars.content.items()){
        if(buildItems().has(item, filter != null && filter.contains(item) ? getBlock().itemCapacity - 3 : 1)){
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
              buildItems().remove(todump, 1);
            }
          }else{
            cortex().getItemDumpTarget(this);
          }
        }
      }
      timer = 0f;
    }
    dumpTimer(timer);
  }

  @Override
  default void readSerialization(Reads read, byte revision){
    NeoplasmBuildComp.super.readSerialization(read, revision);
    degenerate(read.f());
  }

  @Override
  default void writeSerialization(Writes write){
    NeoplasmBuildComp.super.writeSerialization(write);
    write.f(degenerate());
  }
}
