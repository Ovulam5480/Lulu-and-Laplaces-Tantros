package lulu.world.components.buildComps;

import lulu.ctype.packet.*;
import lulu.type.resourceStacks.*;
import lulu.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import universecore.annotations.*;

public interface PacketSelectionTableBuildComp extends BuildCompBase{
  @Annotations.BindField(value = "selectedResources", initialize = "new arc.struct.Seq<>()")
  default Seq<ResourceStack<?>> selectedResources(){
    return null;
  }

  @Annotations.BindField("selectedResources")
  default void selectedResources(Seq<ResourceStack<?>> value){
  }

  @Annotations.BindField(value = "selectionTable", initialize = "new lulu.ui.PacketSelectionTable()")
  default PacketSelectionTable selectionTable(){
    return null;
  }

  @Annotations.BindField("selectionTable")
  default void selectionTable(PacketSelectionTable value){
  }

  @Annotations.BindField(value = "selectionPacketType", initialize = "new arc.struct.Seq<>(lulu.ctype.packet.PacketType.class)")
  default Seq<PacketType> selectionPacketType(){
    return null;
  }

  @Annotations.BindField("selectionPacketType")
  default void selectionPacketType(Seq<PacketType> value){
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
