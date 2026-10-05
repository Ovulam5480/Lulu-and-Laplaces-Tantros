package LLL.graphics;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class OvulamDraw{
  //镜头范围内的图格, 不会出现图格越界的情况
  public static void eachCameraTiles(Cons<Tile> get){
    camera.bounds(Tmp.r1).grow(2 * tilesize);
    Tmp.r2.set(0, 0, (world.width() - 1) * tilesize, (world.height() - 1) * tilesize);

    if(!Intersector.intersectRectangles(Tmp.r1, Tmp.r2, Tmp.r3)){
      return;
    }

    for(int i = 0; i < Tmp.r3.width; i = i + tilesize){
      for(int j = 0; j < Tmp.r3.height; j = j + tilesize){
        get.get(world.tileWorld(Tmp.r3.x + i, Tmp.r3.y + j));
      }
    }
  }

  public static void drawCameraTiles(Boolf<Tile> t){
    drawCameraTiles(Color.white, t);
  }

  public static void drawCameraTiles(Color color, Boolf<Tile> t){
    Draw.color(color);
    Draw.z(Layer.flyingUnit);
    eachCameraTiles(tile -> {
      if(t.get(tile)) Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
    });
  }

  public void drawQuad(QuadTree<?> quadTree){
    if(quadTree.leaf){
      Lines.stroke(2f);

      Lines.rect(quadTree.bounds);
    }else{
      drawQuad(quadTree.topLeft);
      drawQuad(quadTree.topRight);
      drawQuad(quadTree.botLeft);
      drawQuad(quadTree.botRight);
    }
  }
}

