package LLL.lib.singularity.world.consumers;

import LLL.world.uncRecipes.*;
import mindustry.ctype.*;
import universecore.world.consumers.*;

@SuppressWarnings("unchecked")
public class SglConsumeType<T extends BaseConsume<?>> extends ConsumeType<T>{
  public SglConsumeType(Class<T> type, ContentType cType){
    super(type, cType);
  }

  public static final ConsumeType<SglConsumeFloor<?>> floor = (ConsumeType<SglConsumeFloor<?>>)add(SglConsumeFloor.class, null);
  public static final ConsumeType<ConsumeResource<?>> resource = (ConsumeType<ConsumeResource<?>>)add(ConsumeResource.class, null);
}
