package LLL.world.blocks.distribution;

import LLL.ctype.packet.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.module.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ui.*;
import universecore.annotations.*;

public class PacketConveyorSorter extends PacketConveyor{
  public PacketConveyorSorter(String name){
    super(name);
    outputTop = false;

    saveConfig = true;
    noUpdateDisabled = true;

    clearOnDoubleTap = true;
    configurable = true;

    configClear((PacketConveyorSorterBuild tile) -> {
      tile.setSelected(null);
      tile.selectedResources().clear();
    });
  }

  @Annotations.ImplEntries
  public class PacketConveyorSorterBuild extends PacketConveyorBuild implements PacketSelectionTableBuildModule{
    public boolean whiteList = true;

    @Override
    public void buildPacketSelection(Table table){
      table.table(t -> {
        t.button("白名单模式", Styles.clearTogglet, () -> {
          whiteList = !whiteList;
        }).checked(ib -> whiteList).growX().height(40);
      }).growX().row();

      PacketSelectionTableBuildModule.super.buildPacketSelection(table);
    }

    public boolean conformTo(PacketEntry packetEntry){
      if(packetEntry.packet.packetType() != getSelected()){
        return false;
      }else if(!selectedResources().isEmpty()){
        Seq<ResourceStack<?>> resources = packetEntry.packet.resources();

        for(ResourceStack<?> resource : selectedResources()){
          if(!resources.contains(r -> r.item == resource.item && r.amount >= resource.amount)){
            return false;
          }
        }
      }

      return true;
    }

    @Override
    public void setPacketTarget(PacketEntry packetEntry, Vec2 targetToSet){
      boolean rightBlends = hasBlends[1];
      boolean leftBlends = hasBlends[3];

      if(conformTo(packetEntry) == whiteList){//符合条件的封包直行
        packetTarget(packetEntry, targetToSet, true, false);
      }else if(rightBlends && leftBlends){//两端均开口时选择自身最近的侧边
        packetTarget(packetEntry, targetToSet, false, getQuadrant(packetEntry) < 2);
      }else if(rightBlends || leftBlends){//仅一段开口时选择开口的侧边
        packetTarget(packetEntry, targetToSet, false, rightBlends);
      }else{//无开口均直行
        packetTarget(packetEntry, targetToSet, true, false);
      }
    }
  }
}
