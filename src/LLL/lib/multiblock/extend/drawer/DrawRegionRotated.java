package LLL.lib.multiblock.extend.drawer;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawRegionRotated extends DrawBlock{
  public TextureRegion[] region;
  public boolean oneSprite = false;
  public String suffix = "";
  public int x = 0, y = 0;
  /**
   * Any number <=0 disables layer changes.
   */
  public float layer = -1;

  public DrawRegionRotated(String suffix){
    this.suffix = suffix;
  }

  public DrawRegionRotated(){
  }

  @Override
  public void draw(Building build){
    float z = Draw.z();
    if(layer > 0) Draw.z(layer);
    Tmp.v1.set(x, y).rotate(build.rotdeg());
    if(oneSprite){
      Draw.rect(region[build.rotation], Tmp.v1.x + build.x, Tmp.v1.y + build.y, build.rotdeg());
    }else{
      Draw.rect(region[build.rotation], Tmp.v1.x + build.x, Tmp.v1.y + build.y);
    }
    Draw.z(z);
  }

  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    Tmp.v1.set(x, y).rotate(plan.rotation * 90);
    if(oneSprite){
      Draw.rect(region[plan.rotation], Tmp.v1.x + plan.drawx(), Tmp.v1.y + plan.drawy(), plan.rotation * 90);
    }else{
      Draw.rect(region[plan.rotation], Tmp.v1.x + plan.drawx(), Tmp.v1.y + plan.drawy());
    }
  }

  @Override
  public TextureRegion[] icons(Block block){
    return super.icons(block);
  }

  @Override
  public void load(Block block){
    region = new TextureRegion[4];
    if(oneSprite){
      for(int i = 0; i < 4; i++){
        region[i] = Core.atlas.find(block.name + suffix);
      }
    }else{
      for(int i = 0; i < 4; i++){
        region[i] = Core.atlas.find(block.name + suffix + "-" + i);
      }
    }
  }
}
