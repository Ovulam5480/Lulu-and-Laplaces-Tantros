package LLL.lib.universeKit;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import mindustry.game.*;

import java.lang.reflect.*;

public final class ScreenSampler{
  private static final Field currentBoundBuffer;
  private static final Field framebufferHandle;

  private static final FrameBuffer screenSwapBuffer = new FrameBuffer();

  private static final Shader baseScreen = new Shader(
    """
      attribute vec4 a_position;
      attribute vec2 a_texCoord0;
      
      varying vec2 v_texCoords;
      
      void main(){
          v_texCoords = a_texCoord0;
          gl_Position = a_position;
      }""",

    """
      uniform sampler2D u_texture;
      
      varying vec2 v_texCoords;
      
      void main() {
          gl_FragColor.rgb = texture2D(u_texture, v_texCoords).rgb;
          gl_FragColor.a = 1.0;
      }"""
  );

  static{
    try{
      currentBoundBuffer = GLFrameBuffer.class.getDeclaredField("currentBoundFramebuffer");
      currentBoundBuffer.setAccessible(true);

      framebufferHandle = GLFrameBuffer.class.getDeclaredField("framebufferHandle");
      framebufferHandle.setAccessible(true);
    }catch(NoSuchFieldException e){
      throw new RuntimeException(e);
    }

    if(Core.graphics != null) screenSwapBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

    Events.on(EventType.ResizeEvent.class, event -> screenSwapBuffer.resize(
      Core.graphics.getWidth(), Core.graphics.getHeight()
    ));
  }

  private ScreenSampler(){
  }

  public static void toBuffer(FrameBuffer target){
    GLFrameBuffer<?> buffer = currentBound();

    if(buffer != null){
      blitBuffer(buffer, target);
      return;
    }

    Draw.flush();

    if(screenSwapBuffer.getWidth() == target.getWidth() && screenSwapBuffer.getHeight() == target.getHeight()){
      copyPixels(target);
    }else{
      copyPixels(screenSwapBuffer);

      blitBuffer(screenSwapBuffer, target);
    }
  }

  private static void copyPixels(GLFrameBuffer<?> target){
    if(Core.gl30 != null){
      GL30 gl = Core.gl30;

      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, handle(target));
      gl.glReadBuffer(Gl.back);
      gl.glBlitFramebuffer(
        0, 0, Core.graphics.getWidth(), Core.graphics.getHeight(),
        0, 0, target.getWidth(), target.getHeight(),
        Gl.colorBufferBit, Gl.nearest
      );
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }else if(Core.gl20 != null){
      GLTexture texture = target.getTexture();

      texture.bind();
      Gl.copyTexImage2D(
        Gl.texture2d, 0,
        Gl.rgba, 0, 0,
        texture.width, texture.height,
        0
      );
      Gl.bindTexture(Gl.texture2d, 0);
    }
  }

  private static void blitBuffer(GLFrameBuffer<?> source, GLFrameBuffer<?> target){
    if(Core.gl30 != null){
      GL30 gl = Core.gl30;

      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, handle(source));
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, handle(target));
      gl.glBlitFramebuffer(
        0, 0, source.getWidth(), source.getHeight(),
        0, 0, target.getWidth(), target.getHeight(),
        Gl.colorBufferBit, Gl.nearest
      );
      Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
      Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }else if(Core.gl20 != null){
      target.begin(Color.clear);
      source.getTexture().bind(0);
      Draw.blit(baseScreen);
      Gl.bindTexture(Gl.texture2d, 0);
      target.end();
    }
  }

  private static GLFrameBuffer<?> currentBound(){
    try{
      return (GLFrameBuffer<?>)currentBoundBuffer.get(null);
    }catch(IllegalAccessException e){
      throw new RuntimeException(e);
    }
  }

  private static int handle(GLFrameBuffer<?> buffer){
    try{
      return framebufferHandle.getInt(buffer);
    }catch(IllegalAccessException e){
      throw new RuntimeException(e);
    }
  }
}
