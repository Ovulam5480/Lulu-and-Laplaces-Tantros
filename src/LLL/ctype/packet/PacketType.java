package LLL.ctype.packet;

import LLL.*;
import LLL.content.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static arc.graphics.g2d.Draw.*;

//todo loadable
public abstract class PacketType{
  public float capacity = 3;
  public float speedMulti = 1;
  public float size = 1;
  public int collisionLayer = 0;
  public float clipSize = -1;
  public float drag = 0.01f;
  public float unitBindDrag = 1;
  public Effect treadEffect;
  //6格/秒 -> 0.8/刻
  public float treadEffectChangeSpeed = 8f;
  public String name;
  public TextureRegion region;
  //仅限实体封包, 封包无主时多长时间会自己消失
  public float lifetime = 60 * 60;
  public Class<? extends UnlockableContent> resourceClass = Item.class;
  public Prov<? extends Packetc> constructor;
  public float drawLayer = -1;
  public Class<? extends ResourceStack<?>> resourceStackClass;

  protected static final Seq<ResourceStack<?>> removes = new Seq<>();

  @SuppressWarnings("unchecked")
  public PacketType(String name){
    this.name = LuluMod.modName + name;

    constructor = EntityMapping.map(this.name);
    OvulamPacketTypes.packetTypes.add(this);
  }

  public void load(){
    region = Core.atlas.find(name);
  }

  public void init(){
    if(drawLayer < 0){
      drawLayer = switch(collisionLayer){
        case 1 -> Layer.legUnit;
        case 2 -> Layer.flyingUnitLow;
        default -> Layer.blockAdditive;
      };
    }

    if(treadEffect == null){
      treadEffect = new Effect(50, e -> {
        color(Tmp.c1.set(e.color).mul(1.5f));
        Fx.rand.setSeed(e.id);
        for(int i = 0; i < 3; i++){
          Fx.v.trns(e.rotation + Fx.rand.range(40f), Fx.rand.random(6f * e.finpow()));
          Fill.circle(e.x + Fx.v.x + Fx.rand.range(4f), e.y + Fx.v.y + Fx.rand.range(4f), Math.min(e.fout(), e.fin() * e.lifetime / 8f) * size / 3f * 3f * Fx.rand.random(0.8f, 1.1f) + 0.3f);
        }
      }).layer(Layer.debris);
    }

    if(clipSize == -1){
      clipSize = size * 8f;
    }

    resourceStackClass = ResourceStackManager.classMap.get(resourceClass);
  }

  public void updatePacket(Packetc packet){
  }

  public void draw(Packetc packet){
    Draw.z(drawLayer);

    if(!packet.resources().isEmpty()){
      Draw.rect(packet.resources().first().getIcon(), packet.x(), packet.y());
    }
  }

  //最大接收数量判断
  public float maxAccept(Packetc packet, Object item, float amount){
    return Math.min(packet.remainSpace(), amount);
  }

  //接收
  public abstract void handle(Packetc packet, @Nullable Entityc entityc, float amount, Object item);

  public void unpack(Packetc packet, @Nullable Entityc entityc, float amount){
    for(ResourceStack<?> stack : packet.resources()){
      if(stack.unpack(entityc, amount)){
        packet.resources().remove(stack);

        if(packet.resources().isEmpty()){
          packet.remove();
        }
      }
    }
  }

  public boolean unpack(Packetc packet, @Nullable Entityc entityc, float amount, ResourceStack<?> stack){
    if(stack.unpack(entityc, amount)){
      packet.resources().remove(stack);

      if(packet.resources().isEmpty()){
        packet.remove();
      }
      return true;
    }
    return false;
  }

  private Packetc create(){
    Packetc packet = constructor.get();
    packet.setPacketType(this);
    return packet;
  }

  public Packetc create(Teamc owner, float x, float y){
    Packetc packet = create();

    packet.owner(owner);
    packet.set(x, y);
    packet.add();

    return packet;
  }
}
