package LLL.lib.singularity.ui.fragments.entityinfo;

import mindustry.game.*;
import mindustry.gen.*;

public abstract class EntityInfoDisplay<T extends Entityc & Posc>{
  public abstract float draw(EntityInfoFrag.EntityEntry<T> entry, Team team, float maxWight, float dy, float alpha, float scl);

  public abstract void updateVar(EntityInfoFrag.EntityEntry<T> entry, float delta);

  public abstract float wight(float scl);
}
