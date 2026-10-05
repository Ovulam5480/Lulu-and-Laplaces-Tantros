package LLL.util;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;

public class MathUtil{
  private static final Rand rand = new Rand();
  private static final Vec2 rv = new Vec2();
  private static Point2[][] pixelCircles = new Point2[64][];

  static{
    Seq<Point2> tmp = new Seq<>();
    for(int i = 0; i < 64; i++){
      tmp.clear();

      pixelCircleBand(i, (x, y) -> tmp.add(new Point2(x, y)));
      pixelCircles[i] = tmp.toArray(Point2.class);
    }
  }

  public static void stratifiedRandomSampling(long seed, int amount, float length, Floatc2 cons){
    stratifiedRandomSampling(seed, amount, length, 360f / amount, cons);
  }

  public static void stratifiedRandomSampling(long seed, int amount, float length, float bound, Floatc2 cons){
    rand.setSeed(seed);

    float step = 360f / amount;
    for(int i = 0; i < amount; i++){
      rv.trns(i * step + rand.random(bound), length);
      cons.get(rv.x, rv.y);
    }
  }

  public static Point2[] getPixelCircle(int radius){
    return pixelCircles[radius];
  }

  public static void pixelCircleBand(int radius, Intc2 cons){
    if(radius == 0){
      cons.get(0, 0);
      return;
    }

    int inner2 = (2 * radius - 1) * (2 * radius - 1);
    int outer2 = (2 * radius + 1) * (2 * radius + 1);

    int limit = radius + 1;
    for(int x = 0; x <= limit; x++){
      for(int y = 0; y <= x; y++){
        int d2_4 = 4 * (x * x + y * y);
        if(d2_4 >= inner2 && d2_4 < outer2){
          cons.get(+x, +y);
          cons.get(-x, +y);
          if(y != 0){
            cons.get(+x, -y);
            cons.get(-x, -y);
          }

          if(x != y){
            cons.get(+y, +x);
            cons.get(+y, -x);

            if(y != 0){
              cons.get(-y, +x);
              cons.get(-y, -x);
            }
          }
        }
      }
    }
  }
}