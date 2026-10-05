package LLL.entities.comp.utilcomps;

import LLL.entities.gen.*;
import LLL.graphics.*;
import arc.graphics.*;
import ent.anno.*;
import mindustry.entities.*;
import mindustry.gen.*;

@Annotations.EntityDef(value = OvulamEffectStatec.class, pooled = true, serialize = false)
@Annotations.EntityComponent
abstract class OvulamEffectStateComp implements EffectStatec{
  @Annotations.Import
  Effect effect;
  @Annotations.Import
  float time, lifetime, rotation, x, y;
  @Annotations.Import
  int id;
  @Annotations.Import
  Color color;
  @Annotations.Import
  Object data;

  public OvulamEffect effectAs(){
    return (OvulamEffect)effect;
  }

  @Override
  public void update(){
    effectAs().update(id, color, time, lifetime, rotation, x, y, data);
    effectAs().runnables.each(Runnable::run);
  }

  @Override
  public void add(){
    effectAs().update(id, color, 0, lifetime, rotation, x, y, data);
    effectAs().start.each(Runnable::run);
  }

  @Override
  public void remove(){
    effectAs().end.each(Runnable::run);
  }
}
