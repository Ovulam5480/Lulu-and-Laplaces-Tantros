package LLL.world.blocks.special.tjTool;

import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class SandboxBlock extends Block{
  public boolean drawProximity = false;

  public SandboxBlock(String name){
    super(name);
    hasPower =
      outputsPower =
        consumesPower =
          conductivePower =
            hasItems =
              hasLiquids =
                rotateDraw = false;

    update = true;
    solid = true;

    configurable = false;
    saveConfig = false;
    clearOnDoubleTap = false;
    selectionRows = 5;
    selectionColumns = 6;
    noUpdateDisabled = true;

    envEnabled = Env.any;
    schematicPriority = -9;
    canOverdrive = false;
    placeableLiquid = true;
    alwaysUnlocked = true;
  }

  @SuppressWarnings("unused")
  public class SandboxBuild extends Building{
    protected boolean selecting = false;
    protected float selectingDrawRadius = 0f;

    public boolean checkBuild(Building other){
      return other != null && other.team == team;
    }

    @Override
    public void drawConfigure(){
      TjDraw.lightPoly(this, TjDraw.rainbow);
    }

    @Override
    public void drawSelect(){
      selecting = true;
      if(drawProximity && !rotate)
        proximity.each(
          other -> checkBuild(other) && (other.block.hasItems || other.block.hasLiquids),
          other -> Drawf.selected(other.tile, team.color));
    }

    @Override
    public boolean acceptItem(Building source, Item item){
      return false;
    }

    @Override
    public void handleItem(Building source, Item item){
    }

    @Override
    public boolean acceptLiquid(Building source, Liquid liquid){
      return false;
    }

    @Override
    public void handleLiquid(Building source, Liquid liquid, float amount){
    }
  }
}