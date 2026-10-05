package LLL.world.blocks.entity;

import LLL.entities.gen.*;
import LLL.world.blocks.module.*;
import arc.math.*;
import arc.math.geom.*;
import universecore.annotations.*;

import java.util.*;

@Annotations.ImplEntries
public class LargeTileEntityBlock extends TileEntityBlock implements MultiSizeBlock{
  public int entitySize;

  protected static int maxLargeSize = 128;

  private static final Point2[][] edges = new Point2[maxLargeSize][0];
  private static final Point2[][] edgeInside = new Point2[maxLargeSize][0];

  static{
    for(int i = 0; i < maxLargeSize; i++){
      int bot = -(int)(i / 2f) - 1;
      int top = (int)(i / 2f + 0.5f) + 1;
      edges[i] = new Point2[(i + 1) * 4];

      int idx = 0;

      for(int j = 0; j < i + 1; j++){
        //bottom
        edges[i][idx++] = new Point2(bot + 1 + j, bot);
        //top
        edges[i][idx++] = new Point2(bot + 1 + j, top);
        //left
        edges[i][idx++] = new Point2(bot, bot + j + 1);
        //right
        edges[i][idx++] = new Point2(top, bot + j + 1);
      }

      Arrays.sort(edges[i], (e1, e2) -> Float.compare(Mathf.angle(e1.x, e1.y), Mathf.angle(e2.x, e2.y)));

      edgeInside[i] = new Point2[edges[i].length];

      for(int j = 0; j < edges[i].length; j++){
        Point2 point = edges[i][j];
        edgeInside[i][j] = new Point2(Mathf.clamp(point.x, -(int)((i) / 2f), (int)(i / 2f + 0.5f)),
          Mathf.clamp(point.y, -(int)((i) / 2f), (int)(i / 2f + 0.5f)));
      }
    }
  }

  public LargeTileEntityBlock(String name, int entitySize){
    super(name);

    this.entitySize = entitySize;

    instantBuild = true;

    entityProvider = TileEntity::create;
  }

  @Override
  public Point2[] getInsideEdges(){
    return edgeInside[size - 1];
  }

  @Override
  public void init(){
    size = sized = entitySize;
    super.init();
  }
}
