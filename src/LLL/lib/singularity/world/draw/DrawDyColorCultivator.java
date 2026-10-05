package LLL.lib.singularity.world.draw;

import LLL.lib.singularity.*;
import LLL.lib.singularity.graphic.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawDyColorCultivator<T extends Building> extends DrawBlock{
  public Func<T, Color> plantColor = e -> Color.valueOf("5541b1");
  public Func<T, Color> plantColorLight = e -> Color.valueOf("7457ce");
  public Func<T, Color> bottomColor = e -> Color.valueOf("474747");

  public Floatf<T> alpha = Building::warmup;

  public int bubbles = 12, sides = 8;
  public float strokeMin = 0.2f, spread = 3f, timeScl = 70f;
  public float recurrence = 6f, radius = 3f;

  public TextureRegion middle;

  @SuppressWarnings("unchecked")
  @Override
  public void draw(Building build){
    if(Sgl.config.animateLevel < 2) return;

    T entity = (T)build;
    Drawf.liquid(middle, build.x, build.y, build.warmup(), plantColor.get(entity));

    Draw.color(bottomColor.get(entity), plantColorLight.get(entity), alpha.get(entity));

    rand.setSeed(build.pos());
    for(int i = 0; i < bubbles; i++){
      float x = rand.range(spread), y = rand.range(spread);
      float life = 1f - ((Time.time / timeScl + rand.random(recurrence)) % recurrence);

      if(life > 0){
        Lines.stroke(build.warmup() * (life + strokeMin));
        Lines.poly(build.x + x, build.y + y, sides, (1f - life) * radius);
      }
    }

    Draw.color();
  }

  @Override
  public void load(Block block){
    middle = Core.atlas.find(block.name + "_middle");
  }

  @Override
  public TextureRegion[] icons(Block block){
    return SglDrawConst.EMP_REGIONS;
  }
}
