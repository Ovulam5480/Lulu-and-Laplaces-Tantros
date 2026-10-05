package LLL.content;

import LLL.entities.gen.*;
import arc.*;
import mindustry.game.*;

public class OvulamEventFirer{
  private static EventFirer entity = EventFirer.create();

  public static void init(){
    Events.on(EventType.WorldLoadEvent.class, e -> {
      entity.add();
    });

    Events.on(AfterReadAllEvent.class, e -> {
      entity.remove();
    });
  }

  public static class AfterReadAllEvent{
  }
}
