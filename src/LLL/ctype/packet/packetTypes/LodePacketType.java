package LLL.ctype.packet.packetTypes;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class LodePacketType extends PacketType{
  public float tenacity = 0.1f;
  public static final String regionSuffix = "-lode";

  public LodePacketType(String name){
    super(name);
  }

  //todo 随机矿石贴图
  @Override
  public void load(){
    region = findRegion(LuluMod.modName + "cobalt-nodule-lode", 0);
  }

  @Override
  public void draw(Packetc packet){
    Draw.z(Layer.floor);

    LodePacketc lodePacket = (LodePacketc)packet;

    if(lodePacket.LodeRegion() == null){
      for(ResourceStack<?> stack : lodePacket.resources()){
        String name = stack.name() + regionSuffix;

        TextureRegion region = findRegion(name, packet.id());
        if(region != null){
          lodePacket.LodeRegion(region);
        }
      }

      if(lodePacket.LodeRegion() == null){
        lodePacket.LodeRegion(Core.atlas.find("error"));
      }
    }

    Draw.rect(lodePacket.LodeRegion(), packet.x(), packet.y());
  }

  public TextureRegion findRegion(String name, int id){
    if(Core.atlas.has(name)){
      return Core.atlas.find(name);
    }else if(Core.atlas.has(name + "0")){
      int max = 0;
      while(Core.atlas.has(name + max)){
        max++;
      }
      Rand rand = new Rand(id);
      return (Core.atlas.find(name + rand.random(max - 1)));
    }

    return null;
  }

  //矿石不能输入
  @Override
  public float maxAccept(Packetc packet, Object item, float amount){
    return 0;
  }

  @Override
  public void handle(Packetc packet, @Nullable Entityc entityc, float amount, Object item){
  }

  @Override
  public void unpack(Packetc packet, Entityc entityc, float amount){
    float sum = packet.amount();
    float rand = Mathf.random(sum);

    float count = 0;
    for(ResourceStack<?> stack : packet.resources()){
      if(rand >= count && rand < count + stack.amount){
        float transfer = Math.min(stack.amount, amount);

        if(stack.unpack(entityc, transfer)){
          removes.add(stack);
        }
        break;
      }
      count += stack.amount;
    }

    packet.resources().removeAll(removes);

    //todo 允许缓慢的挖掘无限的贫瘠矿
    if(packet.resources().isEmpty()){
      packet.remove();
    }
  }

  @Override
  public Packetc create(Teamc owner, float x, float y){
    float offset = Mathf.mod(size - 1, 2) < 1 ? 0 : 4;

    int wx = World.toTile(x) * 8;
    int wy = World.toTile(y) * 8;
    Rect hitbox = Tmp.r1.setCentered(wx + offset, wy + offset, size * 8);

    if(Tmp.r2.set(0, 0, Vars.world.unitWidth(), Vars.world.unitHeight()).grow(8.0001f).contains(hitbox)//限制在世界内
      && !LuluMod.oIndexer.lodePacketTree.any(hitbox.x, hitbox.y, hitbox.width, hitbox.height)){//防止互相存在重叠
      return super.create(owner, wx, wy);
    }

    return null;
  }
}
