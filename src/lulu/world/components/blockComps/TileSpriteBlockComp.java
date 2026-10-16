package lulu.world.components.blockComps;

import arc.graphics.g2d.*;
import arc.struct.*;
import universecore.annotations.*;

@SuppressWarnings("unused")
public interface TileSpriteBlockComp{
  TextureRegion getToSplit();

  @Annotations.BindField("splitRegions")
  default Seq<TextureRegion> getSplitRegions(){
    return null;
  }

  @Annotations.BindField("splitRegions")
  default void splitRegions(Seq<TextureRegion> splitRegions){
  }

  @Annotations.MethodEntry(entryMethod = "load")
  default void splitRegions(){
    splitRegions(new Seq<>());

    TextureRegion[][] regions = getToSplit().split(32, 32);
    for(int i = 0; i < 4; i++){
      for(int j = 0; j < 12; j++){
        getSplitRegions().add(regions[j][i]);
      }
    }
  }
}
