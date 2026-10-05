package LLL.toMigration.Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;

//方块的建造效果
public class DrawPayloadConstruct extends DrawRecipePayload{
  @Override
  public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY){
    Draw.draw(Layer.blockOver, () -> Drawf.construct(building.x + offsetX, building.y + offsetY, payload.fullIcon,
      0, progress, progress, Time.time));
  }
}
