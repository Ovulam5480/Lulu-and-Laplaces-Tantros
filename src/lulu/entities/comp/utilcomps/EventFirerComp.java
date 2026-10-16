package lulu.entities.comp.utilcomps;

import lulu.content.*;
import lulu.entities.gen.*;
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
