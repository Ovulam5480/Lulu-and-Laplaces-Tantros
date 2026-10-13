package LLL.world.blocks.distribution;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.world.blocks.packet.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

//封包连接器, 远程输入输出, 与中继器相比存在限制, 但是可以连接普通建筑
public class PacketConnecter extends PacketBlock{
  //运输的资源类型, null表示任意
  public @Nullable Class<?> resourceClass;
  public float packetDumpTime = 90f;
  public int packetCapacity = 10;
  public float range = 80f;
  public TextureRegion inputRegion, outputRegion;

  public int maxConnect = 4;
  public float pointRadius = 2f;
  public float roundRadius = 24f;
  public float roundSpeedMulti = 3f;
  public Color pointColor = Pal.accent;

  public PacketConnecter(String name){
    super(name);

    configurable = true;
    rotate = false;
    dumpEdge = false;
  }

  @Override
  public void load(){
    super.load();

    inputRegion = Core.atlas.find(LuluMod.modName + "connect-input");
    outputRegion = Core.atlas.find(LuluMod.modName + "connect-output");
  }

  @Override
  public void drawOverlay(float x, float y, int rotation){
    Drawf.circles(x, y, range);
  }

  public class PacketConnecterBuild extends PacketBlockBuild{
    Seq<PacketTransporterc> froms = new Seq<>();
    Seq<PacketTransporterc> tos = new Seq<>();
    OrderedMap<PacketEntry, PacketTransporterc> dumpPackets = new OrderedMap<>();
    float handleTimer, dumpTimer;
    float roundTimer;

    private final Seq<PacketEntry> toTrans = new Seq<>();

    @Override
    public void drawConfigure(){
      super.drawConfigure();
      drawSelect();
    }

    @Override
    public void draw(){
      super.draw();

      for(PacketTransporterc from : froms){
        Lines.line(inputRegion, x, y, from.x(), from.y(), true);
      }

      for(PacketTransporterc to : tos){
        Lines.line(outputRegion, x, y, to.x(), to.y(), true);
      }
    }

    @Override
    public boolean onConfigureBuildTapped(Building other){
      if(other.within(this, range) && other instanceof PacketTransporterc po){
        if(other instanceof PacketConnecterBuild pbb){
          if(tos.contains(po)) tos.remove(po);
          else if(tos.size + froms.size < maxConnect) tos.add(po);

          pbb.tos.remove(this);
          return false;
        }

        if(other.block instanceof PacketBlock pb){
          if(pb.producePacketEntry){
            if(froms.contains(po)) froms.remove(po);
            else if(tos.size + froms.size < maxConnect) froms.add(po);
          }

          if(pb.acceptPacket){
            if(tos.contains(po)) tos.remove(po);
            else if(tos.size + froms.size < maxConnect) tos.add(po);
          }
        }

        //todo 连接器接口

        return false;
      }

      return true;
    }

    //todo 容量上限
    @Override
    public boolean acceptPacket(PacketEntry packetEntry, PacketTransportercProv source){
      return super.acceptPacket(packetEntry, source)
        && (resourceClass == null || packetEntry.packet.packetType().resourceClass == resourceClass)
        && packets.size < packetCapacity;
    }

    @Override
    public void handlePacket(PacketEntry packetEntry, PacketTransportercProv source){
      super.handlePacket(packetEntry, source);
      dumpPackets.put(packetEntry, null);

      packetEntry.targetPos.set(this);
    }

    @Override
    public void updateTile(){
      handleTimer += edelta();
      dumpTimer += edelta();
      roundTimer += edelta() * roundSpeedMulti;

      tos.removeAll(e -> !e.isAdded());

      if(!froms.isEmpty() && handleTimer >= packetDumpTime){
        for(PacketTransporterc from : froms){
          if(from instanceof PacketBlockBuild pbb){
            PacketEntry entry = pbb.getOutputPacket();
            if(entry != null && acceptPacket(entry, () -> pbb)){
              pbb.transferPacket(entry, () -> this);

              handleTimer %= packetDumpTime;
              break;
            }
          }
        }
      }

      dumpPackets.each((packet, to) -> {
        //将封包移除, 也就是"收纳至自身内"
        if(to == null){
          if(packet.packet.isAdded() && packet.packet.within(this, 0.1f)){
            packet.packet.remove();
            packet.packet.vel().setZero();
          }

          return;
        }

        //todo
        if(!to.isAdded()){
          dumpPackets.remove(packet);
          return;
        }

        if(Tmp.v1.set(to).sub(packet).len() < 0.1f){
          toTrans.add(packet);
        }
      });

      for(PacketEntry toTran : toTrans){
        transferPacket(toTran, () -> dumpPackets.get(toTran));
      }
      toTrans.clear();

      if(!tos.isEmpty() && dumpTimer >= packetDumpTime){
        PacketEntry packet = packets.find(p -> !p.packet.isAdded() && dumpPackets.get(p) == null);
        if(packet != null){
          PacketTransporterc to = null;

          int size = tos.size;
          for(int i = 0; i < size; i++){
            PacketTransporterc t = tos.get(Mathf.mod(i + cdump, size));

            if(t instanceof PacketBlockBuild pbb && pbb.acceptPacket(packet, () -> this)){
              to = t;
              break;
            }
          }

          if(to != null){
            packet.packet.add();
            packet.targetPos.set(to);
            dumpPackets.put(packet, to);

            incrementDump(size);
            dumpTimer %= packetDumpTime;
          }
        }
      }

      super.updateTile();
    }

    @Override
    public void movePacket(PacketEntry packetEntry){
      Vec2 vel = packetEntry.packet.vel();
      vel.set(packetEntry.targetPos).sub(packetEntry.packet);
      vel.setLength(Math.min(packetSpeed(packetEntry), vel.len()) * efficiency);
    }

    @Override
    public void transferPacket(PacketEntry packetEntry, PacketTransportercProv to){
      super.transferPacket(packetEntry, to);
      dumpPackets.remove(packetEntry);
    }

    @Override
    public int overrideCollisionLayer(){
      return -1;
    }

    @Override
    public void throwPacket(PacketEntry packetEntry, float speed){
      if(!packetEntry.packet.isAdded()){
        packetEntry.packet.add();
      }

      super.throwPacket(packetEntry, 0);
    }

    @Override
    public void handlePacketBoundary(PacketEntry packetEntry){
      //super.handlePacketBoundary(packetEntry);
    }
  }
}
