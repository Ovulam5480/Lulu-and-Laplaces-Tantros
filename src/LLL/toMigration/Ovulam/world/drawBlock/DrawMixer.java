package LLL.toMigration.Ovulam.world.drawBlock;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawMixer extends DrawBlock{
  public TextureRegion region, topRegion, rotatorRegion, iconRegion, pointRegion, lineRegion;

  public float radiusBig = 16;
  public float radiusSmail = 16;

  @Override
  public void load(Block block){
    region = Core.atlas.find(block.name);
    topRegion = Core.atlas.find(block.name + "-top");
    rotatorRegion = Core.atlas.find(block.name + "-rotator");
    iconRegion = Core.atlas.find(block.name + "-icon");
    pointRegion = Core.atlas.find(block.name + "-point");
    lineRegion = Core.atlas.find(block.name + "-line");
  }

  //todo
  @Override
  public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(region, plan.drawx(), plan.drawy());
    Draw.rect(topRegion, plan.drawx(), plan.drawy());
  }

  @Override
  public void draw(Building build){
    Draw.rect(region, build.x, build.y);

    float approach = build.totalProgress() / (60);

    float bx = Mathf.cos(approach) * radiusBig;
    float by = Mathf.sin(approach) * radiusBig;

    float sx = Mathf.cos(-approach * 2) * radiusSmail;
    float sy = Mathf.sin(-approach * 2) * radiusSmail;

    Draw.z(Layer.blockOver + 1);

    Drawf.spinSprite(rotatorRegion, build.x + bx * 2, build.y + by * 2, build.totalProgress() * 12);
    Drawf.spinSprite(rotatorRegion, build.x - bx + sx, build.y - by + sy, build.totalProgress() * 12);
    Drawf.spinSprite(rotatorRegion, build.x - bx - sx, build.y - by - sy, build.totalProgress() * 12);

    Lines.stroke(4f);
    Lines.line(lineRegion, build.x - bx + sx, build.y - by + sy,
      build.x - bx - sx, build.y - by - sy, false);
    Lines.line(lineRegion, build.x + bx * 2, build.y + by * 2,
      build.x - bx, build.y - by, false);
    Lines.stroke(1f);

    Draw.rect(pointRegion, build.x + bx * 2, build.y + by * 2);
    Draw.rect(pointRegion, build.x - bx, build.y - by);
    Draw.rect(pointRegion, build.x - bx + sx, build.y - by + sy);
    Draw.rect(pointRegion, build.x - bx - sx, build.y - by - sy);
    Draw.rect(pointRegion, build.x, build.y);

    Draw.rect(topRegion, build.x, build.y);

    Draw.reset();
  }

  @Override
  public TextureRegion[] icons(Block block){
    return new TextureRegion[]{iconRegion};
  }
}
