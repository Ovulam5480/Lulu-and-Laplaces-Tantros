package LLL.world.blocks.module;

import arc.graphics.g2d.*;
import arc.struct.*;
import universecore.annotations.*;

@SuppressWarnings("unused")
public interface TileSpriteBlockModule{
  TextureRegion getToSplit();

  @Annotations.BindField("splitRegions")
  default Seq<TextureRegion> getSplitRegions(){
    return null;
  }

  @Annotations.BindField("splitRegions")
  default void setSplitRegions(Seq<TextureRegion> splitRegions){
  }

  @Annotations.MethodEntry(entryMethod = "load")
  default void splitRegions(){
    setSplitRegions(new Seq<>());

    TextureRegion[][] regions = getToSplit().split(32, 32);
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 12; j++){
        getSplitRegions().add(regions[j][i]);
      }
    }
  }
}
