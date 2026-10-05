package LLL.world.blocks.module;

import arc.*;
import mindustry.game.*;
import universecore.annotations.*;

//经过存档后不会增生的多边长无建筑实体方块
@SuppressWarnings("unused")
public interface MultiSizeBlock{
  @Annotations.BindField("size")
  default void sizeSetter(int size){
  }

  @Annotations.BindField("size")
  default int sizeGetter(){
    return 0;
  }

  @Annotations.BindField("sized")
  default void sizedSetter(int sized){
  }

  @Annotations.BindField("sized")
  default int sizedGetter(){
    return 0;
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initMultiSize(){
    sizedSetter(sizeGetter());

    Events.on(EventType.WorldLoadBeginEvent.class, e -> {
      sizeSetter(1);
    });

    Events.on(EventType.WorldLoadEvent.class, e -> {
      sizeSetter(sizedGetter());
    });
  }
}
