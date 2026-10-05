package LLL.world.blocks.packet;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.io.*;
import LLL.world.blocks.entity.*;
import LLL.world.blocks.module.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import universecore.annotations.*;

//todo 设定上允许封包自由进出?
public class PacketCollector extends PacketBlock{
  public float range = 60f;
  public float consumeTime = 300f;//todo

  public PacketCollector(String name){
    super(name);

    dumpEdge = true;
    rotate = true;
    acceptPacket = false;
    producePacketEntry = true;

    clearOnDoubleTap = true;
    configurable = true;

    configClear((PacketCollectorBuild tile) -> {
      tile.setSelected(null);
      tile.selectedResources().clear();
    });
  }

  @Override
  public void drawOverlay(float x, float y, int rotation){
    Drawf.circles(x, y, range);
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);

    LuluMod.oIndexer.tileEntity.each(c -> {
      if(c.block() instanceof CrystalCoreBlock ccb){
        ccb.drawOverlay(c.x(), c.y(), 0);
      }
    });
  }

  @Annotations.ImplEntries
  public class PacketCollectorBuild extends PacketBlockBuild implements PacketSelectionTableBuildModule{
    public @Nullable PacketEntry target;
    public OvulamTypeIO.PacketEntryBox targetBox;
    private int targetId;
    public float timer = 0f;

    @Override
    public float deltaTime(){
      return delta();
    }

    @Override
    public void handlePacketBoundary(PacketEntry packetEntry){
      if(packetEntry != target){
        super.handlePacketBoundary(packetEntry);
      }
    }

    @Override
    public PacketEntry getOutputPacket(){
      return packets.find(pe -> pe != target);
    }

    @Override
    public boolean shouldConsume(){
      return super.shouldConsume() && target != null;
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if((timer += delta()) > consumeTime){
        consume();
        timer = 0f;
      }

      if(target != null){
        //todo 距离过远断开?
        if(target.dst(this) < 1){
          Tmp.v1.set(hitSize(), hitSize()).scl(0.5f).scl(Geometry.d4x(rotation), Geometry.d4y(rotation)).add(this);
          target.setTargetPos(Tmp.v1);
          target = null;
        }
      }else if(packets.isEmpty()){
        Packetc packetc = LuluMod.oIndexer.entityPackets
          .intersect(x - range, y - range, range * 2, range * 2)
          .find(p -> p.owner() == null
            && p.isAdded()
            && p.dst(this) <= range
            && (getSelected() == null || getSelected() == p.packetType())
            && (selectedResources().isEmpty() || !selectedResources().contains(rs -> !p.resources().contains(r -> rs.item == r.item && r.amount >= rs.amount)))
          );

        if(packetc != null){
          packetc.owner(this);
          target = PacketEntry.create(packetc);
          target.setTargetPos(this);
          handlePacket(target, null);
        }
      }
    }

    @Override
    public void updatePacketEntry(PacketEntry packetEntry){
      if(packetEntry == target) movePacket(packetEntry);
      else super.updatePacketEntry(packetEntry);
    }

    @Override
    public void movePacket(PacketEntry packetEntry){
      Packetc packet = packetEntry.packet;
      Vec2 toVel = Tmp.v1.set(packetEntry.targetPos).sub(packet).setLength(Math.min(Tmp.v1.len(), packetSpeed(packetEntry)));
      packet.vel().lerp(toVel, packetAccel() * (packetEntry == target ? edelta() : deltaTime()));
    }

    @Override
    public void throwAllPackets(float speed){
      for(PacketEntry entry : packets){
        if(entry != target) throwPacket(entry, speed);
        else throwPacket(entry, 0);
      }
    }

    @Override
    public int overrideCollisionLayer(){
      return -1;
    }

    @Override
    public void write(Writes write){
      super.write(write);

      write.i(target != null ? target.packet.id() : -1);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);

      targetId = read.i();
    }

    @Override
    public void afterReadAll(){
      super.afterReadAll();

      if(targetId != -1){
        target = packets.find(pe -> pe.packet.id() == targetId);
      }
    }
  }
}
