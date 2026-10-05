package LLL.world.blocks.module;

import universecore.annotations.*;
import universecore.components.blockcomp.*;

//todo 待施工状态, 还未重写
public interface ResourceEffectiveBuildModule extends ResourceBuildModule, ConsumerBuildComp{
//  default ResourceEffectiveBlockModule getResourceEffectiveBlock(){
//    return (ResourceEffectiveBlockModule)getBlock();
//  }
//
//  @Annotations.MethodEntry(entryMethod = "acceptItem", paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Item -> item"}, override = true)
//  default boolean acceptItemResourceEffectiveBuild(Building source, Item item){
//    return getMaxAccepted(item) >= 1;
//  }
//
//  @Annotations.MethodEntry(entryMethod = "acceptStack", paramTypes = {"mindustry.type.Item -> item", "int -> amount", "mindustry.gen.Teamc -> source"}, override = true)
//  default int acceptStackResourceEffectiveBuild(Item item, int amount, Teamc source){
//    return Math.min(Mathf.ceil(getMaxAccepted(item)), amount);
//  }
//
//  @Annotations.MethodEntry(entryMethod = "acceptLiquid", paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Liquid -> liquid"}, override = true)
//  default boolean acceptLiquidResourceEffectiveBuild(Building source, Liquid liquid){
//    return true;
//  }
//
//  @Annotations.MethodEntry(entryMethod = "acceptPayload", paramTypes = {"mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"}, override = true)
//  default boolean acceptPayloadResourceEffectiveBuild(Building source, Payload payload){
//    boolean stackable;
//    if(payload instanceof UnitPayload u){
//      stackable = checkPayloadStackable(u.unit);
//    }else{
//      stackable = checkPayloadStackable(((BuildPayload)payload).build);
//    }
//
//    return stackable && getMaxAccepted(payload.content()) >= 1;
//  }
}
