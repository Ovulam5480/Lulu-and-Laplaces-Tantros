package LLL.entities.neoplasmBehavior;

import LLL.content.*;
import LLL.content.blocks.*;
import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.util.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.*;
import LLL.world.neoplasm.production.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.world.blocks.environment.*;

public class NeoplasmGrows{
  public static GrowLeafTask growRadula(){
    return new GrowAttributes(Neoplasm.neoplasmRadula, t -> t.drop() != null
      && t.drop() != Items.sand && t.drop() != Items.coal);
  }

  public static GrowLeafTask growPholas(){
    return new GrowAttributes(Neoplasm.neoplasmPholas, t -> Neoplasm.neoplasmPholas.canPlaceOn(t, null, 0));
  }

  public static GrowLeafTask growMurex(){
    return new GrowAttributes(Neoplasm.neoplasmMurex, (t, b, te) -> ((NeoplasmMurex)Neoplasm.neoplasmMurex).canDamageOn(t, te));
  }

  public static GrowLeafTask growBranchialHeart(){
    return new GrowAttributes(Neoplasm.neoplasmBranchialHeart, t -> {
      return t.floor().liquidDrop == Liquids.neoplasm
        || t.floor().liquidDrop == Liquids.water
        || (t.floor() instanceof SteamVent sv && sv.isCenterVent(t));
    });
  }

  public static GrowLeafTask growTriaxonBase(){
    return new GrowItemFilter(Neoplasm.neoplasmTriaxonBase, i -> i == OvulamItems.calcium);
  }

  public static GrowLeafTask growHexactinBase(){
    return new GrowItemFilter(Neoplasm.neoplasmHexactinBase, i -> i == Items.silicon);
  }

  public static GrowLeafTask growDodecactinBase(){
    return new GrowItemFilter(Neoplasm.neoplasmDodecactinBase, i -> i == OvulamItems.ferrum);
  }

  public static GrowLeafTask growPhosphorusLarvaCyst(){
    return new GrowItemFilter(Neoplasm.neoplasmPhosphorusLarvaCyst, i -> i == OvulamItems.phosphorus);
  }

  public static GrowLeafTask growCalciumPylorus(){
    return new GrowItemFilter(Neoplasm.neoplasmCalciumPylorus, i -> Neoplasm.calcium.contains(is -> is.item == i));
  }

  public static GrowLeafTask growPhosphorusPylorus(){
    return new GrowItemFilter(Neoplasm.neoplasmPhosphorusPylorus, i -> Neoplasm.phosphorus.contains(is -> is.item == i));
  }

  public static GrowLeafTask growSiliconPylorus(){
    return new GrowItemFilter(Neoplasm.neoplasmSiliconPylorus, i -> Neoplasm.silicon.contains(is -> is.item == i));
  }

  public static GrowLeafTask growFerrumPylorus(){
    return new GrowItemFilter(Neoplasm.neoplasmFerrumPylorus, i -> Neoplasm.ferrum.contains(is -> is.item == i));
  }

  public static GrowLeafTask growArterius(){
    return new GrowOwnedBlocksSelect<NeoplasmVessel.NeoplasmVesselBuild>(Neoplasm.neoplasmArterius, Neoplasm.neoplasmVessel, v -> {
      for(NeoplasmBuildModule child : v.children()){
        //v.neoplasm().amount() / v.getBlock().liquidCapacity > child.neoplasm().amount() / child.getBlock().liquidCapacity
        if(v.neoplasm().amount() * child.getBlock().liquidCapacity > child.neoplasm().amount() * v.getBlock().liquidCapacity){
          return true;
        }
      }

      return false;
    });
  }

  public static GrowLeafTask growAortus(){
    return new GrowOwnedBlocks(Neoplasm.neoplasmAortus, Neoplasm.neoplasmArterius);
  }

  public static GrowLeafTask growSettledlarvaCyst(){
    return new GrowOwnedBlocks(Neoplasm.settledlarvaCyst, Neoplasm.neoplasmTriaxonBase){{
      hasBlockLimit = true;
      blockLimit = 3;
    }};
  }

  public static GrowLeafTask growGanglion(int radius){
    return new GrowFunction(Neoplasm.neoplasmGanglion, MathUtil.getPixelCircle(radius).length, (n, i) -> {
      int x = n.tileX() + MathUtil.getPixelCircle(radius)[i].x * 2;
      int y = n.tileY() + MathUtil.getPixelCircle(radius)[i].y * 2;

      return Point2.pack(x, y);
    });
  }

  public static GrowLeafTask growSyncytium(){
    return new GrowFunction(Neoplasm.neoplasmHecto, 1, (n, i) -> Point2.pack(n.tileX() + 6, n.tileY()));
  }
}
