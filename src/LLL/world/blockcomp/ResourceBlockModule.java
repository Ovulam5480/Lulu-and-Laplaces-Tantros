package LLL.world.blockcomp;

import LLL.world.buildComp.*;
import arc.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import universecore.annotations.*;

public interface ResourceBlockModule{
  default Block block(){
    return (Block)this;
  }

  @Annotations.BindField(value = "resourceCapacity", initialize = "100")
  default float resourceCapacity(){
    return 0;
  }

  @Annotations.BindField("resourceCapacity")
  default void setResourceCapacity(float capacity){
  }

  @Annotations.BindField("separateResource")
  default boolean separateResource(){
    return false;
  }

  @Annotations.BindField("separateResource")
  default void separateResource(boolean separate){
  }

  @Annotations.BindField("tempLiquidCapacity")
  default float tempLiquidCapacity(){
    return 0;
  }

  @Annotations.BindField("tempLiquidCapacity")
  default void setTempLiquidCapacity(float capacity){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initResourceBlock(){
    Block block = block();
    setTempLiquidCapacity(block.liquidCapacity);

    block.itemCapacity = (int)resourceCapacity();
    block.hasLiquids = true;
    block.acceptsPayload = true;
    block.configurable = true;
    block.clearOnDoubleTap = true;
    block.allowConfigInventory = false;

    Events.run(EventType.Trigger.afterGameUpdate, () -> {
      block.liquidCapacity = tempLiquidCapacity();
    });
  }

  @Annotations.MethodEntry(entryMethod = "setBars")
  default void setBarsResourceBlock(){
    Block block = (Block)this;

    if(!separateResource()){
      block.removeBar("items");
      block.removeBar("liquid");
      block.removeBar("power");
      block.removeBar("capacity");

      block.addBar("capacity", (Building e) -> {
        ResourceBuildModule module = (ResourceBuildModule)e;
        return new Bar(
          () -> Core.bundle.format("bar.capacity", UI.formatAmount((long)resourceCapacity())),
          () -> Pal.items,
          () -> module.resources().sum(module::getResourceOccupy) / resourceCapacity()
        );
      });
    }
  }
}
