package LLL.world.components.blockComps;

import LLL.entities.gen.*;
import LLL.world.components.buildComps.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import universecore.annotations.*;

public interface NeoplasmOrganBlockComp<T extends Building & ProducerBuildc<T>> extends ProducerBlockComp<T>{
  @Annotations.BindField(value = "targetScale", initialize = "0.7f")
  default float targetScale(){
    return 0;
  }

  @Annotations.BindField(value = "targetScale")
  default void targetScale(float targetScale){
  }

  @Annotations.BindField(value = "degenerateTime", initialize = "600f")
  default float degenerateTime(){
    return 600f;
  }

  @Annotations.BindField(value = "degenerateTime")
  default void degenerateTime(float degenerateTime){
  }

  @Annotations.MethodEntry(entryMethod = "init", insert = Annotations.InsertPosition.END)
  default void initNeoplasmOrganBlock(){
    self().size = 3;
    self().itemCapacity = 20;
    self().liquidCapacity = 50;
    autoSelect(true);
    canSelect(false);
  }

  @Annotations.MethodEntry(entryMethod = "setBars")
  default void setBarsNeoplasmOrganBlock(){
    self().addBar("sleepTime", (Building b) -> new Bar("bar.neoplasm", Pal.accent, () -> ((NeoplasmOrganComp<T>)b).degenerate() / degenerateTime()));
  }
}
