package LLL.entities.ability.drawer;

import LLL.lib.singularity.graphic.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SettledlarvaDrawer extends Drawer{
  public float innerRadius = 15 * 0.7f;
  public Color color = Pal.remove;

  @Override
  public void draw(Unit unit){
    float radius = unit.hitSize * 0.7f;
    float x = unit.x(), y = unit.y();

    Draw.color(color, 0.8f);
    Lines.poly(x, y, Lines.circleVertices(innerRadius) * 2, innerRadius + 0.5f);

    Vec2 v = Tmp.v1.trns(Time.time * Mathf.pi / 5, radius);
    Lines.line(x + v.x, y + v.y, x - v.x, y - v.y);
    v.rotate90(1);
    Lines.line(x + v.x, y + v.y, x - v.x, y - v.y);
    v.trns(-Time.time * Mathf.E / 5, radius);
    Lines.line(x + v.x, y + v.y, x - v.x, y - v.y);
    v.rotate90(1);
    Lines.line(x + v.x, y + v.y, x - v.x, y - v.y);

    float alpha = 0.2f + Mathf.sin(40, 0.05f);
    Tmp.c2.set(Draw.getColor()).a(alpha);

    Draw.alpha(0.8f);
    SglDraw.gradientPoly(x, y, Lines.circleVertices(radius) * 2, radius,
      Draw.getColor(), x, y, -radius * 0.3f, Tmp.c2, 0);
    SglDraw.gradientPoly(x, y, 12, radius * 0.0001f,
      Draw.getColor(), x, y, radius * 0.1f * (1 + Mathf.absin(20, 0.4f)), Tmp.c2, 0);
    Draw.alpha(1);
    Lines.poly(x, y, Lines.circleVertices(radius) * 2, radius + 0.5f);

    Draw.alpha(alpha);
    Fill.circle(x, y, radius * 0.7f);
    Draw.reset();
  }
}
