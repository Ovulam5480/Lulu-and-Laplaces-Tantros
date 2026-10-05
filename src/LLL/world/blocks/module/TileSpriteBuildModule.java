package LLL.world.blocks.module;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.*;
import mindustry.gen.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

@SuppressWarnings("unused")
public interface TileSpriteBuildModule extends BuildCompBase{
  int[] tileMapping = {
    39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
    38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
    39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
    38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
    3, 4, 3, 4, 15, 40, 15, 20, 3, 4, 3, 4, 15, 40, 15, 20,
    5, 28, 5, 28, 29, 10, 29, 23, 5, 28, 5, 28, 31, 11, 31, 32,
    3, 4, 3, 4, 15, 40, 15, 20, 3, 4, 3, 4, 15, 40, 15, 20,
    2, 30, 2, 30, 9, 46, 9, 22, 2, 30, 2, 30, 14, 44, 14, 6,
    39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
    38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
    39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
    38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
    3, 0, 3, 0, 15, 42, 15, 12, 3, 0, 3, 0, 15, 42, 15, 12,
    5, 8, 5, 8, 29, 35, 29, 33, 5, 8, 5, 8, 31, 34, 31, 7,
    3, 0, 3, 0, 15, 42, 15, 12, 3, 0, 3, 0, 15, 42, 15, 12,
    2, 1, 2, 1, 9, 45, 9, 19, 2, 1, 2, 1, 14, 18, 14, 13};

  int[] indexX = {1, 0, -1, -1, -1, 0, 1, 1};
  int[] indexY = {-1, -1, -1, 0, 1, 1, 1, 0};

  default TileSpriteBlockModule getModule(){
    return (TileSpriteBlockModule)getBlock();
  }

  @Annotations.BindField("spriteMask")
  default int getSpriteMask(){
    return 0;
  }

  @Annotations.BindField("spriteMask")
  default void setSpriteMask(int mask){
  }

  default TextureRegion getTileRegion(){
    int index = tileMapping[getSpriteMask()];

    return getModule().getSplitRegions().get(index);
  }

  @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  default void updateTileMask(){
    setSpriteMask(getMask());
  }

  default int getMask(){
    int mask = 0;

    for(int i = 0; i < 8; i++){
      mask |= Mathf.num(getTileLink(i));
      mask <<= 1;
    }
    mask >>= 1;

    return mask;
  }

  default boolean getTileLink(int index){
    int ix = indexX[index];
    int iy = indexY[index];

    Building build = Vars.world.build(getTile().x + ix, getTile().y + iy);

    return build != null && canLink(build);
  }

  default boolean canLink(Building other){
    return other.block == getBlock();
  }
}
