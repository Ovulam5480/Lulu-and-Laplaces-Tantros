package LLL.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class MetaBallManager{
  public static OvulamShaders.MetaBallShader shader;
  private final Rect viewport = new Rect();
  //todo 更多层?
  public Seq<MetaBall> metaBalls = new Seq<>();

  private final FrameBuffer buffer = new FrameBuffer();
  Bloom bloom;

  public void draw(){
    Core.camera.bounds(viewport);

    if(shader == null){
      shader = OvulamShaders.metaBall;
    }

    int i = 0;
    for(MetaBall ball : metaBalls){
      float clip = ball.clipSize();
      if(viewport.overlaps(ball.x() - clip / 2f, ball.y() - clip / 2f, clip, clip)){
        shader.metaBallX[i] = ball.x();
        shader.metaBallY[i] = ball.y();
        shader.metaBallRadius[i] = ball.getRadius();
        shader.metaBallColor[i] = ball.getColor().rgba8888();

        if(i++ == 1024){
          break;
        }
      }
    }

    shader.metaBallCount = i;
    shader.apply();

    Draw.draw(Layer.block, () -> {
      buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      buffer.begin(Color.clear);
      Draw.color(Color.clear);
      Draw.rect();

      buffer.end();
      buffer.blit(shader);
    });

    for(MetaBall ball : metaBalls){
      float clip = ball.clipSize();
      if(viewport.overlaps(ball.x() - clip / 2f, ball.y() - clip / 2f, clip, clip)){
        Draw.color(ball.getColor());
        Lines.circle(ball.x(), ball.y(), ball.getRadius());
      }
    }

    Draw.reset();
  }

  public float circleRadius(float hitSize, int blurPasses){
    return hitSize - blurPasses * blurPasses * 0.833f * 0.05f;
  }

  public interface MetaBall extends Drawc{
    float getRadius();

    Color getColor();
  }
}