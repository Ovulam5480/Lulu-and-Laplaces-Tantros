package LLL.graphics;

import LLL.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import mindustry.graphics.*;

public class OvulamTrail extends Trail{
  public FrameBuffer buffer = new FrameBuffer();
  //-1~1
  public float offsetBias;
  public float trailWidth = 8f;
  public Interp widthInterp = Interps.A;

  public OvulamTrail(int length, float bias){
    super(length);
    this.offsetBias = bias;
  }

  public OvulamTrail(int length){
    this(length, 0);
  }

  public void draw(Color color){
    float[] items = points.items;
    float lastAngle = this.lastAngle;
    Draw.color(color);

    for(int i = 0; i < points.size; i += 3){
      float x1 = items[i], y1 = items[i + 1], w1 = items[i + 2];
      float x2, y2, w2;

      //last position is always lastX/Y/W
      if(i < points.size - 3){
        x2 = items[i + 3];
        y2 = items[i + 4];
        w2 = items[i + 5];

        if(i == 0 && points.size >= (length - 1) * 3){
          x1 = Mathf.lerp(x1, x2, counter);
          y1 = Mathf.lerp(y1, y2, counter);
          w1 = Mathf.lerp(w1, w2, counter);
        }
      }else{
        x2 = lastX;
        y2 = lastY;
        w2 = lastW;
      }

      float z2 = -Angles.angleRad(x1, y1, x2, y2);
      //end of the trail (i = 0) has the same angle as the next.
      float z1 = i == 0 ? z2 : lastAngle;
      if(w1 <= 0.001f || w2 <= 0.001f || Mathf.zero(z2, 0.001f)) continue;

      float
        cx = Mathf.sin(z1) * widthInterp.apply((float)i / points.size) * w1 * trailWidth,
        cy = Mathf.cos(z1) * widthInterp.apply((float)i / points.size) * w1 * trailWidth,
        nx = Mathf.sin(z2) * widthInterp.apply((float)(i + 3) / points.size) * w2 * trailWidth,
        ny = Mathf.cos(z2) * widthInterp.apply((float)(i + 3) / points.size) * w2 * trailWidth;

      Fill.quad(
        x1 + cx * (offsetBias - 1), y1 + cy * (offsetBias - 1),
        x1 + cx * (offsetBias + 1), y1 + cy * (offsetBias + 1),
        x2 + nx * (offsetBias + 1), y2 + ny * (offsetBias + 1),
        x2 + nx * (offsetBias - 1), y2 + ny * (offsetBias - 1)
      );

      lastAngle = z2;
    }

    Draw.reset();
  }
}
