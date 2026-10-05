package LLL.lib.singularity.world.meta;

import mindustry.world.meta.*;
import universecore.util.handler.*;

public class SglBlockGroup{
  private static final EnumHandler<BlockGroup> handler = new EnumHandler<>(BlockGroup.class);

  public static BlockGroup nuclear = handler.addEnumItemTail("nuclear", true);
}
