package LLL.world.blocks.packet;

import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.payloads.PayloadBlock.*;

//对封包内容解包固定数量的建筑
public class UnpackerBlock extends PacketBlock{
  //每次解包后随机设定封包
  public boolean randomUnpackPacket = false;
  //非随机解包时, 随机选择一个资源
  public boolean randomUnpackResource = false;
  public float excavateAmount = 1f;
  public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;

  //解包的封包类型, 为空时 再对解包类型匹配
  public PacketType filterType;
  //解包类型, 空为全部类型
  public Seq<Class<? extends ResourceStack<?>>> filter = new Seq<>();

  protected Seq<ResourceStack<?>> instances = new Seq<>();

  public TextureRegion inRegion1, inRegion2, iconRegion;

  public UnpackerBlock(String name){
    super(name);

    rotate = false;
    dumpEdge = false;
  }

  @Override
  public void load(){
    super.load();

    inRegion1 = Core.atlas.find(name + "-in-1");
    inRegion2 = Core.atlas.find(name + "-in-2");
    iconRegion = Core.atlas.find(name + "-icon");
    bottomRegion = Core.atlas.find("lulu-unpacker-bottom-" + size);
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{iconRegion};
  }

  @Override
  public void init(){
    super.init();

    initPackerType();
  }

  @SuppressWarnings("unchecked")
  public void initPackerType(){
    if(filterType != null){
      filter.add(ResourceStackManager.classMap.get(filterType.resourceClass));
    }

    for(ResourceStack<?> instance : ResourceStackManager.resourceStackInstances){
      if(filter.isEmpty() || filter.contains((Class<? extends ResourceStack<?>>)instance.getClass())){
        instance.applyBlock(this);
        instances.add(instance);
      }
    }
  }

  public class UnpackerBuild<T extends Packetc> extends PacketBlockBuild{
    public float progress;
    public float warmup;

    public float speed, targetSpeed;

    public Seq<T> toUnpacks = new Seq<>();

    public @Nullable T toUnpack;
    public @Nullable ResourceStack<?> stack;

    public Payload payload;
    public Vec2 payVector = new Vec2();
    public float payRotation;

    @Override
    public boolean shouldConsume(){
      return enabled && stack != null && stack.canUnpack(this);
    }

    @Override
    public boolean acceptPayload(Building source, Payload payload){
      return source == null && this.payload == null;
    }

    @Override
    public void handlePayload(Building source, Payload payload){
      this.payload = payload;
      this.payVector.set(source == null ? this : source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
      this.payRotation = payload.rotation();

      updatePayload();
    }

    public void setStack(){
      if(toUnpacks.isEmpty()) return;

      if(toUnpack == null){
        toUnpack = randomUnpackPacket ? toUnpacks.random() : toUnpacks.first();
      }

      stack = randomUnpackPacket ? toUnpack.resources().random() : toUnpack.resources().first();
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(progress > 1){
        unpack();
        progress %= 1;
      }

      dumpOutputs();
    }

    @Override
    public void draw(){
      super.draw();

      for(int i = 0; i < 4; i++){
        if(!hasBlends[i]) Draw.rect(i < 2 ? inRegion1 : inRegion2, x, y, i * 90);
      }
    }

    public void unpack(){
      if(toUnpack.unpack(this, stack, excavateAmount) || (!randomUnpackPacket && randomUnpackResource)){
        stack = null;
      }

      if(!toUnpack.isAdded()){
        removePacket();
      }

      if(randomUnpackPacket){
        toUnpack = null;
      }

      consume();
    }

    public void removePacket(){
      packets.remove(pe -> pe.packet == toUnpack);
      toUnpacks.remove(toUnpack);

      toUnpack = null;
      stack = null;
    }


    public void dumpOutputs(){
      instances.each(stack -> stack.dumpOutputs(this));
    }

    @Override
    public Payload getPayload(){
      return payload;
    }


    public void moveOutPayload(){
      if(payload == null) return;

      updatePayload();

      Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize / 2f);

      payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
      payVector.approach(dest, payloadSpeed * delta());

      Building front = front();
      boolean canDump = front == null || !front.tile.solid();
      boolean canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

      if(canDump && !canMove){
        pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));
      }

      if(payVector.within(dest, 0.001f)){
        payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

        if(canMove){
          if(movePayload(payload)){
            payload = null;
          }
        }else if(canDump){
          dumpPayload();
        }
      }
    }

    public void updatePayload(){
      payload.set(x + payVector.x, y + payVector.y, payRotation);
    }

    public void dumpPayload(){
      float tx = Angles.trnsx(payload.rotation(), 0.1f), ty = Angles.trnsy(payload.rotation(), 0.1f);
      payload.set(payload.x() + tx, payload.y() + ty, payload.rotation());

      if(payload.dump()){
        payload = null;
      }else{
        payload.set(payload.x() - tx, payload.y() - ty, payload.rotation());
      }
    }
  }
}
