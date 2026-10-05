package LLL.world.blocks.working;

import LLL.entities.gen.*;
import LLL.world.blocks.module.*;
import LLL.world.blocks.packet.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;
import universecore.annotations.*;

public class PacketSource extends PacketBlock{
  public float dropInterval = 5f;

  public PacketSource(String name){
    super(name);

    saveConfig = true;
    noUpdateDisabled = true;

    clearOnDoubleTap = true;
    configurable = true;

    configClear((PacketSourceBuild tile) -> {
      tile.setSelected(null);
      tile.selectedResources().clear();
    });
  }

  @Annotations.ImplEntries
  public class PacketSourceBuild extends PacketBlockBuild implements ControlBlock, PacketSelectionTableBuildModule{
    public @Nullable BlockUnitc unit;

    @Override
    public Unit unit(){
      if(unit == null){
        unit = (BlockUnitc)UnitTypes.block.create(team);
        unit.tile(this);
      }
      return (Unit)unit;
    }

    @Override
    public boolean shouldAutoTarget(){
      return false;
    }

    @Override
    public void updateTile(){
      if(unit != null && getSelected() != null && !selectedResources().isEmpty() && isControlled() && unit.isShooting() && timer(0, dropInterval)){

        Packetc packet = getSelected().create(null, unit.aimX(), unit.aimY());
        if(packet != null) packet.resources().addAll(selectedResources());
      }
    }
  }
}
