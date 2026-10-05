package LLL.entities.ability.drawer;

import LLL.graphics.*;
import LLL.lib.singularity.graphic.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class TailFlagellumDrawer extends Drawer{
  public Vec2 flagellum;
  public Color color = Pal.remove;
  public float radius;

  public float swingScl = 1;
  public float lcsGniws = 1;

  public float tailLength = 16;

  public TailFlagellumDrawer(float radius){
    this.radius = radius;
  }

  @Override
  public void update(Unit unit){
    super.update(unit);

    Tmp.v1.set(-radius, 0).rotate(unit.rotation).add(unit);
    Tmp.v2.set(-tailLength, 0).rotate(Mathf.sin(20 * swingScl, 12)).rotate(unit.rotation);

    float x2 = Tmp.v2.x + Tmp.v1.x;
    float y2 = Tmp.v2.y + Tmp.v1.y;

    if(flagellum == null){
      flagellum = new Vec2(x2, y2);
    }

    flagellum.lerp(x2, y2, 0.13f * lcsGniws);
  }

  @Override
  public void draw(Unit unit){
    if(flagellum == null){
      return;
    }

    Draw.draw(Layer.flyingUnit - 1, () -> {
      Draw.color(color, 0.7f);
      MathRenderer.setThreshold(0.04f, 0.05f);

      Tmp.v1.set(-radius, 0).rotate(unit.rotation).add(unit);
      OvulamMathRenderers.drawParabola(Tmp.v1.x, Tmp.v1.y, flagellum.x, flagellum.y, Mathf.sin(Time.time + 20 * Mathf.pi, 20 * swingScl, 0.3f), 0);
    });
  }

  @Override
  public Ability copy(){
    TailFlagellumDrawer drawer = (TailFlagellumDrawer)super.copy();
    drawer.flagellum = null;
    return drawer;
  }
}
