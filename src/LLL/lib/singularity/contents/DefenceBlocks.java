package LLL.lib.singularity.contents;

import LLL.lib.singularity.world.blocks.defence.*;
import LLL.lib.singularity.world.draw.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DefenceBlocks implements ContentList{
  /**
   * 相控雷达
   */
  public static Block phased_radar;

  @Override
  public void load(){
    phased_radar = new PhasedRadar("phased_radar"){{
      requirements(Category.effect, ItemStack.empty);

      newConsume();
      consume.power(1);

      draw = new DrawMulti(
        new DrawDefault(),
        new DrawDirSpliceBlock<PhasedRadarBuild>(){{
          simpleSpliceRegion = true;
          spliceBits = e -> e.spliceDirBit;
          layerRec = false;
        }},
        new DrawRegion("_rotator"){{
          layer = Layer.blockOver;
          rotateSpeed = 0.4f;
        }}
      );
    }};
  }
}
