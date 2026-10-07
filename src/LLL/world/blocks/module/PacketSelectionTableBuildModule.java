package LLL.world.blocks.module;

import LLL.packet.*;
import LLL.packet.ResourceStacks.*;
import LLL.ui.*;
import LLL.world.blockcomp.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import universecore.annotations.*;

public interface PacketSelectionTableBuildModule extends BuildCompBase{
  @Annotations.BindField(value = "selectedResources", initialize = "new arc.struct.Seq<>()")
  default Seq<ResourceStack<?, ?, ?>> selectedResources(){
    return null;
  }

  @Annotations.BindField(value = "selectionTable", initialize = "new LLL.ui.PacketSelectionTable()")
  default PacketSelectionTable selectionTable(){
    return null;
  }

  @Annotations.BindField(value = "selectionPacketType", initialize = "new arc.struct.Seq<>(LLL.packet.PacketType.class)")
  default Seq<PacketType> selectionPacketType(){
    return null;
  }

  default PacketType getSelected(){
    return selectionPacketType().items[0];
  }

  default void setSelected(PacketType type){
    if(type == null){
      selectionPacketType().clear();
    }else{
      selectionPacketType().items[0] = type;
    }
  }

  @Annotations.MethodEntry(entryMethod = "buildConfiguration", paramTypes = "arc.scene.ui.layout.Table -> table")
  default void buildPacketSelection(Table table){
    selectionTable().build(table, selectionPacketType().items, selectedResources());
  }

  //todo 读写
}
