package LLL.entities.ability.drawer;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class ParenchymellaDrawer extends Drawer{
  public Color color = Pal.remove;
  public float radius;
  public float stroke;
  public int amount = 20;

  public float px, py;

  public float rotateSpeed = 0f;

  private static final Vec2 vector = new Vec2();

  public ParenchymellaDrawer(float radius, float stroke){
    this.radius = radius;
    this.stroke = stroke;
  }

  public ParenchymellaDrawer(float radius, float stroke, int amount){
    this.radius = radius;
    this.stroke = stroke;
    this.amount = amount;
  }

  public ParenchymellaDrawer(float radius, float stroke, int amount, float rotateSpeed){
    this.radius = radius;
    this.stroke = stroke;
    this.amount = amount;
    this.rotateSpeed = rotateSpeed;
  }

  @Override
  public void draw(Unit unit){
    Draw.color(color, 0.6f);

    Lines.stroke(stroke);

    Tmp.v1.set(px, py).rotate(unit.rotation - 90).add(unit);
    dashStripedRing(Tmp.v1.x, Tmp.v1.y, unit.rotation, radius, amount);
  }

  public void dashStripedRing(float x, float y, float rotate, float radius, int amount){
    vector.set(0, 0);

    int size = amount * 2;

    for(int i = 0; i < size; i += 2){
      vector.set(radius, 0).rotate(360f / size * i + 90 + rotate);
      if(rotateSpeed != 0){
        vector.rotate(Time.time * rotateSpeed);
      }
      float x1 = vector.x;
      float y1 = vector.y;

      vector.set(radius, 0).rotate(360f / size * (i + 1) + 90 + rotate);
      if(rotateSpeed != 0){
        vector.rotate(Time.time * rotateSpeed);
      }

      Lines.line(x1 + x, y1 + y, vector.x + x, vector.y + y, false);
    }
  }
}
