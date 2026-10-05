package LLL.entities.ability.drawer;


import LLL.graphics.*;
import LLL.lib.singularity.graphic.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class AmphiblastulaDrawer extends Drawer{
  public Color color = Pal.remove;
  public static Vec2 tmp = new Vec2();
  public static int[] dir = {1, -1};

  public Seq<Vec2> flagellums = new Seq<>();

  @Override
  public void update(Unit unit){
    float radius = unit.hitSize * 0.7f;
    int index = 0;
    for(int i = 1; i < 5; i++){
      for(int j : dir){
        Tmp.v1.set(-radius, 0).rotate(i * 25).scl(1, j).rotate(unit.rotation).add(unit);
        Tmp.v2.set(-8, 0).rotate(i * 15 + Mathf.sin(20, 3 * (i + 2))).scl(1, j).rotate(unit.rotation);

        float x2 = Tmp.v2.x + Tmp.v1.x;
        float y2 = Tmp.v2.y + Tmp.v1.y;

        if(index >= flagellums.size){
          flagellums.add(new Vec2(x2, y2));
        }

        flagellums.get(index).lerp(x2, y2, 0.13f);

        index++;
      }
    }
  }

  @Override
  public void draw(Unit unit){
    float radius = unit.hitSize * 0.7f;

    Draw.draw(Layer.flyingUnit - 1, () -> {
      Draw.color(color, 0.7f);
      MathRenderer.setThreshold(0.04f, 0.05f);

      int index = 0;
      for(int i = 1; i < 5; i++){
        for(int j : dir){
          Tmp.v1.set(-radius, 0).rotate(i * 25).scl(1, j).rotate(unit.rotation).add(unit);

          float x1 = Tmp.v1.x;
          float y1 = Tmp.v1.y;

          Vec2 v = flagellums.get(index);

          OvulamMathRenderers.drawParabola(x1, y1, v.x, v.y, Mathf.absin(Time.time + 20 * Mathf.pi, 10, j * 0.6f), 0);

          index++;
        }
      }
    });
  }

  @Override
  public Ability copy(){
    AmphiblastulaDrawer drawer = (AmphiblastulaDrawer)super.copy();
    drawer.flagellums = new Seq<>();
    return drawer;
  }
}
