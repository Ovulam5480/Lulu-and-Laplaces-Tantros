package LLL.world.neoplasm.pedigree.porifera;

import LLL.content.*;
import LLL.graphics.*;
import LLL.lib.singularity.graphic.*;
import LLL.util.func.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.units.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@Annotations.ImplEntries
public class NeoplasmTriaxTurretBase extends NeoplasmMetamorphicCyst implements NeoplasmBlockModule{
  public Block blockType = Blocks.duo;
  public UnitType blockUnitType = OvulamUnitTypes.neoplasmTraixBuildUnit;
  public int maxUnits = 5;
  public float buildTimeMulti = 1f;
  public float moveSpeed = 0.01f;
  public float orbitRadius = 80f;
  public float override = 1f;

  public boolean drawBase = true;

  public Color color = Pal.remove;

  public float approachRange = 10f;

  public IntCons<Vec2> posHandler = (i, v) -> v.set(0, orbitRadius).rotate(i * 360f / maxUnits);

  public NeoplasmTriaxTurretBase(String name){
    super(name);
    size = 3;
    degenerateTime = 60 * 60 * 5;
  }

  @Override
  public void init(){
    super.init();

    clipSize = Math.max(clipSize, orbitRadius + approachRange * 2 + blockType.size * 4 * 1.414f * 1.6f + 20f);
  }

  @Annotations.ImplEntries
  public class NeoplasmTriaxTurretBaseBuild extends NeoplasmMetamorphicCystBuild implements NeoplasmBuildModule{
    public IntMap<BlockUnitc> turrets = new IntMap<>();
    public float constructTime;
    public float overrideTime;
    public final float blockRadius = blockType.size * 4 * 1.414f * 1.4f;
    public float[] rands = new float[maxUnits * 4];
    public float[] scales = new float[maxUnits];

    private IntIntMap readKey;

    @Override
    public void draw(){
      if(drawBase) Draw.rect(baseRegion(), x, y);

      Draw.z(Layer.flyingUnit + 2);
      Draw.color(color);
      Tmp.c2.set(Draw.getColor()).a(0.2f);

      for(IntMap.Entry<BlockUnitc> entry : turrets){
        BlockUnitc unit = entry.value;
        float radius = blockRadius * scales[entry.key];

        float ux = unit.x();
        float uy = unit.y();

        Draw.alpha(1);
        SglDraw.gradientPoly(ux, uy, Lines.circleVertices(radius) * 2, radius,
          Draw.getColor(), ux, uy, -radius * 0.3f, Tmp.c2, 0);
        SglDraw.gradientPoly(ux, uy, 8, radius * 0.001f,
          Draw.getColor(), ux, uy, radius * 0.2f, Tmp.c2, 0);

        Draw.alpha(0.2f);
        Fill.circle(ux, uy, radius * 0.7f);
      }

      Draw.draw(Layer.effect, () -> {
        Draw.color(color);
        for(IntMap.Entry<BlockUnitc> entry : turrets){
          int i = entry.key;
          BlockUnitc unit = entry.value;

          float ux = unit.x();
          float uy = unit.y();

          float len = Mathf.len(ux - x, uy - y);

          MathRenderer.setThreshold(0.03f, 0.03f);
          OvulamMathRenderers.drawParabola(x, y, ux, uy,
            Mathf.sin(rands[i * 4] * 20 + 20, Math.max(rands[i * 4 + 1] * Mathf.lerp(2f, 0f, len / orbitRadius) + 0.2f, 0.06f)),
            Mathf.sin(rands[i * 4 + 2] * 40 + 30, rands[i * 4 + 3] * 20f + 10f));
        }
      });
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(turrets.size < maxUnits){
        constructTime += delta() * buildTimeMulti;

        if(constructTime >= blockType.buildTime){
          Building building = blockType.newBuilding().create(blockType, team);
          building.proximity.clear();
          building.tile = emptyTile;
          building.checkAllowUpdate();
          building.created();

          BlockUnitc unit = (BlockUnitc)blockUnitType.create(team);
          unit.tile(building);
          unit.set(this);
          unit.add();

          for(int i = 0; i < maxUnits; i++){
            if(!turrets.containsKey(i)){
              turrets.put(i, unit);
              scales[i] = 0f;
              break;
            }
          }

          constructTime = 0;
        }
      }

      Vec2 tmp = Tmp.v1;
      Vec2 tmp2 = Tmp.v2;
      Vec2 tmp3 = Tmp.v3;

      for(IntMap.Entry<BlockUnitc> entry : turrets){
        int i = entry.key;
        BlockUnitc unitc = entry.value;

        if(unitc.dead()){
          turrets.remove(entry.key);
          continue;
        }

        scales[i] = Mathf.lerp(scales[i], 1f, 0.02f * delta());

        tmp2.set(unitc).sub(this);
        posHandler.get(i, tmp).approach(tmp2, approachRange);

        tmp3.set(tmp2).approach(tmp, moveSpeed).sub(tmp2);
        unitc.vel().add(tmp3).add(tmp.trns(Mathf.random(360), 0.004f));
      }

      overrideTime += delta();
      if(overrideTime > 60f){
        for(IntMap.Entry<BlockUnitc> entry : turrets){
          entry.value.tile().applyBoost(override, 60f);
        }
        overrideTime = 0;
      }
    }

    @Override
    public void remove(){
      super.remove();

      for(IntMap.Entry<BlockUnitc> entry : turrets){
        entry.value.kill();
      }
    }

    @Override
    public void created(){
      super.created();

      for(int i = 0; i < rands.length; i++){
        rands[i] = Mathf.random();
      }
    }

    @Override
    public void write(Writes write){
      super.write(write);

      write.f(constructTime);
      write.f(overrideTime);
      write.i(rands.length);
      for(float v : rands){
        write.f(v);
      }
      write.i(scales.length);
      for(float scale : scales){
        write.f(scale);
      }

      write.i(turrets.size);
      for(IntMap.Entry<BlockUnitc> turret : turrets){
        write.i(turret.value.id());
        write.i(turret.key);
      }
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);

      constructTime = read.f();
      overrideTime = read.f();
      rands = new float[read.i()];
      for(int i = 0; i < rands.length; i++){
        rands[i] = read.f();
      }
      scales = new float[read.i()];
      for(int i = 0; i < scales.length; i++){
        scales[i] = read.f();
      }

      readKey = new IntIntMap();
      int size = read.i();
      for(int i = 0; i < size; i++){
        int key = read.i();
        int value = read.i();
        readKey.put(key, value);
      }
    }

    @Override
    public void addToWrite(Seq<Entityc> toWrite){
      for(BlockUnitc value : turrets.values()){
        toWrite.add(value);
      }
    }

    @Override
    public void getFromRead(Queue<Entityc> toRead){
      for(int i = 0; i < readKey.size; i++){
        BlockUnitc unit = (BlockUnitc)toRead.first();

        turrets.put(readKey.get(unit.id()), unit);
        toRead.removeFirst();
      }
    }
  }
}
