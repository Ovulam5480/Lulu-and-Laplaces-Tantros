package LLL.content.extensions;

import mindustry.world.meta.*;
import universecore.util.handler.*;

public class OvulamBlockGroup{
  private static final EnumHandler<BlockGroup> handler = new EnumHandler<>(BlockGroup.class);

  public static BlockGroup neoplastic = handler.addEnumItemTail("neoplastic", true);
}
