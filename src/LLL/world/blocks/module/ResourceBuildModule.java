package LLL.world.blocks.module;

import LLL.packet.ResourceStacks.*;
import LLL.world.blockcomp.*;
import LLL.world.modules.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

public interface ResourceBuildModule extends BuildCompBase{
  @Annotations.BindField("resources")
  default ResourceModule resources(){
    return null;
  }
    @Annotations.BindField("resources")
    default void setResources (ResourceModule resourceModule){
    }

    default float getResourceFunc (ResourceStack < ?, ?, ?>stack){
      return switch(stack.id()){
        case 0 -> stack.amount;
        case 1 -> stack.amount / 6;
        case 2 -> stack.amount * getPayloadOccupy((UnlockableContent)stack.item);
        case 3 -> stack.amount / 60;
        default -> 99999999;
      };
    }

    default
    float getPayloadOccupy (UnlockableContent content){
      if(content instanceof Block b){
        return b.health / 40f;
      }else if(content instanceof UnitType u){
        return u.hitSize / 40f;
      }else{
        return 99999999;
      }
    }

    default boolean acceptResourceStack (ResourceStack < ?, ?, ?>stack){
      return true;
    }

    default int maxResourceStack (ResourceStack < ?, ?, ?>stack){
      return 0;
    }

    default void handleResourceStack (ResourceStack < ?, ?, ?>stack){
      resources().add(stack);
    }

  }
