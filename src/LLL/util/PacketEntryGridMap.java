package LLL.util;

import LLL.ctype.packet.*;
import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.graphics.*;

import static LLL.graphics.OvulamDraw.*;

public class PacketEntryGridMap{
  protected IntMap<PacketEntry> map = new IntMap<>();
  public boolean visible = false;

  public PacketEntryGridMap(){
    Events.on(EventType.WorldLoadEvent.class, e -> {
      clear();

      //todo Reads Packets
    });

    if(visible)
      Events.run(EventType.Trigger.draw, () -> {
        Draw.z(Layer.fogOfWar);
        Draw.color(0.5f, 0.5f, 1f, 0.5f);
        eachCameraTiles(tile -> {
          if(get(tile.x, tile.y) != null){
            Fill.square(tile.x * 8, tile.y * 8, 4);

            PacketEntry e = get(tile.x, tile.y);
            Lines.line(tile.x * 8, tile.y * 8, e.getX(), e.getY());
          }
        });
      });
  }

  private static int getHash(int x, int y){
    return (x << 16) + y;
  }

  public static Point2 hashToPoint2(int hash){
    return Tmp.p1.set(hash >> 16, hash & 0xffff);
  }

  public void add(int x, int y, PacketEntry entry){
    add(getHash(x, y), entry);
  }

  public void add(int hash, PacketEntry entry){
    map.put(hash, entry);
  }

  public boolean addAtEntry(PacketEntry entry){
    int size = Mathf.ceil(entry.packet.packetType().size);

    int[] point2s = new int[size * size];
    int[] index = {0};
    boolean[] used = {false};

    foreachAtEntry(entry, hash -> {
      if(map.get(hash) == null){
        point2s[index[0]++] = hash;
      }else{
        used[0] = true;
      }
    });

    if(used[0]){
      return false;
    }

    //todo 未经测试
    for(int point2 : point2s){
      map.put(point2, entry);
    }

    return true;
  }

//    public boolean replace(PacketEntry entry){
//        int size = Mathf.ceil(entry.packet.packetType().size);
//
//        foreachAtEntry(entry, size + 1,point2 -> {
//
//        });
//    }

  public PacketEntry get(int x, int y){
    return get(getHash(x, y));
  }

  public PacketEntry get(int hash){
    if(!map.containsKey(hash)){
      return null;
    }
    return map.get(hash);
  }

  public PacketEntry getAtEntry(PacketEntry entry){
    return get(World.toTile(entry.getX()), World.toTile(entry.getY()));
  }

  public void foreachAtEntry(PacketEntry entry, Cons<Integer> cons){
    this.foreachAtEntry(entry, Mathf.ceil(entry.packet.packetType().size), cons);
  }

  public void foreachAtEntry(PacketEntry entry, int ceilSize, Cons<Integer> cons){
    int centerX = Mathf.round(entry.getX() / 8);
    int centerY = Mathf.round(entry.getY() / 8);

    int startX;
    int startY;

    if(ceilSize % 2 == 0){
      startX = centerX - ceilSize / 2;
      startY = centerY - ceilSize / 2;
      if(entry.getX() / 8 - centerX > 0) startX++;
      if(entry.getY() / 8 - centerY > 0) startY++;
    }else{
      startX = centerX - (ceilSize - 1) / 2;
      startY = centerY - (ceilSize - 1) / 2;
    }

    for(int i = 0; i < ceilSize; i++){
      for(int j = 0; j < ceilSize; j++){
        cons.get(getHash(startX + i, startY + j));
      }
    }
  }

  public boolean has(int x, int y){
    return map.containsKey(getHash(x, y));
  }

  public void remove(int x, int y){
    remove(getHash(x, y));
  }

  public void remove(int hash){
    map.remove(hash);
  }

  public void removeAtEntry(PacketEntry entry){
    foreachAtEntry(entry, this::remove);
  }

  public IntMap.Values<PacketEntry> values(){
    return map.values();
  }

  public IntMap.Keys keys(){
    return map.keys();
  }

  public void clear(){
    map.clear();
  }

  public int size(){
    return map.size;
  }
}
