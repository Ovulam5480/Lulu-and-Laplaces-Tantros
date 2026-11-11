package lulu.world.blocks.production;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static arc.util.Time.*;
import static mindustry.Vars.*;

public class PayloadDrill extends Block{
  public Block oreBlock;
  public float glowScl = 3f;
  public float pulseIntensity = 0.07f;
  public float laserWidth = 0.45f;
  public float laserDrillRadius = 11f;

  public TextureRegion region, iconRegion;
  public TextureRegion topRegion;
  public TextureRegion shadowRegion;
  public TextureRegion laserDrillRegion;
  public TextureRegion laser;
  public TextureRegion laserEnd;
  public TextureRegion laserCenter;

  public boolean hasTop = true;

  public PayloadDrill(String name){
    super(name);
    update = false;
    hasShadow = false;
    rotate = true;
    rotateDraw = false;
  }

  @Override
  public void load(){
    super.load();
    region = Core.atlas.find(name);
    iconRegion = Core.atlas.find(name + "-icon");
    if(hasTop) topRegion = Core.atlas.find(name + "-top");
    shadowRegion = Core.atlas.find(name + "-shadow");
    laserDrillRegion = Core.atlas.find(name + "-laserDrill");
    laser = Core.atlas.find("laser");
    laserEnd = Core.atlas.find("laser-end");
    laserCenter = Core.atlas.find("laser-center");
  }

  @Override
  public TextureRegion[] icons(){
    return new TextureRegion[]{iconRegion};
  }

  public class PayloadDrillBuild extends Building{
    float[] wangle = new float[4];

    //绘制用进度: 当前恒为 1 (oreBlock 始终"已建成"); 后续接入生产逻辑后可改为真实进度
    public float progress(){
      return 1f;
    }

    @Override
    public void draw(){
      Drawf.shadow(shadowRegion, x, y);
      Draw.rect(region, x, y);

      Drawf.shadow(x, y, oreBlock.size * tilesize * 2f, progress());
      Draw.draw(Layer.blockBuilding, () -> {
        Draw.color(Pal.accent);

        for(TextureRegion region : oreBlock.getGeneratedIcons()){
          Shaders.blockbuild.region = region;
          Shaders.blockbuild.time = time;
          Shaders.blockbuild.progress = progress();

          Draw.rect(region, x, y, oreBlock.rotate ? rotdeg() : 0);
          Draw.flush();
        }
        Draw.color();
      });
      Draw.z(Layer.blockBuilding + 1);
      Draw.color(Pal.accent);

      Lines.lineAngleCenter(x + Mathf.sin(time, 10f, Vars.tilesize / 2f * oreBlock.size + 1f), y, 90, oreBlock.size * Vars.tilesize + 1f);

      Draw.reset();

      Draw.z(Layer.blockBuilding + 1.1f);
      if(hasTop) Draw.rect(topRegion, x, y);
      int trns = oreBlock.size * tilesize / 2;
      //激光汇聚目标点 (progress=1 时收敛于中心)
      float lx = progress() * trns * Mathf.sin(progress() * 6 * 3.14f) + x;
      float ly = progress() * trns * Mathf.cos(progress() * 6 * 3.14f) + y;
      float width = laserWidth + Mathf.absin(time + 5 + (id % 9) * 9, glowScl, pulseIntensity);
      for(int i = 0; i < 4; i++){
        float px = x + Geometry.d8edge(i).x * laserDrillRadius;
        float py = y + Geometry.d8edge(i).y * laserDrillRadius;
        float angle = (float)Math.toDegrees(Math.atan((ly - py) / (lx - px))) + 90 * Geometry.d8edge(i).x;

        wangle[i] = Mathf.lerpDelta(wangle[i], angle, 0.05f);
        Draw.rect(laserDrillRegion, px, py, wangle[i]);
        float p2x = px + Mathf.cos((float)Math.toRadians(wangle[i] + 90)) * laserDrillRegion.height / 8;
        float p2y = py + Mathf.sin((float)Math.toRadians(wangle[i] + 90)) * laserDrillRegion.height / 8;
        Drawf.laser(laser, laserEnd, p2x, p2y, lx, ly, width);
      }
    }
  }
}
