package LLL.toMigration.Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class DrawPayloadBuild extends DrawRecipePayload{
  @Override
  public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY){
    Draw.draw(Layer.blockBuilding, () -> {
      Draw.color(Pal.accent);

      Shaders.blockbuild.region = payload.fullIcon;
      Shaders.blockbuild.time = Time.time;
      Shaders.blockbuild.progress = progress;

      Draw.rect(payload.fullIcon, building.x + offsetX, building.y + offsetY);
      Draw.flush();

      Draw.color();
    });
  }
}
