package LLL.lib.singularity.ui.fragments.entityinfo;

import LLL.lib.singularity.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.game.*;
import mindustry.gen.*;

public class EntityHealthDisplay<T extends Teamc & Healthc> extends EntityInfoDisplay<T>{
  float tmp;

  @Override
  public float draw(EntityInfoFrag.EntityEntry<T> entry, Team team, float maxWight, float dy, float alpha, float scl){
    T entity = entry.entity;

    Draw.alpha(alpha);

    tmp = Sgl.config.healthBarStyle.draw(
      entity.x(), entity.y() + dy,
      entry,
      team,
      dy,
      alpha,
      scl
    );

    Draw.reset();

    return tmp;
  }

  @Override
  public void updateVar(EntityInfoFrag.EntityEntry<T> entry, float delta){
    entry.handleVar("over", (float f) -> Mathf.lerp(f, entry.entity.health(), delta * 0.04f), entry.entity.health());
  }

  @Override
  public float wight(float scl){
    return Sgl.config.healthBarStyle.getWidth(scl);
  }
}
