package LLL.entities.ability.drawer;

import LLL.lib.singularity.graphic.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class TrichimellaDrawer extends Drawer{
  public float sclH, sclV;

  public TrichimellaDrawer(float sclH, float sclV){
    this.sclH = sclH;
    this.sclV = sclV;
  }

  @Override
  public void draw(Unit unit){
    Draw.color(Pal.remove);
    Tmp.c2.set(Draw.getColor()).a(0.1f);

    float ux = unit.x(), uy = unit.y();
    float radius = unit.hitSize * 0.7f;
    float hr = radius * sclH, vr = radius * sclV;

    Draw.alpha(0.8f);
    SglDraw.gradientPoly(ux, uy, 12, 0.0001f,
      Draw.getColor(), ux, uy, radius * 0.35f * (1 + Mathf.absin(6, 0.4f)), Tmp.c2, 0);

    Draw.draw(Layer.flyingUnit + 2, () -> {
      Draw.color(Pal.remove);
      Draw.alpha(0.6f);
      MathRenderer.setThreshold(0.05f, 0.05f);
      MathRenderer.drawOval(ux, uy, hr, vr, unit.rotation);
      Draw.alpha(0.6f);

      MathRenderer.setDispersion(0.1f);
      MathRenderer.setThreshold(0.01f, 0.2f);
      MathRenderer.drawOval(ux, uy, hr, vr, unit.rotation);
      MathRenderer.setDispersion(0.02f);
    });

    Draw.reset();
  }
}
