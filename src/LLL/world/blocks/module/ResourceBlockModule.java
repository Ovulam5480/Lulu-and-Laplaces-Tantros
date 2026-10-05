package LLL.world.blocks.module;

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

//  default ConsumePower consumeResourcePower(float powerPerTick, float capacity){
//    return block().consume(new ConsumeResourcePower(powerPerTick, capacity));
//  }
//
//  default <T extends Building> ConsumePower consumeResourcePowerCond(float usage, float capacity, Boolf<T> cons){
//    return block().consume(new ConsumeResourcePowerCondition(usage, capacity, (Boolf<Building>)cons));
//  }
//
//  default <T extends Building> ConsumePower consumeResourcePowerDynamic(Floatf<T> usage, float capacity){
//    return block().consume(new ConsumeResourcePowerDynamic((Floatf<Building>)usage, capacity));
//  }
//
//  default <T extends Building> ConsumePower consumeResourcePowerDynamic(float displayed, Floatf<T> usage, float capacity){
//    return block().consume(new ConsumeResourcePowerDynamic(displayed, (Floatf<Building>)usage, capacity));
//  }

//  @Annotations.MethodEntry(entryMethod = "consumePower", paramTypes = "float -> powerPerTick")
//  default ConsumePower consumePowerResourceBlock(float powerPerTick){throw new RuntimeException("no use this method");}
//  @Annotations.MethodEntry(entryMethod = "consumePowerCond", paramTypes = {"float -> usage", "arc.func.Boolf -> cons"})
//  default <T extends Building> ConsumePower consumePowerCondResourceBlock(float usage, Boolf<T> cons){throw new RuntimeException("no use this method");}
//  @Annotations.MethodEntry(entryMethod = "consumePowerDynamic", paramTypes = {"arc.func.Floatf -> usage"})
//  default <T extends Building> ConsumePower consumePowerDynamicResourceBlock(Floatf<T> usage){throw new RuntimeException("no use this method");}
//  @Annotations.MethodEntry(entryMethod = "consumePowerDynamic", paramTypes = {"float -> displayed", "arc.func.Floatf -> usage"})
//  default <T extends Building> ConsumePower consumePowerDynamicResourceBlock(float displayed, Floatf<T> usage){throw new RuntimeException("no use this method");}
//  @Annotations.MethodEntry(entryMethod = "consumePowerBuffered", paramTypes = "float -> powerCapacity")
//  default ConsumePower consumePowerBufferedResourceBlock(float powerCapacity){throw new RuntimeException("no use this method");}

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
