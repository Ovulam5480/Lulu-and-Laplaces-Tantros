package LLL.entities.types;

import LLL.entities.gen.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;

public class RotationalCubeUnitType extends UnitType{
  private final static int[][] index = {{7, 5, 1, 3}, {5, 4, 0, 1}, {7, 6, 4, 5}, {6, 2, 0, 4}, {7, 3, 2, 6}, {3, 1, 0, 2}};
  public float cubeRadius;
  public float rotationMulti = 1f;
  public Effect effect = new Effect(120f, e -> {
    Draw.color(Color.valueOf("6ff4e2"), e.fout());
    Fill.square(e.x, e.y, e.rotation / 2 * (0.8f + 0.2f * e.foutpow()));
    Draw.reset();
  });

  public float cameraZ = 8f;

  public boolean drawBottom;
  public boolean drawCenter;
  public float cTile;

  public boolean randomRoll = true;

  public boolean deceiveAccurateDelay;
  public float deceiveMulti;

  public RotationalCubeUnitType(String name){
    super(name);
    drawCell = false;
    engineSize = 0f;
    wobble = false;
  }

  @Override
  public void init(){
    super.init();
    if(cubeRadius == 0) cubeRadius = hitSize / 2f;
    if(cTile == 0) cTile = hitSize;
  }

  @Override
  public void drawBody(Unit unit){
    if(!(unit instanceof RotationalCubeUnitc r)) return;
    applyColor(unit);

    drawCube(r.vec3s(), unit, cubeRadius, region, drawCenter);
  }

  public void drawCube(Seq<Vec3> vec3s, Unit unit, float radius, TextureRegion region, boolean applyCenter){
    ObjectMap<Float, Integer> indexs = new ObjectMap<>();

    for(int i = 0; i < 6; i++){
      float z = 0;

      for(int j = 0; j < 4; j++){
        z += vec3s.get(index[i][j]).z;
      }

      if(drawBottom || z > 0) indexs.put(z, i);
    }

    Seq<Float> ins = indexs.keys().toSeq().sort();

    for(int i = 0; i < indexs.size; i++){
      int[] ints = index[indexs.get(ins.get(i))];

      Fill.quad(region,
        convert(vec3s.get(ints[0]).z, vec3s.get(ints[0]).x, radius) + unit.x,
        convert(vec3s.get(ints[0]).z, vec3s.get(ints[0]).y, radius) + unit.y,
        convert(vec3s.get(ints[1]).z, vec3s.get(ints[1]).x, radius) + unit.x,
        convert(vec3s.get(ints[1]).z, vec3s.get(ints[1]).y, radius) + unit.y,
        convert(vec3s.get(ints[2]).z, vec3s.get(ints[2]).x, radius) + unit.x,
        convert(vec3s.get(ints[2]).z, vec3s.get(ints[2]).y, radius) + unit.y,
        convert(vec3s.get(ints[3]).z, vec3s.get(ints[3]).x, radius) + unit.x,
        convert(vec3s.get(ints[3]).z, vec3s.get(ints[3]).y, radius) + unit.y);

      if(applyCenter && i == 2) drawCenter(unit);
    }
  }

  public void drawCenter(Unit unit){
  }


  public float convert(float z, float c, float scl){
    return (cameraZ / (cameraZ - z / cubeRadius)) * c * scl;
  }

  //todo
  @Override
  public void drawShadow(Unit unit){
  }
}
