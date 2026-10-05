package LLL.lib.singularity.core;

import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;

public class ProxyBuildings{
  protected ObjectMap<Block, Building> oldBuildType = new ObjectMap<>();

  public void setBuildType(Block block, Building buildType){
    if(block.buildType.get() != buildType){
      oldBuildType.put(block, block.buildType.get());
      block.buildType = () -> buildType;
    }
  }
}
