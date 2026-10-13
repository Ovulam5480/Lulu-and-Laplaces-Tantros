package LLL.world.blocks.distribution;

import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.util.*;
import LLL.world.blocks.packet.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;

public class PacketCompositeConveyor extends PacketBlock{
  public static PacketEntryGridMap grid = new PacketEntryGridMap();

  public PacketCompositeConveyor(String name){
    super(name);
  }

  @Override
  public void init(){
    super.init();
  }

  public class PacketCompositeConveyorBuild extends PacketBlockBuild{
    public ObjectMap<PacketEntry, Integer> horizontalPacket = new ObjectMap<>();
    public Seq<PacketEntry> removes = new Seq<>();

    @Override
    public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
      return super.acceptPacket(packetEntry, source) && grid.getAtEntry(packetEntry) == null;
    }

    @Override
    public void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
      PacketTransporterc sourceEntity = source.get();

      int column;
      if(sourceEntity instanceof PacketBlockBuild build){
        int dir = getDirection(packetEntry.packet);
        if(dir % 2 == 1){
          int sourceSize = build.block.size;

          Vec2 vec = Tmp.v1.set(sourceEntity).sub(this).rotate(-rotation * 90);
          int drot = Mathf.floor((vec.angle() + 45) % 360 / 90);
          int difference = Mathf.round((drot % 2 == 1 ? vec.x : vec.y) * Mathf.sign(drot % 2 != 0));
          int rx = Math.min(size * 8, difference + (size + sourceSize) * 8 / 2) / 8;

          Vec2 vecP = Tmp.v2.set(packetEntry.packet).sub(this).rotate(-rotation * 90);
          int px = Mathf.clamp(Mathf.floor(vecP.x / 8f + size / 2f), 0, size - 1);

          column = dir / 2 == 0 ? rx - px - 1 : size - (rx - px);
          horizontalPacket.put(packetEntry, column);
        }else{
          column = blockPosToColumn(rotation % 2 == 1 ? packetEntry.getX() - x : packetEntry.getY() - y);
          packets.add(packetEntry);
        }
      }else{ //todo
        column = blockPosToColumn(rotation % 2 == 1 ? packetEntry.getX() - x : packetEntry.getY() - y);
        packets.add(packetEntry);
      }

      packetEntry.targetPos.set(columnToBlockPos(column), radius()).rotate((rotation - 1) * 90);
      packetEntry.targetPos.add(this);
      grid.addAtEntry(packetEntry);
    }

    @Override
    public void updateTile(){
      super.updateTile();

      horizontalPacket.each((packet, column) -> {
        if(updatePacketGrid(packet)){
          Vec2 vel = packet.packet.vel();

          float current = Tmp.v1.set(packet).sub(this).rotate((1 - rotation) * 90).x;
          float target = columnToBlockPos(column);

          if(Mathf.equal(current, target, 0.05f)){
            vel.setZero();
            removes.add(packet);
            packets.add(packet);
          }else{
            int sign = Mathf.sign(target > current);

            //该传送带中速度直接set而非lerp
            float velx = Mathf.clamp(Math.abs(current - target), 0, packetSpeed(packet)) * sign;

            vel.set(velx, 0).rotate((rotation - 1) * 90);
          }
        }else if(packet.packet.vel().isZero()){
          //todo 横向移动被阻挡时, 直接在当前列纵向移动
          //todo 可能存在崩溃问题, 待测试
          //horizontalPacket.put(packet, column);
        }
      });

      removes.each(p -> horizontalPacket.remove(p));
      removes.clear();
    }

    @Override
    public void updatePacketEntry(PacketEntry packetEntry){
      if(updatePacketGrid(packetEntry)){
        movePacket(packetEntry);
      }

      Packetc packet = packetEntry.packet;
      if(rotation % 2 == 0){
        packetEntry.packet.y(Mathf.round(packet.getY() / 8) * 8);
      }else{
        packetEntry.packet.x(Mathf.round(packet.getX() / 8) * 8);
      }

      if(dumpEdge){
        outputEdgePackets(packetEntry);
      }
    }

    @Override
    public void movePacket(PacketEntry packetEntry){
      Packetc packet = packetEntry.packet;
      float current = Tmp.v1.set(packetEntry.targetPos).sub(packet).rotate((1 - rotation) * 90).y;

      float vely = Mathf.clamp(current + 0.01f, 0, packetSpeed(packetEntry));

      packet.vel().set(vely, 0).rotate(rotation * 90);
    }

    public boolean updatePacketGrid(PacketEntry packet){
      int ceilSize = Mathf.ceil(packet.packet.packetType().size);

      //检测是否被其他packet占用
      Vec2 vel = Tmp.v1.set(packet.packet.vel()).setLength(packet.packet.hitSize() / 2f + 0.1f);
      PacketEntry target = grid.get(Mathf.round(packet.getX() + vel.x), Mathf.round(packet.getY() + vel.y));

      if(target != null && target != packet){
        packet.packet.set(Tmp.v1.set(packet.packet.vel()).scl(delta() * -1.1f).add(packet.packet));
        packet.packet.vel().setZero();
        return false;
      }

      //检查封包是否可能到达新的网格
      boolean shouldReplace = false;

      int centerX = Mathf.round(packet.getX() / 8);
      int centerY = Mathf.round(packet.getY() / 8);

      if(ceilSize == 1 && grid.get(centerX, centerY) == null){
        shouldReplace = true;
      }else{
        int startX;
        int startY;

        if(ceilSize % 2 == 0){
          startX = centerX - ceilSize / 2;
          startY = centerY - ceilSize / 2;
          if(packet.getX() / 8 - centerX > 0) startX++;
          if(packet.getY() / 8 - centerY > 0) startY++;
        }else{
          startX = centerX - (ceilSize - 1) / 2;
          startY = centerY - (ceilSize - 1) / 2;
        }

        if(grid.get(startX, startY) == null
          || grid.get(startX + ceilSize - 1, startY) == null
          || grid.get(startX, startY + ceilSize - 1) == null
          || grid.get(startX + ceilSize - 1, startY + ceilSize - 1) == null){
          shouldReplace = true;
        }
      }

      if(shouldReplace){
        float range = (ceilSize + 1) / 2f;

        grid.foreachAtEntry(packet, ceilSize + 1, hash -> {
          Point2 point2 = PacketEntryGridMap.hashToPoint2(hash);
          Fx.healBlock.at(point2.x * 8, point2.y * 8, 1);

          PacketEntry entry = grid.get(hash);

          boolean in = Math.max(Math.abs(point2.x - (centerX)), Math.abs(point2.y - (centerY))) < range;

          if(in){
            if(entry == null) grid.add(hash, packet);
          }else{
            if(entry != null) grid.remove(hash);
          }
        });

        return false;
      }

      return true;
    }

    @Override
    public void transferPacket(PacketEntry packetEntry, PacketTransportercProv to){
      grid.removeAtEntry(packetEntry);
      super.transferPacket(packetEntry, to);
    }

    @Override
    public void removePacketEntry(PacketEntry packetEntry){
      grid.removeAtEntry(packetEntry);
      super.removePacketEntry(packetEntry);
    }

    public void eachGrid(Cons3<Integer, Integer, PacketEntry> cons){
      for(int x = 0; x < size; x++){
        for(int y = 0; y < size; y++){
          cons.get(x, y, grid.get(x, y));
        }
      }
    }

    public float columnToBlockPos(float column){
      return (column + 0.5f) * 8 - radius();
    }

    public int blockPosToColumn(float blockPos){
      return Mathf.floor((blockPos + radius()) / 8);
    }

    public Vec2 gridToWorldPos(int gx, int gy){
      return Tmp.v1.set(gx, size - 1 - gy)
        .scl(8)
        .add(4, 4)
        .sub(radius(), radius())
        .rotate(rotdeg() - 90)
        .add(x(), y());
    }

    public int radius(){
      return size * 4;
    }

    public int tileRadius(){
      return radius() - 4;
    }
  }
}
