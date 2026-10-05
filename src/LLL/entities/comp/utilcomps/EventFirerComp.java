package LLL.entities.comp.utilcomps;

import LLL.content.*;
import LLL.entities.gen.*;
import arc.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityDef(EventFirerc.class)
@Annotations.EntityComponent
abstract class EventFirerComp implements Entityc{
  @Override
  public void afterReadAll(){
    Events.fire(new OvulamEventFirer.AfterReadAllEvent());
  }
}
