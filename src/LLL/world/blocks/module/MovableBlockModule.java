package LLL.world.blocks.module;

import LLL.*;
import arc.*;
import arc.graphics.g2d.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

@SuppressWarnings("unused")
public interface MovableBlockModule{
  //todo setState

  default Block getBlock(){
    return (Block)this;
  }

  @Annotations.BindField(value = "drawThrusters", initialize = "true")
  default boolean drawThrusters(){
    return false;
  }

  @Annotations.BindField("movableBlockType")
  default UnitType movableBlockType(){
    return null;
  }

  @Annotations.BindField("thruster1")
  default TextureRegion thruster1(){
    return null;
  }

  @Annotations.BindField("thruster1")
  default void setThruster1(TextureRegion thruster1){
  }

  @Annotations.BindField("thruster2")
  default TextureRegion thruster2(){
    return null;
  }

  @Annotations.BindField("thruster2")
  default void setThruster2(TextureRegion thruster2){
  }

  default void loadThrusters(){
    if(drawThrusters()){
      setThruster1(Core.atlas.has(getBlock().name + "-thruster1") ? Core.atlas.find(getBlock().name + "-thruster1") : Core.atlas.find(LuluMod.modName + "thruster1"));
      setThruster2(Core.atlas.has(getBlock().name + "-thruster2") ? Core.atlas.find(getBlock().name + "-thruster2") : Core.atlas.find(LuluMod.modName + "thruster2"));
    }
  }
}
