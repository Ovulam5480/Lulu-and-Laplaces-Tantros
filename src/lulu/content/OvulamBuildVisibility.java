package lulu.content;

import lulu.entities.gen.*;
import mindustry.*;
import mindustry.world.meta.*;

public class OvulamBuildVisibility{
  public static BuildVisibility frameBuilder = new BuildVisibility(() -> Vars.player.unit() instanceof FrameBuilderc);
}
