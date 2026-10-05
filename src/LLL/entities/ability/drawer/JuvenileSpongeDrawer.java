package LLL.entities.ability.drawer;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class JuvenileSpongeDrawer extends Drawer{
  public Color color = Pal.remove;
  public float radius;
  public float distance;

  public Seq<Vec2> positions = Seq.with(
    new Vec2(-2, 1),
    new Vec2(0, 1),
    new Vec2(2, 1),
    new Vec2(-2, -1),
    new Vec2(0, -1),
    new Vec2(2, -1)
  );

  public JuvenileSpongeDrawer(float radius, float distance){
    this.radius = radius;
    this.distance = distance;
  }

  @Override
  public void draw(Unit unit){
    Draw.color(color, 0.6f);
    Lines.stroke(6);

    for(int i = 0; i < 3; i++){
      Tmp.v1.set(positions.get(i)).scl(distance).rotate(unit.rotation - 90).add(unit);
      Tmp.v2.set(positions.get(i + 3)).scl(distance).rotate(unit.rotation - 90).add(unit);

      Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
    }

    Tmp.v1.set(-2.4f, 0).scl(distance).rotate(unit.rotation - 90).add(unit);
    Tmp.v2.set(2.4f, 0).scl(distance).rotate(unit.rotation - 90).add(unit);
    Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
  }
}
