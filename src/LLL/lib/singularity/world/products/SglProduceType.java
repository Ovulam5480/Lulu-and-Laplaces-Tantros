package LLL.lib.singularity.world.products;

import universecore.world.producers.*;

public class SglProduceType<T extends BaseProduce<?>> extends ProduceType<T>{
  public SglProduceType(Class<T> type){
    super(type);
  }
}
