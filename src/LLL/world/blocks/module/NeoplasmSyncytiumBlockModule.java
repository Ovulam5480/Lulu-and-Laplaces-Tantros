package LLL.world.blocks.module;

import LLL.lib.multiblock.*;
import LLL.lib.multiblock.extend.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

public interface NeoplasmSyncytiumBlockModule extends MultiBlock, NeoplasmBlockModule{
  default LinkBlock getLinkBlockType(Block block, int linkSize){
    return MultiBlockLib.linkNeoplasm[linkSize - 1];
  }

  @Override
  default Seq<Building> setLinkBuild(Building building, Block block, Tile tile, Team team, int size, int rotation){
    Seq<Building> bs = MultiBlock.super.setLinkBuild(building, block, tile, team, size, rotation);

    bs.each(Building::updateProximity);

    return bs;
  }

  @Override
  default void initMultiBlock(){
    setLinkValues(initLinkValues());
    MultiBlock.super.initMultiBlock();
    initSyncytium();
  }

  default IntSeq initLinkValues(){
    IntSeq seq = new IntSeq();
    Point2 center = null;

    for(Point2 p2 : Geometry.d4){
      if(center == null){
        center = new Point2(6 * p2.x, 6 * p2.y);
      }else{
        seq.add(6 * p2.x - center.x, 6 * p2.y - center.y, 6);
      }
    }
    for(Point2 p2 : Geometry.d8edge){
      seq.add(5 * p2.x - center.x, 5 * p2.y - center.y, 4);
      seq.add(8 * p2.x - center.x, 4 * p2.y - center.y, 2);
      seq.add(4 * p2.x - center.x, 8 * p2.y - center.y, 2);
    }

    return seq;
  }

  default void initSyncytium(){
    Block self = self();
    NeoplasmBlockModule selfAs = (NeoplasmBlockModule)self;

    self.size = 6;
    self.rotate = false;

    self.liquidCapacity = 10000f;
    self.itemCapacity = 10000;

    self.health = 100000;
    selfAs.setMaxHealOnce(30f);
  }
}
