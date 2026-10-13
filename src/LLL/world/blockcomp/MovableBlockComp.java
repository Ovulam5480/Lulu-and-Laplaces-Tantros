package LLL.world.blockcomp;

import LLL.*;
import arc.*;
import arc.graphics.g2d.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

@SuppressWarnings("unused")
public interface MovableBlockComp{
  //todo setState

  default Block getBlock(){
    return (Block)this;
  }

  @Annotations.BindField(value = "drawThrusters", initialize = "true")
  default boolean drawThrusters(){
    return false;
  }

  @Annotations.BindField("drawThrusters")
  default void drawThrusters(boolean value){
  }

  @Annotations.BindField("movableBlockType")
  default UnitType movableBlockType(){
    return null;
  }

  @Annotations.BindField("movableBlockType")
  default void movableBlockType(UnitType value){
  }

  @Annotations.BindField("thruster1")
  default TextureRegion thruster1(){
    return null;
  }

  @Annotations.BindField("thruster1")
  default void thruster1(TextureRegion thruster1){
  }

  @Annotations.BindField("thruster2")
  default TextureRegion thruster2(){
    return null;
  }

  @Annotations.BindField("thruster2")
  default void thruster2(TextureRegion thruster2){
  }

  default void loadThrusters(){
    if(drawThrusters()){
      thruster1(Core.atlas.has(getBlock().name + "-thruster1") ? Core.atlas.find(getBlock().name + "-thruster1") : Core.atlas.find(LuluMod.modName + "thruster1"));
      thruster2(Core.atlas.has(getBlock().name + "-thruster2") ? Core.atlas.find(getBlock().name + "-thruster2") : Core.atlas.find(LuluMod.modName + "thruster2"));
    }
  }
}
