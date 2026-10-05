package LLL.world.blocks.packet;

import LLL.content.*;
import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

//打包机, 准备完成后查找可打包的资源进行封包, 资源消耗相关逻辑在封包类型内
public class PackerBlock extends PacketBlock{
  public PacketType packetType = OvulamPacketTypes.smallItemPacket;
  public int PayloadCapacity = 4;
  public float checkInterval = 10f;

  protected ResourceStack<?> instance;

  public TextureRegion outRegion;

  public PackerBlock(String name){
    super(name);
    rotate = true;

    outputFacing = true;
    acceptPacket = false;
    producePacketEntry = true;
  }

  @Override
  public void load(){
    super.load();
    outRegion = Core.atlas.find("lulu-packer-out-" + size);
  }

  @Override
  public void init(){
    initPackerType();
    super.init();
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{bottomRegion, outRegion, region};
  }

  @Override
  public void drawPlanRegionDir(BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
    Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    super.drawPlanRegionDir(plan, list);
  }

  public void initPackerType(){
    instance = ResourceStackManager.resourceStackInstances.find(stack ->
      ResourceStackManager.classMap.get(packetType.resourceClass).isInstance(stack)
    );

    instance.applyBlock(this);
  }

  public class PackerBuild extends PacketBlockBuild{
    public PayloadSeq payloadSeq;
    public boolean prepared;
    public Packetc[] toOutput = new Packetc[(int)(size / packetType.size)];
    public Interval timer = new Interval();

    @Override
    public void draw(){
      Draw.rect(bottomRegion, x, y, size * 8, size * 8);
      Draw.rect(outRegion, x, y, rotdeg());

      drawEdge();

      drawDirRegion();
    }

    @Override
    public void add(){
      super.add();
      if(packetType.resourceClass == UnlockableContent.class){
        payloadSeq = new PayloadSeq();
      }
    }

    //todo 由于打包的是载荷的享元, 因此接收的载荷实体的关键属性与初始状态相同, 例如不能通过打包解包后使得残血单位重新满血
    @Override
    public boolean acceptPayload(Building source, Payload payload){
      if(payloadSeq == null) return false;

      if(payload instanceof BuildPayload bp){
        Building build = bp.build;
        if(build.health < build.maxHealth) return false;
      }else if(payload instanceof UnitPayload up){
        Unit unit = up.unit;
        if(unit.health < unit.maxHealth) return false;
      }

      return payloadSeq.total() < PayloadCapacity;
    }

    @Override
    public void handlePayload(Building source, Payload payload){
      payloadSeq.add(payload.content());
    }

    @Override
    public PayloadSeq getPayloads(){
      return payloadSeq;
    }

    @Override
    public PacketEntry getOutputPacket(){
      return packets.isEmpty() ? null : packets.first();
    }

    @Override
    public boolean shouldConsume(){
      for(PacketEntry entry : packets){
        //size * 8 / 2 * 2
        if(entry.within(this, packetType.size * 8)){
          return false;
        }
      }
      return super.shouldConsume() && packets.size + 1 <= size && !prepared;
    }

    @Override
    public float deltaTime(){
      return delta();//防止打包器因为封包位于中心导致shouldConsume为false使得效率为0
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(prepared && timer.get(checkInterval)){
        Object over = instance.findAvailableResource(this, packetType.capacity);

        if(over == null) return;
        prepared = false;

        PacketEntry entry = createPacketEntry(packetType);
        packets.add(entry);
        entry.packet.handle(this, over, packetType.capacity);

        int total = toOutput.length;
        for(int i = 0; i < total; i++){
          int index = (i + cdump) % total;
          if(toOutput[index] == null){
            toOutput[index] = entry.packet;
            entry.targetPos.set(size, -size / packetType.size + 1 + index * 2).scl(4).rotate(rotation * 90).add(this);

            incrementDump(total);
            break;
          }
        }
      }
    }

    @Override
    public void movePacket(PacketEntry packetEntry){
      super.movePacket(packetEntry);

      Vec2 vel = packetEntry.packet.vel();
      float len = vel.len();

      if(rotation % 2 == 0) Tmp.v1.scl(1, 2);
      else Tmp.v1.scl(2, 1);

      vel.setLength(len);
    }

    @Override
    public void transferPacket(PacketEntry packetEntry, PacketTransportercProv to){
      super.transferPacket(packetEntry, to);

      for(int i = 0; i < toOutput.length; i++){
        if(toOutput[i] == packetEntry.packet){
          toOutput[i] = null;
        }
      }
    }
  }
}
