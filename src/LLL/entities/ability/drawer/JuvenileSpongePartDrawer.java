package LLL.entities.ability.drawer;

import LLL.lib.singularity.graphic.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;

public class JuvenileSpongePartDrawer extends Drawer{
  public ParenchymellaDrawer parenchymella;
  public float partX, partY;
  public float radius;

  public JuvenileSpongePartDrawer(float radius, float stroke, int amount, float rotateSpeed, float partX, float partY){
    this.radius = radius;
    this.partX = partX;
    this.partY = partY;

    parenchymella = new ParenchymellaDrawer(radius, stroke, amount, rotateSpeed){{
      px = partX;
      py = partY;
    }};
  }

  @Override
  public void draw(Unit unit){
    parenchymella.draw(unit);

    float x = Tmp.v1.x, y = Tmp.v1.y;

    float alpha = 0.2f + Mathf.sin(40, 0.05f);
    Tmp.c2.set(Draw.getColor()).a(alpha);

    Lines.stroke(1);
    Draw.alpha(0.8f);
    SglDraw.gradientPoly(x, y, Lines.circleVertices(radius) * 2, radius,
      Draw.getColor(), x, y, -radius * 0.3f, Tmp.c2, 0);
    SglDraw.gradientPoly(x, y, 12, radius * 0.0001f,
      Draw.getColor(), x, y, radius * 0.1f * (1 + Mathf.absin(20, 0.4f)), Tmp.c2, 0);
    Draw.alpha(1);
    Lines.poly(x, y, Lines.circleVertices(radius) * 2, radius);

    Draw.alpha(alpha);
    Fill.circle(x, y, radius * 0.7f);
  }
}
