package LLL.world;

import arc.*;
import mindustry.core.*;
import mindustry.world.*;

public class OvulamWorld extends World{
  @Override
  public Tiles resize(int width, int height){
    super.resize(width, height);
    Events.fire(new WorldResizeEvent());

    return tiles;
  }

  public static class WorldResizeEvent{
  }
}
