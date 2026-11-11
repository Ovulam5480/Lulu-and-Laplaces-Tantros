package lulu.world.blocks.defense;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import lulu.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.*;

public class Mortar extends PowerTurret{
  public TextureRegion arrowRegion, topRegion, topLightRegion, iconRegion, podRegion, podIconRegion, podThrustersRegion;

  public int podSize = 8;
  public float range = 9999f;

  public MortarBulletType pod = new MortarBulletType("mortar", range){{
    damage = 1000f;
    lifetime = 600f;
    speed = 1f;
    offsideMultiplier = 4f;
    shadowOffsideMultiplier = 2f;
    hasIcon = true;
    hasThrusters = true;
    hitSize = Mathf.sqr(podSize);
  }};

  public Mortar(String name){
    super(name);
    size = 15;
    rotate = true;
    shootType = pod;
  }

  @Override
  public void load(){
    super.load();
    arrowRegion = Core.atlas.find(name + "-arrow");
    topRegion = Core.atlas.find(name + "-top");
    topLightRegion = Core.atlas.find(name + "-top-light");
    iconRegion = Core.atlas.find(name + "-icon");
    podRegion = Core.atlas.find(name + "-pod");
    podIconRegion = Core.atlas.find(name + "-pod-icon");
    podThrustersRegion = Core.atlas.find(name + "-pod-thrusters");
  }

  @Override
  public TextureRegion[] icons(){
    return new TextureRegion[]{iconRegion};
  }

  public class MortarBuild extends PowerTurretBuild{
    @Override
    public void draw(){
      Draw.z(Layer.blockUnder + 1f);
      Draw.rect(region, x, y);

      Draw.z(Layer.block);
      Draw.rect(podRegion, x, y);
      Draw.rect(podIconRegion, x, y);

      Draw.z(Layer.turret);
      Draw.rect(arrowRegion, x, y, rotation);
      Draw.z(Layer.blockOver);
      Draw.rect(topRegion, x, y, rotation);
      Draw.z(Layer.blockOver + 0.2f);
      Draw.rect(topLightRegion, x, y, rotation);
      Draw.z(Layer.blockOver + 0.1f);
      Draw.blend();
    }
  }
}
