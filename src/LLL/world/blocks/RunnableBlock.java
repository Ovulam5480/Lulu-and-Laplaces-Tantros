package LLL.world.blocks;

import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class RunnableBlock extends Block{
  public Runnable runnable;

  public RunnableBlock(String name){
    super(name);
    update = true;

    buildVisibility = BuildVisibility.shown;
  }

  public class RunnableBuild extends Building{
    @Override
    public void created(){
      super.created();
      runnable.run();
    }
  }
}
