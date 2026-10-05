package LLL.ctype;

import LLL.util.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class HardeningLiquid extends Liquid{
  public Block hardeningTarget;
  public float hardeningRate = 0.0005f;
  public Interp hardeningInterp = Interp.pow2In;

  private final Prov<Block> blockProv;

  public HardeningLiquid(String name, Color color, Prov<Block> blockProv){
    super(name, color);
    this.blockProv = blockProv;

    heatCapacity = 0.2f;
    viscosity = 0.9f;
    coolant = false;
    incinerable = false;
  }

  @Override
  public void init(){
    super.init();
    hardeningTarget = blockProv.get();
  }

  public boolean willBoil(){
    return false;
  }

  @Override
  public void update(Puddle puddle){
    if((Vars.state.rules.env & Env.underwater) == 0
      && puddle.tile.block() == Blocks.air
      && puddle.tile.floor().liquidDrop == null
      && Mathf.chance(hardeningRate * hardeningInterp.apply(puddle.amount / Puddles.maxLiquid) * Time.delta)){

      Team team = null;
      int x = puddle.tile.x;
      int y = puddle.tile.y;

      for(Point2 point2 : MathUtil.getPixelCircle(1)){
        Tile tile = Vars.world.tile(x + point2.x, y + point2.y);
        if(tile != null && tile.build != null){
          team = tile.build.team();
          break;
        }
      }

      if(team == null){
        for(Point2 point2 : MathUtil.getPixelCircle(2)){
          Tile tile = Vars.world.tile(x + point2.x, y + point2.y);
          if(tile != null && tile.build != null){
            team = tile.build.team();
            break;
          }
        }
      }

      if(team == null){
        team = Team.derelict;
      }

      puddle.tile.setBlock(hardeningTarget, team);
    }
  }
}
