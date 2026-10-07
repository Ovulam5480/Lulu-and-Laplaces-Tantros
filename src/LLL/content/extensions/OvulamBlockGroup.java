package LLL.content.extensions;

import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class OvulamBlockGroup{
  private static final EnumAccessor1<BlockGroup, Boolean> handler = Enums.accessEnum(BlockGroup.class, boolean.class);

  public static BlockGroup neoplastic = handler.appendEnumInstance("neoplastic", true, false);
}
