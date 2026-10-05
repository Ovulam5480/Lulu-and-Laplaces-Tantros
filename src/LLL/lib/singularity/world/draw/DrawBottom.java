package LLL.lib.singularity.world.draw;

import LLL.lib.singularity.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBottom extends DrawBlock{
  public TextureRegion bottom;

  @Override
  public void load(Block block){
    bottom = Core.atlas.find(block.name + "_bottom", Singularity.getModAtlas("bottom_" + block.size));
  }

  @Override
  public void draw(Building build){
    float z = Draw.z();
    Draw.z(Layer.blockUnder);
    Draw.rect(bottom, build.x, build.y);
    Draw.z(z);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(bottom, plan.drawx(), plan.drawy());
  }

  @Override
  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{bottom};
  }
}
