package LLL.lib.singularity.world.components.distnet;

import LLL.lib.singularity.world.blocks.distribute.matrixGrid.*;
import LLL.lib.singularity.world.distribution.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.world.*;
import universecore.annotations.*;

public interface IOPointBlockComp{
  @SuppressWarnings("rawtypes")
  @Annotations.BindField(value = "requestFactories", initialize = "new arc.struct.ObjectMap<>()")
  default ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler>> requestFactories(){
    return null;
  }

  @Annotations.BindField(value = "configTypes", initialize = "new arc.struct.OrderedSet<>()")
  default OrderedSet<GridChildType> configTypes(){
    return null;
  }

  @Annotations.BindField(value = "supportContentType", initialize = "new arc.struct.OrderedSet<>()")
  default OrderedSet<ContentType> supportContentType(){
    return null;
  }

  @SuppressWarnings("rawtypes")
  default void setFactory(GridChildType type, ContentType contType, RequestHandlers.RequestHandler factory){
    requestFactories().get(type, ObjectMap::new).put(contType, factory);
    configTypes().add(type);
    supportContentType().add(contType);
  }

  default Block getBlock(){
    return (Block)this;
  }
}
