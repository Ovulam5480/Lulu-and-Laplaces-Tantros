package LLL.lib.seam.world.blocks;

import LLL.lib.seam.*;
import LLL.lib.seam.core.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;

public class SubWorldMonitor extends Block{
  public SubWorldMonitor(String name){
    super(name);
    update = true;
    solid = true;
    destructible = true;
  }

  public class SubWorldMonitorBuild extends Building{
    public SubWorld subworld;
    public SubWorldRenderer renderer;
    public TextureRegion region = new TextureRegion();
    public FrameBuffer currentFbo;

    @Override
    public void created(){
      super.created();
      subworld = Seam.createWorld();
      subworld.resize(size, size);

      subworld.context.run(() -> {
        subworld.world.loadGenerator(size, size, tiles -> {
          for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
              Block floor = Blocks.stone;
              Block wall = Blocks.air;

              float noise = Simplex.noise2d(1, 2, 0.5, 0.2, x, y);
              if(noise > 0.6f){
                floor = Blocks.dirt;
              }else if(noise > 0.4f){
                floor = Blocks.sand;
              }

              Tile t = new Tile(x, y, floor, Blocks.air, wall);

              if(Simplex.noise2d(2, 2, 0.5, 0.1, x, y) > 0.7){
                t.setBlock(Blocks.conveyor, Team.sharded);
              }

              tiles.set(x, y, t);
            }
          }
        });
      });

      subworld.state.set(GameState.State.playing);

      renderer = new SubWorldRenderer();
      renderer.resize(size * 32, size * 32);

      Seam.monitors.add(this);
    }

    public void renderSubWorld(){
      if(subworld != null && renderer != null){
        float cx = (size - 1) * 4f;
        float cy = (size - 1) * 4f;
        currentFbo = renderer.render(subworld, cx, cy, size * 8f, size * 8f);
      }
    }

    @Override
    public void draw(){
      super.draw();

      if(currentFbo != null){
        Draw.color();
        region.set(currentFbo.getTexture());
        region.set(0, 0, currentFbo.getWidth(), currentFbo.getHeight());
        region.flip(false, true);
        Draw.rect(region, x, y, size * 8f, size * 8f);
      }
    }

    @Override
    public void remove(){
      Seam.monitors.remove(this);
      if(subworld != null) Seam.removeWorld(subworld);
      if(renderer != null) renderer.dispose();
      super.remove();
    }
  }
}
