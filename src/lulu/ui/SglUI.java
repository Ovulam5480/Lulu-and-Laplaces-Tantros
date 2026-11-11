package lulu.ui;

import arc.*;
import arc.util.*;
import lulu.graphics.renderer.*;
import mindustry.*;
import mindustry.game.*;

public class SglUI{
  public NotificationFrag notificationFrag;

  public static Blur uiBlur = new Blur(Blur.DEf_B);

  public void init(){
    notificationFrag = new NotificationFrag();
    notificationFrag.build(Vars.ui.hudGroup);
    Vars.ui.hints.build(Vars.ui.hudGroup);

    configEventListeners();
  }

  void configEventListeners(){
    Events.on(EventType.WorldLoadEndEvent.class, e -> {
      for(int i = 0; i < 10; i++){
        int fi = i;
        Time.run(60 + 30 * i, () -> {
          notificationFrag.notify(
            new Notification.Warning(
              "notification test " + fi,
              "notification message"
            )
          );
        });
      }
    });
  }
}
