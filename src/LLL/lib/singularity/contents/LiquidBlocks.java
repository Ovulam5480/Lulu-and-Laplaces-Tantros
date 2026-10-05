package LLL.lib.singularity.contents;

import LLL.lib.singularity.world.blocks.liquid.*;
import arc.func.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;

public class LiquidBlocks implements ContentList{
  /**
   * 集束管道
   */
  public static Block cluster_conduit,
  /**
   * 管道铆
   */
  conduit_riveting,
  /**
   * 过滤阀
   */
  filter_valve,
  /**
   * 液体提取器
   */
  liquid_unloader;

  @Override
  public void load(){
    cluster_conduit = new ClusterConduit("cluster_conduit"){{
      requirements(Category.liquid, ItemStack.empty);
      liquidCapacity = 10;
      liquidPressure = 1.05f;
      health = 360;
    }};

    conduit_riveting = new FakeBlock(new ConduitRiveting("conduit_riveting"){{
      requirements(Category.liquid, ItemStack.empty);
      liquidCapacity = 10;
      health = 300;
    }}, (tile, team, rotation) -> tile.build instanceof ClusterConduit.ClusterConduitBuild b && b.rotation == rotation);

    filter_valve = new FakeBlock(new ClusterValve("filter_valve"){{
      requirements(Category.liquid, ItemStack.empty);
      liquidCapacity = 10;
      health = 300;
    }}, (tile, team, rotation) -> tile.build instanceof ClusterConduit.ClusterConduitBuild b && b.rotation == rotation);

    liquid_unloader = new LiquidUnloader("liquid_unloader"){{
      requirements(Category.liquid, ItemStack.empty);
      size = 1;
    }};
  }

  static class FakeBlock extends universecore.world.blocks.FakeBlock{
    public FakeBlock(Block maskedBlock, Boolf3<Tile, Team, Integer> placeValid){
      super(maskedBlock, placeValid);
      maskedBlock.alwaysUnlocked = true;
    }
  }
}
