package LLL.toMigration.Ovulam.world.drawBlock;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawBatchFactory extends DrawBlock{
  TextureRegion region, teamRegion, sideRegion, topRegion;

  public void draw(Building build){
    Draw.rect(region, build.x, build.y);

    Draw.color(build.team.color);
    Draw.rect(teamRegion, build.x, build.y);
    Draw.reset();

    Draw.z(Layer.blockOver - 1f);
    Draw.rect(sideRegion, build.x, build.y, build.rotdeg());

    Draw.z(Layer.blockBuilding - 1);
    Draw.rect(topRegion, build.x, build.y);
    Draw.reset();
  }

  public void load(Block block){
    region = Core.atlas.find(block.name);
    teamRegion = Core.atlas.find(block.name + "-team");
    sideRegion = Core.atlas.find(block.name + "-side");
    topRegion = Core.atlas.find(block.name + "-top");
  }

  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{region, teamRegion, sideRegion, topRegion};
  }
}
