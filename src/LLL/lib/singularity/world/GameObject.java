package LLL.lib.singularity.world;

import universecore.annotations.*;

public interface GameObject extends Transform{
  @Annotations.BindField("entityID")
  default int getID(){
    return 0;
  }

  @Annotations.BindField("entityID")
  default void setID(int index){
  }

  void update();
}
