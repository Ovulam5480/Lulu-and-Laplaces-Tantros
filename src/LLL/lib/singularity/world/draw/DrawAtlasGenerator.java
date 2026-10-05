package LLL.lib.singularity.world.draw;

import mindustry.graphics.*;
import mindustry.world.*;

public interface DrawAtlasGenerator{
  void generateAtlas(Block block, MultiPacker packer);

  void postLoad(Block block);
}
