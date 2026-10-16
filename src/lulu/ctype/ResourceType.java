package lulu.ctype;

import lulu.content.resourceTypes.*;
import lulu.type.resourceStacks.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.type.*;

public enum ResourceType{
  item(Item.class),
  liquid(Liquid.class),
  power(Power.class),
  payload(UnlockableContent.class),
  resource(ResourceStackManager.Resource.class),
  floor(null),
  heat(null),
  neoplasm(null);

  public static final ResourceType[] all = values();

  public final @Nullable Class<?> clazz;
  public final int id;

  ResourceType(Class<?> clazz){
    this.clazz = clazz;
    id = ordinal();
  }
}
