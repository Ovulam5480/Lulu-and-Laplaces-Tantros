package LLL.world.neoplasm.production;

import LLL.world.blocks.module.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmPholas extends Block implements NeoplasmBlockModule{
  public int tier = 9;
  public float drillTime = 100f;
  public ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

  private static final ItemSeq tmp = new ItemSeq();

  public NeoplasmPholas(String name){
    super(name);
  }

  @Override
  public void setBars(){
    super.setBars();

    //addBar("progress", (NeoplasmPholasBuild e) -> new Bar(() -> String.valueOf(e.progress() * 100), () -> Pal.accent, e::progress));
  }

  @Override
  public void afterInit(){
    replaceable = false;
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return canMineOn(tile);
  }

  public boolean canMineOn(Tile tile){
    return getItems(tile.x, tile.y, tmp).total > 0;
  }

  public ItemSeq getItems(int tx, int ty, ItemSeq items){
    items.clear();

    for(Point2 edge : getEdges()){
      Tile t = Vars.world.tile(tx + edge.x, ty + edge.y);

      if(t != null && t.wallDrop() != null && tier >= t.wallDrop().hardness){
        items.add(t.wallDrop());
      }
    }

    return items;
  }

  @Annotations.ImplEntries
  public class NeoplasmPholasBuild extends Building implements NeoplasmBuildModule{
    public ItemSeq drillItems = new ItemSeq();
    public float warmup, time;

    @Override
    public float progress(){
      return time / (drillTime * drillItems.total);
    }

    @Override
    public void updateTile(){
      warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 60f);

      time += edelta() * warmup;

      if(time > (drillTime * drillItems.total)){
        items.add(drillItems);
        time %= (drillTime * drillItems.total);
      }

      if(timer(timerDump, dumpTime / timeScale)){
        dump();
      }
    }

    @Override
    public void draw(){
      Draw.scl(neoplasmScale());
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }

    @Override
    public void created(){
      super.created();

      getItems(tileX(), tileY(), drillItems);
    }

    @Override
    public void write(Writes write){
      write.f(warmup);
      write.f(time);
    }

    @Override
    public void read(Reads read, byte revision){
      warmup = read.f();
      time = read.f();
    }
  }
}
