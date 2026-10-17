package lulu.dev;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.*;
import lulu.lib.universeKit.ScreenSampler;
import mindustry.content.*;
import mindustry.graphics.*;

/** 绘制实验；调用方负责绘制时机以及 FrameBuffer、Shader 的创建和释放。 */
public final class DevRendering{
  private DevRendering(){
  }

  /** 在世界 drawOver 回调中调用。 */
  public static void drawWorldCircle(FrameBuffer buffer, Shader shader){
    float z = Draw.z();
    Draw.z(Layer.fogOfWar + 1);
    buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    buffer.begin(Color.clear);
    Draw.blend(Blending.additive);
    Fill.circle(2500, 2500, 4);
    Draw.blend();
    buffer.end();
    Draw.blit(buffer, shader);
    Draw.z(z);
  }

  /** 在 HUD Element.draw 中调用，shader 为 null 时显示缩放纹理及边框。 */
  public static void drawFramebuffer(FrameBuffer buffer, Shader shader){
    int w = Core.graphics.getWidth();
    int h = Core.graphics.getHeight();
    buffer.resize(Math.max(1, w / 4), h);
    buffer.begin(Color.clear);
    Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w, h / 2f, 9 * 32 * 2, 9 * 32 * 2);
    Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w / 2f, h, 9 * 32 * 2, 9 * 32 * 2);
    Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w / 2f, h / 2f, 9 * 32 * 2, 9 * 32 * 2);
    buffer.end();

    if(shader != null){
      Draw.blit(buffer, shader);
    }else{
      Tmp.tr1.set(buffer.getTexture());
      Draw.scl(4);
      Draw.rect(Tmp.tr1, w / 2f, h / 2f);
      Draw.scl();
      Tmp.r1.setCentered(w / 2f, h / 2f, Tmp.tr1.width, Tmp.tr1.height);
      Lines.rect(Tmp.r1);
    }
  }

  /** 在 HUD Element.draw 中调用，shader 为 null 时执行黑底采样回绘实验。 */
  public static void drawScreenSampler(FrameBuffer buffer, Shader shader){
    int w = Core.graphics.getWidth();
    int h = Core.graphics.getHeight();
    buffer.resize(w, h);
    ScreenSampler.toBuffer(buffer);

    if(shader != null){
      Draw.blit(buffer, shader);
    }else{
      Tmp.tr1.set(buffer.getTexture());
      Draw.color(Color.black);
      Fill.circle(w / 2f, h / 2f, w);
      Draw.color();
      Draw.rect(Tmp.tr1, w / 2f, h / 2f, w, h);
    }
  }
}
