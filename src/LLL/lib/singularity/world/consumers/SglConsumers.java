package LLL.lib.singularity.world.consumers;

import arc.struct.*;
import universecore.world.consumers.*;

public class SglConsumers extends BaseConsumers{
  public SglConsumers(boolean optional){
    super(optional);
  }

  public BaseConsume<?> first(){
    for(ObjectMap.Entry<ConsumeType<?>, BaseConsume<?>> con : cons){
      if(con.value != null) return con.value;
    }
    return null;
  }
}
