package LLL.world.neoplasm.production;

import LLL.world.blocks.module.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

//地面钻头
@Annotations.ImplEntries
public class NeoplasmRadula extends Block implements NeoplasmBlockModule{
  public float hardnessDrillMultiplier = 50f;
  public int tier = 9;
  public float drillTime = 300;
  public float rotateSpeed = 2f;
  public ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();
  protected @Nullable Item returnItem;
  protected int returnCount;

  protected final ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
  protected final Seq<Item> itemArray = new Seq<>();

  public NeoplasmRadula(String name){
    super(name);
    envEnabled |= Env.space;
  }

  @Override
  public void afterInit(){
    replaceable = false;
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    if(isMultiblock()){
      for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
        if(canMine(other)){
          return true;
        }
      }
      return false;
    }else{
      return canMine(tile);
    }
  }

  protected void countOre(Tile tile){
    returnItem = null;
    returnCount = 0;

    oreCount.clear();
    itemArray.clear();

    for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
      if(canMine(other)){
        oreCount.increment(other.drop(), 0, 1);
      }
    }

    for(Item item : oreCount.keys()){
      itemArray.add(item);
    }

    itemArray.sort((item1, item2) -> {
      int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
      if(type != 0) return type;
      int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
      if(amounts != 0) return amounts;
      return Integer.compare(item1.id, item2.id);
    });

    if(itemArray.size == 0){
      return;
    }

    returnItem = itemArray.peek();
    returnCount = oreCount.get(itemArray.peek(), 0);
  }

  public boolean canMine(Tile tile){
    if(tile == null || tile.block().isStatic()) return false;
    Item drops = tile.drop();
    return drops != null && drops.hardness <= tier;
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);

    Tile tile = world.tile(x, y);
    if(tile == null) return;

    countOre(tile);

    if(returnItem != null){
      float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
      float dx = x * tilesize + offset - width / 2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
      Draw.mixcol(Color.darkGray, 1f);
      Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
      Draw.reset();
      Draw.rect(returnItem.fullIcon, dx, dy, s, s);
    }else{
      Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> t.drop() != null && t.drop().hardness > tier);
      Item item = to == null ? null : to.drop();
      if(item != null){
        drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
      }
    }
  }

  public float getDrillTime(Item item){
    return (drillTime + hardnessDrillMultiplier * item.hardness) / drillMultipliers.get(item, 1f);
  }

  @Annotations.ImplEntries
  public class NeoplasmRadulaBuild extends Building implements NeoplasmBuildModule{
    public float progress;
    public float timeDrilled;
    public float lastDrillSpeed;
    public float delay;

    public int dominantItems;
    public Item dominantItem;

    @Override
    public boolean shouldConsume(){
      return items.total() < itemCapacity && enabled && dominantItem != null;
    }

    @Override
    public void drawSelect(){
      drawItemSelection(dominantItem);
    }

    @Override
    public void pickedUp(){
      dominantItem = null;
    }

    @Override
    public void onProximityUpdate(){
      super.onProximityUpdate();

      countOre(tile);
      dominantItem = returnItem;
      dominantItems = returnCount;
      if(dominantItem != null){
        delay = getDrillTime(dominantItem);
      }else{
        suicide();
      }
    }

    @Override
    public Object senseObject(LAccess sensor){
      if(sensor == LAccess.firstItem) return dominantItem;
      return super.senseObject(sensor);
    }

    @Override
    public void updateTile(){
      if(timer(timerDump, dumpTime / timeScale)){
        dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
      }

      if(dominantItem == null){
        return;
      }

      timeDrilled += delta();

      if(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0){
        lastDrillSpeed = (efficiency * dominantItems) / delay;
        progress += delta() * dominantItems * efficiency;
      }else{
        lastDrillSpeed = 0f;
        return;
      }

      if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
        int amount = (int)(progress / delay);
        for(int i = 0; i < amount; i++){
          produced(dominantItem, 1);
          handleItem(this, dominantItem);
        }

        progress %= delay;
      }
    }

    @Override
    public float progress(){
      return dominantItem == null ? 0f : Mathf.clamp(progress / delay);
    }

    @Override
    public double sense(LAccess sensor){
      if(sensor == LAccess.progress && dominantItem != null) return progress;
      return super.sense(sensor);
    }

    @Override
    public void draw(){
      Draw.scl(neoplasmScale());
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }

    @Override
    public void write(Writes write){
      write.f(progress);
    }

    @Override
    public void read(Reads read, byte revision){
      progress = read.f();
    }
  }
}
