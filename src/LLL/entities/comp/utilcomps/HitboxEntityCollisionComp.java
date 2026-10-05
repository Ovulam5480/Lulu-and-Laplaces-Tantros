package LLL.entities.comp.utilcomps;

import LLL.entities.*;
import ent.anno.*;
import mindustry.entities.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class HitboxEntityCollisionComp implements Velc, Hitboxc, Healthc{
  @Annotations.Import
  float x, y;

  @Annotations.Replace
  @Override
  public void move(float cx, float cy){
    EntityCollisions.SolidPred check = solidity();

    if(check != null){
      OvulamEntityCollisions.move(self(), cx, cy, check);
    }else{
      x += cx;
      y += cy;
    }
  }
}
