package LLL.toMigration.Ovulam.world.drawBlock;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawOrganize extends DrawBlock{
  public TextureRegion region, podRegion;

  public DrawOrganize(){
  }

  @Override
  public void load(Block block){
    region = Core.atlas.find(block.name);
    podRegion = Core.atlas.find(block.name + "-pod");
  }

  @Override
  public void draw(Building build){
    Draw.rect(region, build.x, build.y);
    Draw.rect(podRegion, build.x, build.y);
  }

  @Override
  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{region, podRegion};
  }
}
