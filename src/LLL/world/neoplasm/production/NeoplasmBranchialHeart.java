package LLL.world.neoplasm.production;

import LLL.world.blocks.module.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmBranchialHeart extends Block implements NeoplasmBlockModule{
  float section = 0.2f;
  Interp beatInterp = i -> {
    if(i < section) return Interp.pow2In.apply(i / section);
    return 1 - Interp.pow2Out.apply((i - section) / (1 - section));
  };

  public float neoplasmAbsorbRate = 200f;
  public float beatTime = 120f;

  public NeoplasmBranchialHeart(String name){
    super(name);
    size = 3;
    liquidCapacity = 1800;
  }

  @Annotations.ImplEntries
  public class NeoplasmBranchialHeartBuild extends Building implements NeoplasmBuildModule{
    public float neoplasmScale;

    @Override
    public boolean isSourceOrgan(){
      return true;
    }

    @Override
    public void draw(){
      Draw.scl(neoplasmScale);
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }

    @Override
    public void updateNeoplasmScale(){
      neoplasmScale = beatInterp.apply(Mathf.mod(Time.time, beatTime) / beatTime) * -0.2f + 1.1f;
    }
  }
}
