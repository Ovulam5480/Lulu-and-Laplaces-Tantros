package LLL.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;

import static arc.Core.*;

public class OvulamGraphics{
  public MetaBallManager manager;
  public static FrameBuffer buffer;
  public static Mat mat = new Mat();

  public OvulamShake shake;

  //public StaticMeshBuildRenderer staticMeshBuildRenderer = new StaticMeshBuildRenderer();

  public OvulamGraphics(){
    init();

    shake = new OvulamShake();

    //Events.run(EventType.Trigger.drawOver, () -> manager.draw());
  }

  public void init(){
    manager = new MetaBallManager();
    //testTrail();
  }

  public static void drawBufferTextureWorld(FrameBuffer buffer, Runnable setter){
    Draw.draw(Layer.groundUnit + 1, () -> {
      Tmp.tr1.set(Draw.wrap(buffer.getTexture()));
      Tmp.tr1.flip(false, true);

      Draw.scl(32f / 8 / Vars.renderer.getDisplayScale());

      setter.run();

      Draw.rect(Tmp.tr1, camera.position.x, camera.position.y);
      Draw.scl();
    });
  }

  public static TextureRegion renderToRegion(Runnable render, TextureRegion toset){
    return renderToRegion(render, toset, camera.position.x, camera.position.y, Core.graphics.getWidth(), Core.graphics.getHeight());
  }

  public static TextureRegion renderToRegion(Runnable render, TextureRegion toset,
                                             float renderX, float renderY,
                                             float width, float height){
    if(buffer == null){
      buffer = new FrameBuffer();
    }

    buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    buffer.begin(Color.white);
    Draw.scl(32f / 8 / Vars.renderer.getDisplayScale());

    mat.set(Draw.trans());
    Draw.trans().translate(camera.position.x - renderX, camera.position.y - renderY);

    render.run();

    buffer.end();
    Draw.scl();
    Draw.trans().set(mat);

    Texture texture = buffer.getTexture();
    texture.setFilter(Core.settings.getBool("linear") ? Texture.TextureFilter.linear : Texture.TextureFilter.nearest);

    int rx = (int)(Core.graphics.getWidth() - width * 4) / 2;
    int ry = (int)(Core.graphics.getHeight() - height * 4) / 2;
    int rw = (int)width * 4;
    int rh = (int)height * 4;

    toset.set(texture, rx, ry, rw, rh);
    toset.flip(false, true);

    return toset;
  }
}
