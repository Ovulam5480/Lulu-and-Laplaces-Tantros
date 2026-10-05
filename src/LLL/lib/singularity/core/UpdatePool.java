package LLL.lib.singularity.core;

import arc.*;
import arc.struct.*;
import mindustry.game.*;

public class UpdatePool{
  static{
    Events.run(EventType.Trigger.update, UpdatePool::update);
  }

  private static final ObjectMap<String, Runnable> updateTasks = new ObjectMap<>();

  public static void receive(String key, Runnable task){
    updateTasks.put(key, task);
  }

  public static boolean remove(String key){
    return updateTasks.remove(key) != null;
  }

  public static void update(){
    for(Runnable task : updateTasks.values()){
      task.run();
    }
  }
}
