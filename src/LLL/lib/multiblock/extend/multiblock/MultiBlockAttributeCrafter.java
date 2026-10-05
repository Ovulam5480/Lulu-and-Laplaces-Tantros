package LLL.lib.multiblock.extend.multiblock;

import LLL.lib.multiblock.extend.*;
import arc.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@Annotations.ImplEntries
//and also the code are duplicated, its silly but for qol mod reason i have to extend AttributeCrafter instead of MultiBlockCrafter
public class MultiBlockAttributeCrafter extends AttributeCrafter implements MultiBlock{

  public MultiBlockAttributeCrafter(String name){
    super(name);

    hasItems = true;
    hasLiquids = true;

    rotate = true;
    rotateDraw = true;
    quickRotate = false;
    allowDiagonal = false;
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return baseEfficiency + linkTiles(tile.x, tile.y, size, rotation).sumf(other -> other.floor().attributes.get(attribute)) >= minEfficiency && checkLink(tile, team, size, rotation);
  }

  @Override
  public void setBars(){
    super.setBars();
    if(outputLiquid == null && (outputLiquids == null || outputLiquids.length == 0)){
      removeBar("liquid");
    }
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    drawPotentialLinks(x, y);
    drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);

    if(!displayEfficiency) return;

    drawPlaceText(Core.bundle.format("bar.efficiency",
      (int)((baseEfficiency + Math.min(maxBoost, boostScale * sumTileAttribute(attribute, x, y, rotation))) * 100f)), x, y, valid);
  }

  public float sumTileAttribute(@Nullable Attribute attr, int x, int y, int rotation){
    if(attr == null) return 0;
    Tile tile = world.tile(x, y);
    if(tile == null) return 0;
    return linkTiles(x, y, size, rotation).sumf(other -> !floating && other.floor().isDeep() ? 0 : other.floor().attributes.get(attr));
  }

  @Annotations.ImplEntries
  public class MultiBlockAttributeCrafterBuild extends AttributeCrafterBuild implements MultiBlockEntity{
    @Override
    public void updateLinkProximity(){
      MultiBlockEntity.super.updateLinkProximity();
      attrsum = sumTileAttribute(attribute, tile.x, tile.y, rotation);
    }
  }
}
