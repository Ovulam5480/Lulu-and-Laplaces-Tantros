package LLL.world.neoplasm.effect;

import LLL.world.blocks.module.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmEye extends Block implements NeoplasmBlockModule{
  public TextureRegion eyeballRegion, pupilRegion;
  //左上右下
  public float[] bound = {1.25f, 1, 1.5f, 1};
  public float k = 0.1f;

  public NeoplasmEye(String name){
    super(name);

    liquidCapacity = 55f;
  }

  @Override
  public void load(){
    super.load();
    eyeballRegion = Core.atlas.find(name + "-eyeball");
    pupilRegion = Core.atlas.find(name + "-pupil");
  }

  @Annotations.ImplEntries
  public class NeoplasmEyeBuild extends Building implements NeoplasmBuildModule{
    public Vec2 lastView;
    public boolean randomRotation = Mathf.randomBoolean(0.01f);

    @Override
    public void updateTile(){
      super.updateTile();

      if(randomRotation){
        lastView.set(0, 16).rotate(Time.time * 8).add(this);
        return;
      }

      float minDst = 99999;
      Position target = null;

      for(Player player : Groups.player){
        if(player.dst(this) < minDst){
          minDst = player.dst(this);
          target = player;
        }
      }

      if(target != null){
        lastView.lerpDelta(target, 0.07f);
      }
    }

    @Override
    public void draw(){
      Draw.rect(baseRegion(), x, y);
      Draw.rect(eyeballRegion, x, y, rotation * 90);

      float xb = bound[Mathf.mod(-rotation + (lastView.x < x ? 2 : 0), 4)] * (lastView.x < x ? -1 : 1);
      float yb = bound[Mathf.mod(-rotation + (lastView.y < y ? 3 : 1), 4)] * (lastView.y < y ? -1 : 1);

      float px = (1 - Mathf.pow(Mathf.E, -Math.abs(lastView.x - x) * k)) * xb;
      float py = (1 - Mathf.pow(Mathf.E, -Math.abs(lastView.y - y) * k)) * yb;

      Draw.rect(pupilRegion, px + x, py + y);
    }

    @Override
    public void created(){
      lastView = new Vec2(this.x, this.y);
      rotation = Mathf.random(3);
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.bool(randomRotation);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      randomRotation = read.bool();
    }
  }
}
