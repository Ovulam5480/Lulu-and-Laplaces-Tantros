package LLL.world.blocks.effect;

import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class BlastCylinder extends Block{
  public float damage = 50;
  public float range = 32;

  public BlastCylinder(String name){
    super(name);
    update = true;
    configurable = true;
    rebuildable = false;
    category = Category.effect;
    buildVisibility = BuildVisibility.shown;
  }

  public class BlastCylinderBuild extends Building{
    @Override
    public boolean configTapped(){
      kill();
      return true;
    }

    @Override
    public void kill(){
      Damage.damage(x, y, range, damage);
      super.kill();
    }
  }
}
