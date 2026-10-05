package LLL.lib.singularity.world.blocks.drills;

import LLL.lib.singularity.*;
import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.world.blocks.*;
import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public abstract class MatrixMinerPlugin extends SglBlock{
  private static final Rand rand = new Rand();

  public int range;
  public int drillSize;
  public boolean pierceBuild;
  public float energyMulti = 1;
  public float drillMoveMulti = 1;
  public float warmupSpeed = 0.02f;

  public MatrixMinerPlugin(String name){
    super(name);
  }

  @Override
  public void setStats(){
    super.setStats();
    if(range > 0) stats.add(Stat.range, range + "x" + range + StatUnit.blocks.localized());
    if(drillSize > 0) stats.add(SglStat.drillSize, drillSize, StatUnit.blocks);
    if(pierceBuild) stats.add(SglStat.pierceBuild, true);
    if(drillMoveMulti != 1) stats.add(SglStat.drillMoveMulti, drillMoveMulti + "x");
    stats.add(SglStat.matrixEnergyUseMulti, energyMulti + "x");
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    for(Point2 edge : Edges.getEdges(size)){
      Building build = Vars.world.build(tile.x + edge.x, tile.y + edge.y);
      if(build instanceof MatrixMiner.MatrixMinerBuild b && b.team == team && (b.tile.x == tile.x || b.tile.y == tile.y)){
        return true;
      }
    }

    return false;
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);

    if(!valid){
      drawPlaceText(Core.bundle.get("infos.requireMasterDevice"), x, y, false);
    }
  }

  public abstract class MatrixMinerPluginBuild extends SglBuilding{
    public MatrixMiner.MatrixMinerBuild owner;
    public float warmup;

    public void setOwner(MatrixMiner.MatrixMinerBuild miner){
      this.owner = miner;
    }

    @Override
    public boolean updateValid(){
      return owner != null && owner.updateValid() && enabled;
    }

    @Override
    public float consEfficiency(){
      return super.consEfficiency() * (owner == null ? 0 : owner.consEfficiency());
    }

    @Override
    public void updateTile(){
      if(updateValid() && !owner.isChild(this)) owner = null;

      warmup = Mathf.approachDelta(warmup, updateValid() && consumeValid() ? owner.distributor.network.netEfficiency() : 0, warmupSpeed);
      super.updateTile();

      if(updateValid()) updatePlugin(owner);
    }

    @Override
    public void draw(){
      super.draw();
      Draw.z(Layer.effect);
      if(owner != null){
        Lines.stroke(1.6f * warmup, SglDrawConst.matrixNet);
        Lines.line(x, y, owner.x, owner.y);

        if(Sgl.config.animateLevel < 3) return;
        Draw.draw(Draw.z(), () -> {
          rand.setSeed(id);
          Draw.color(SglDrawConst.matrixNet);
          MathRenderer.setDispersion(rand.random(0.08f, 0.12f) * warmup);
          MathRenderer.setThreshold(0.4f, 0.6f);

          for(int i = 0; i < 3; i++){
            MathRenderer.drawSin(x, y, 1f, owner.x, owner.y, rand.random(1.5f, 2.5f),
              rand.random(360f, 720f),
              Time.time * rand.random(2f, 4f) * (rand.random(1f) > 0.5f ? 1 : -1)
            );
          }
        });
      }
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.f(warmup);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      warmup = read.f();
    }

    public float boost(){
      return 1;
    }

    public boolean angleValid(float angle){
      return false;
    }

    public int range(){
      return range;
    }

    public int drillSize(){
      return drillSize;
    }

    public float drillMoveMulti(){
      return drillMoveMulti;
    }

    public boolean pierceBuild(){
      return pierceBuild;
    }

    public float energyMultiplier(){
      return energyMulti;
    }

    public abstract void updatePlugin(MatrixMiner.MatrixMinerBuild owner);

  }
}
