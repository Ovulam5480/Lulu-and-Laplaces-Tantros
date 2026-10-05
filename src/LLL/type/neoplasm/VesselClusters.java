package LLL.type.neoplasm;

import LLL.lib.multiblock.extend.*;
import LLL.type.*;
import LLL.world.blocks.module.*;
import LLL.world.modules.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class VesselClusters implements Visible{
  public NeoplasmNeuron.NeoplasmNeuronBuild neuron;
  public static final float pressure = 0.95f;

  public VesselClusters(NeoplasmNeuron.NeoplasmNeuronBuild neuron){
    this.neuron = neuron;
    initVisible();
  }

  void updateVesselLiquid(NeoplasmBuildModule build){
    if(build.children().size == 0) return;

    boolean isSync = build instanceof LinkBlock.LinkBuild;
    LinkBlock.LinkBuild asLink = isSync ? (LinkBlock.LinkBuild)build : null;

    float capacity = build.neoplasm().liquidCapacity;
    float has, had;

    NeoplasmLiquidModule liquids = build.neoplasm();
    had = has = liquids.amount();

    for(NeoplasmBuildModule child : build.children()){
      if(isSync){
        boolean isSyncChild = child instanceof LinkBlock.LinkBuild;

        if(isSyncChild){
          LinkBlock.LinkBuild asChild = (LinkBlock.LinkBuild)child;

          if(asChild.linkBuild == asLink.linkBuild){
            continue;
          }
        }
      }

      NeoplasmLiquidModule childLiquids = child.neoplasm();

      float childHas = childLiquids.amount();
      float childCapacity = child.neoplasm().liquidCapacity;

      float ofract = childHas / childCapacity;
      float fract = has / capacity;

      if(child.thrombus()){
        if(ofract * 1.3f < fract){
          child.changeThrombus(false);
        }
      }else{
        boolean isSource = child.isSourceOrgan();

        if(fract >= ofract){
          float amount = Math.min((fract - ofract) * capacity * pressure, childCapacity - childHas);

          has -= amount;
          childLiquids.add(amount);

          if(build.children().size > 2
            && !isSource
            && ofract > 0.9f
            && childLiquids.neoplasmMean.hasEnoughData()
            && childLiquids.neoplasmMean.mean() * 1000 < childCapacity){
            child.changeThrombus(true);
          }
        }else{
          childLiquids.add(0);
        }

        if(!isSource) updateVesselLiquid(child);
      }
    }

    liquids.add(has - had);
  }

  void updateVesselLiquidBranchParent(NeoplasmBuildModule build){
    if(build.parent() == null || build instanceof NeoplasmNeuron.NeoplasmNeuronBuild) return;

    float capacity = build.getBlock().liquidCapacity;
    float has, had;

    NeoplasmLiquidModule liquids = build.neoplasm();
    had = has = liquids.amount();

    NeoplasmBuildModule parent = build.parent();
    if(parent.parent() == build){
      return;
    }

    NeoplasmLiquidModule parentLiquids = parent.neoplasm();

    float parentHas = parentLiquids.amount();
    float parentCapacity = parent.getBlock().liquidCapacity;

    float ofract = parentHas / parentCapacity;
    float fract = has / capacity;

    if(fract >= ofract){
      float amount = Math.min((fract - ofract) * capacity * pressure, parentCapacity - parentHas);

      has -= amount;
      parentLiquids.add(amount);
    }

    if(build.children().size < 2) updateVesselLiquidBranchParent(parent);

    liquids.add(has - had);
  }

  private final Rect camera = new Rect();
  public static final Color thr = Pal.remove, sour = Pal.heal, blocking = Color.yellow;

  @Override
  public void draw(){
    Core.camera.bounds(camera);
    drawVesselLiquid(neuron, sour);
  }

  private void drawVesselLiquid(NeoplasmBuildModule build, Color color){
    if(build.children().size == 0) return;

    boolean isSync = build instanceof LinkBlock.LinkBuild;
    LinkBlock.LinkBuild asLink = isSync ? (LinkBlock.LinkBuild)build : null;

    for(NeoplasmBuildModule child : build.children()){
      if(!Tmp.r1.set(camera).grow(child.getBlock().clipSize).contains(child.x(), child.y())){
        continue;
      }

      float off = child.getBlock().offset;
      int size = child.getBlock().size;

      Tile tile = child.getTile();
      Draw.color(color, 0.7f);
      Fill.rect(tile.worldx() + off, tile.worldy() + off, size * 8, size * 8);

      if(isSync && child instanceof LinkBlock.LinkBuild l && l.linkBuild == asLink.linkBuild){
        drawVesselLiquid(child, Color.purple);
      }else if(child.thrombus()){
        drawVesselLiquid(child, thr);
      }else if(child.isSourceOrgan()){
        drawVesselLiquid(child, sour);
      }else if(child.neoplasm().amount() / child.getBlock().liquidCapacity > build.neoplasm().amount() / build.getBlock().liquidCapacity){
        drawVesselLiquid(child, blocking);
      }else if(color == blocking){
        drawVesselLiquid(child, sour);
      }else{
        drawVesselLiquid(child, color);
      }
    }
  }

  @Override
  public void initVisible(){
    neuron.visibles.add(this);
  }

  @Override
  public String description(){
    return "显示瘤液的运输情况";
  }
}
