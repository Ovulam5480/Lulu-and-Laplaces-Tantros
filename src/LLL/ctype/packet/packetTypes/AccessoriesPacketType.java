package LLL.ctype.packet.packetTypes;

import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;

//配件封包类型, 进行封包时损失部分资源, 只能整体进行打包和解包
public class AccessoriesPacketType extends SinglePacketType{
  public float recoveryRate = 0.5f;
  public AccessoriesType accessoriesType;
  public TextureRegion assessoriesRegion;

  public AccessoriesPacketType(String name, AccessoriesType accessoriesType){
    //todo name
    super(name);
    this.accessoriesType = accessoriesType;
  }

  @Override
  public void load(){
    super.load();
    assessoriesRegion = Core.atlas.find(name + "-item");
  }

  @Override
  public void draw(Packetc packet){
    Draw.z(drawLayer);

    if(!packet.resources().isEmpty()){
      ResourceStack<?> stack = packet.resources().first();

      if(stack.item instanceof Item i){
        Draw.color(i.color);
      }else if(stack.item instanceof Liquid l){
        Draw.color(l.color);
      }
    }
    Draw.rect(assessoriesRegion, packet.x(), packet.y());
    Draw.color();
  }

  @Override
  public float maxAccept(Packetc packet, Object item, float amount){
    if(!resourceClass.isInstance(item)) return 0;

    return packet.resources().isEmpty() ? packet.remainSpace() : 0;
  }

  @Override
  public void handle(Packetc packet, @Nullable Entityc entityc, float amount, Object item){
    Seq<ResourceStack<?>> resources = packet.resources();
    if(!resources.isEmpty()) return;

    ResourceStack<?> resourceStack = ResourceStackManager.getResourceInstanceByClass(item, resourceClass, amount);
    resources.add(resourceStack);

    if(entityc != null) resourceStack.applyPack(entityc, item, amount * recoveryRate);
  }

  public void unpack(Packetc packet, @Nullable Entityc entityc, float amount){
    packet.resources().first().unpack(entityc, amount);
    packet.remove();
  }

  public boolean unpack(Packetc packet, @Nullable Entityc entityc, float amount, ResourceStack<?> stack){
    packet.resources().first().unpack(entityc, amount);
    packet.remove();
    return true;
  }
}
