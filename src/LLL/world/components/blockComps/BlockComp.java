package LLL.world.components.blockComps;

import mindustry.world.*;

public interface BlockComp{
  default Block self(){
    return (Block)this;
  }

  default <T> T selfAs(){
    return (T)this;
  }
}
