package LLL.lib.multiblock.extend;

import LLL.lib.multiblock.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

//i guess some method here are terrible and should use sizeOffset
public interface MultiBlock{
  default Block self(){
    return (Block)this;
  }

  @Annotations.BindField("canMirror")
  default boolean canMirror(){
    return false;
  }

  @Annotations.BindField(value = "linkValues", initialize = "new arc.struct.IntSeq()")
  default IntSeq linkValues(){
    return null;
  }

  @Annotations.BindField(value = "linkValues")
  default void setLinkValues(IntSeq values){
  }

  @Annotations.BindField(value = "linkBlockPos", initialize = "new arc.struct.Seq<>()")
  default Seq<Point2> linkBlockPos(){
    return null;
  }

  @Annotations.BindField(value = "linkBlockSize", initialize = "new arc.struct.IntSeq()")
  default IntSeq linkBlockSize(){
    return null;
  }

  int[] rotations = {0, 1, 2, 3, 0, 1, 2, 3};

  @Nullable
  default Block mirrorBlock(){
    String name = self().name;

    if(isMirror()) return content.block(name.replace("-mirror", ""));
    else return content.block(name + "-mirror");
  }

  default boolean isMirror(){
    return self().name.endsWith("-mirror");
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initMultiBlock(){
    Block block = self();

    addLink(linkValues());

    //always set those, these are not supposed to be changed
    block.rotateDraw = true;
    block.quickRotate = false;
    block.allowDiagonal = false;

    //always required due to Tile#getFlammability, in this case anuke sucks for this
    block.hasItems = true;
    block.hasLiquids = true;

    //current no support and i dont want things being disaster
    //outputLiquids = null;

    if(isMirror()){
      block.alwaysUnlocked = true;
    }

    getMaxSize(block.size, 0);

    float dst1 = Mathf.dst(0, 0, Tmp.r1.x, Tmp.r1.y);
    float dst2 = Mathf.dst(0, 0, Tmp.r1.x + Tmp.r1.width, Tmp.r1.y);
    float dst3 = Mathf.dst(0, 0, Tmp.r1.x, Tmp.r1.y + Tmp.r1.height);
    float dst4 = Mathf.dst(0, 0, Tmp.r1.x + Tmp.r1.width, Tmp.r1.y + Tmp.r1.height);

    block.clipSize = Math.max(dst1, Math.max(dst2, Math.max(dst3, dst4))) * 8 * 2;
  }

  @Annotations.MethodEntry(entryMethod = "setStats", context = {"stats -> stats", "size -> size"})
  default void setMultiBlockStats(Stats stats, int size){
    stats.remove(Stat.size);
    stats.add(Stat.size, "@x@", getMaxSize(size, 0).x, getMaxSize(size, 0).y);
  }

  @Annotations.MethodEntry(entryMethod = "changePlacementPath", paramTypes = {"arc.struct.Seq -> points", "int -> rotation"}, context = "size -> size", override = true)
  default void changeMultiBlockPlacementPath(Seq<Point2> points, int rotation, int size){
    Placement.calculateNodes(points, self(), rotation, (point, other) -> {
      if(rotation % 2 == 0){
        return Math.abs(point.x - other.x) <= getMaxSize(size, rotation).x;
      }else{
        return Math.abs(point.y - other.y) <= getMaxSize(size, rotation).y;
      }
    });
  }

  default Point2 calculateRotatedPosition(Point2 pos, int blockSize, int linkSize, int rotation){
    int shift = (blockSize + 1) % 2;
    int offset = (linkSize + 1) % 2;
    int px = pos.x, py = pos.y;

    return switch(rotation){
      case 1 -> new Point2(-py + shift - offset, px);
      case 2 -> new Point2(-px + shift - offset, -py + shift - offset);
      case 3 -> new Point2(py, -px + shift - offset);
      default -> new Point2(px, py); // default rotation 0
    };
  }

  default void addLink(IntSeq values){
    for(int i = 0; i < values.size; i += 3){
      linkBlockPos().add(new Point2(values.get(i), values.get(i + 1)));
      linkBlockSize().add(values.get(i + 2));
    }
  }

  @Annotations.MethodEntry(entryMethod = "canPlaceOn", paramTypes = {"mindustry.world.Tile -> tile", "mindustry.game.Team -> team", "int -> rotation"}, context = "size -> size")
  default boolean canPlaceOnMultiBlock(Tile tile, Team team, int rotation){
    return checkLink(tile, team, self().size, rotation);
  }

  default boolean checkLink(Tile tile, Team team, int size, int rotation){
    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);
      if(!Build.validPlace(getLinkBlockType(self(), s), team, tile.x + rotated.x, tile.y + rotated.y, 0, false)){
        return false;
      }
    }
    return true;
  }

  @Annotations.MethodEntry(entryMethod = "placeBegan", paramTypes = {"mindustry.world.Tile -> tile", "mindustry.world.Block"}, context = {"size -> size"})
  default void createPlaceholder(Tile tile, int size){
    if(state.rules.infiniteResources || tile == null || tile.build == null) return;

    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, tile.build.rotation);
      Tile t = world.tile(tile.x + rotated.x, tile.y + rotated.y);
      t.setBlock(MultiBlockLib.placeholderEntity[s - 1], tile.team(), 0);
      PlaceholderBlock.PlaceholderBuild b = (PlaceholderBlock.PlaceholderBuild)t.build;
      b.updateLink(tile);
    }
  }

  @Annotations.MethodEntry(entryMethod = "flipRotation", paramTypes = {"mindustry.entities.units.BuildPlan -> req", "boolean -> x"}, override = true)
  default void flipRotationMultiBlock(BuildPlan req, boolean x){
    if(canMirror()){
      if(mirrorBlock() != null){
        if(x){
          if(req.rotation == 1) req.rotation = 3;
          if(req.rotation == 3) req.rotation = 1;
        }else{
          if(req.rotation == 0) req.rotation = 2;
          if(req.rotation == 2) req.rotation = 0;
        }
        req.block = mirrorBlock();
      }else{
        req.rotation = rotations[req.rotation + (x ? 0 : 4)];
      }
    }else{
      if((x == (req.rotation % 2 == 0)) != self().invertFlip){
        req.rotation = self().planRotation(Mathf.mod(req.rotation + 2, 4));
      }
    }
  }

  default Seq<Building> setLinkBuild(Building building, Block block, Tile tile, Team team, int size, int rotation){
    Seq<Building> out = new Seq<>();
    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);
      Tile t = world.tile(tile.x + rotated.x, tile.y + rotated.y);
      Block linkBlock = getLinkBlockType(block, s);

      t.setBlock(linkBlock, team, 0);
      LinkBlock.LinkBuild b = (LinkBlock.LinkBuild)t.build;
      b.updateLink(building);
      out.add(b);
    }
    return out;
  }

  default LinkBlock getLinkBlockType(Block block, int linkSize){
    return block.outputsLiquid ? MultiBlockLib.linkEntityLiquid[linkSize - 1] : MultiBlockLib.linkEntity[linkSize - 1];
  }

  default Seq<Tile> linkTiles(int x, int y, int size, int rotation){
    Seq<Tile> tiles = new Seq<>();
    Point2 lb = leftBottomPos(size);
    for(int tx = 0; tx < size; tx++){
      for(int ty = 0; ty < size; ty++){
        Tile other = world.tile(x + tx + lb.x, y + ty + lb.y);
        if(other != null) tiles.add(other);
      }
    }

    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);
      Point2 lb2 = leftBottomPos(s).add(rotated);

      for(int tx = 0; tx < s; tx++){
        for(int ty = 0; ty < s; ty++){
          Tile other = world.tile(x + tx + lb2.x, y + ty + lb2.y);
          if(other != null) tiles.add(other);
        }
      }
    }

    return tiles;
  }

  default Point2 teamOverlayPos(int size, int rotation){
    Point2 out = leftBottomPos(size);

    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);
      Point2 lb = leftBottomPos(s).add(rotated);

      if((lb.x + lb.y) < (out.x + out.y)) out.set(lb.x, lb.y);
    }
    return out;
  }

  default Point2 statusOverlayPos(int size, int rotation){
    Point2 out = rightBottomPos(size);

    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);
      Point2 rb = rightBottomPos(s).add(rotated);

      if((rb.x - rb.y) > (out.x - out.y)) out.set(rb.x, rb.y);
    }
    return out;
  }

  default Point2 leftBottomPos(int size){
    int shift = (size + 1) % 2;
    return new Point2(-size / 2 + shift, -size / 2 + shift);
  }

  default Point2 rightBottomPos(int size){
    int shift = (size + 1) % 2;
    return new Point2(size / 2, -size / 2 + shift);
  }

  default Point2 getMaxSize(int size, int rotation){
    return getMaxSize(size, rotation, Tmp.r1);
  }

  default Point2 getMaxSize(int size, int rotation, Rect rect){
    int shift = (size + 1) % 2;
    int left = -size / 2 + shift, bot = -size / 2 + shift, right = size / 2, top = size / 2;

    Point2 out = new Point2(size, size);

    for(int i = 0; i < linkBlockPos().size; i++){
      Point2 p = linkBlockPos().get(i);
      int s = linkBlockSize().get(i);
      Point2 rotated = calculateRotatedPosition(p, size, s, rotation);

      int ort = Mathf.ceil((s - 1) / 2f), olb = Mathf.floor((s - 1) / 2f);

      left = Math.min(left, rotated.x - olb);
      right = Math.max(right, rotated.x + ort);
      bot = Math.min(bot, rotated.y - olb);
      top = Math.max(top, rotated.y + ort);
    }

    out.set(right - left + 1, top - bot + 1);
    rect.set(left - 0.5f, bot - 0.5f, right - left + 1, top - bot + 1);
    return out;
  }
}
