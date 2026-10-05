package LLL.type.resourceStacks;

import LLL.world.blocks.packet.*;
import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import universecore.util.handler.*;

public class PayloadResourceStack extends ResourceStack<UnlockableContent>{
  public static Seq<UnlockableContent> payloads = new Seq<>();
  private static final ObjectIntMap<UnlockableContent> contents = new ObjectIntMap<>();

  @Override
  public String name(){
    return item.name;
  }

  @Override
  public boolean discrete(){
    return true;
  }

  @Override
  public int id(){
    return 3;
  }

  @Override
  public void register(){
    ResourceStackManager.register(UnlockableContent.class, PayloadResourceStack.class, PayloadResourceStack::new, new PayloadResourceStack(), id());
  }

  @Override
  public void init(){
    payloads.addAll(Vars.content.units()).addAll(Vars.content.blocks());
    ResourceStackManager.init(UnlockableContent.class, payloads);
  }

  @Override
  public void applyBlock(Block block){
  }

  @Override
  public UnlockableContent findAvailableResource(Entityc entityc, float amount){
    int amountInt = Mathf.floor(amount);

    if(entityc instanceof Building b){
      if(b.getPayloads() != null){
        ObjectIntMap<UnlockableContent> payloadsMap = FieldHandler.getValueDefault(b.getPayloads(), "payloads");
        for(ObjectIntMap.Entry<UnlockableContent> entry : payloadsMap){
          if(entry.value >= amountInt){
            return entry.key;
          }
        }
      }else if(b.getPayload() != null && amountInt <= 1){
        return b.getPayload().content();
      }
    }else if(entityc instanceof Payloadc pc){
      contents.clear();

      pc.payloads().each(p -> contents.increment(p.content(), 1));
      for(ObjectIntMap.Entry<UnlockableContent> entry : contents.entries()){
        if(entry.value >= amountInt){
          return entry.key;
        }
      }
    }

    return null;
  }

  @Override
  public void applyPack(Entityc entityc, Object object, float amount){
    int amountInt = Mathf.floor(amount);

    if(entityc instanceof Building b){
      if(b.getPayloads() != null){
        b.getPayloads().remove(item, amountInt);
      }else{
        for(int i = 0; i < amountInt; i++){
          if(b.getPayload() == item){
            b.takePayload().remove();
          }else{
            break;
          }
        }
      }
    }else if(entityc instanceof Payloadc pc){
      int[] i = {0};
      pc.payloads().removeAll(p -> p.content() == item && i[0]++ < amountInt);
    }
  }

  @Override
  public boolean unpack(Entityc entityc, float amount){
    int had = Math.min((int)amount, (int)this.amount);

    if(entityc instanceof Building b){
      for(int i = 0; i < had; i++){
        Payload payload = getPayload(item, b.team);

        if(b.acceptPayload(null, payload)){
          b.handlePayload(null, payload);
          this.amount--;
        }else return false;
      }
      return true;
    }else if(entityc instanceof Payloadc p){
      for(int i = 0; i < had; i++){
        Payload payload = getPayload(item, p.team());

        if(p.canPickupPayload(payload)){
          p.addPayload(payload);
          this.amount--;
        }else return false;
      }
      return true;
    }
    return false;
  }

  private static Payload getPayload(UnlockableContent item, Team team){
    if(item instanceof Block cb){
      return new BuildPayload(cb, team);
    }else if(item instanceof UnitType cu){
      return new UnitPayload(cu.create(team));
    }

    return null;
  }

  @Override
  public boolean canUnpack(Entityc entityc){
    if(entityc instanceof Building b){
      return b.acceptPayload(null, getPayload(item, b.team));
    }else if(entityc instanceof Payloadc p){
      return p.canPickupPayload(getPayload(item, p.team()));
    }

    return false;
  }

  @Override
  public void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building){
    building.moveOutPayload();
  }

  @Override
  public void write(Writes writes){
    TypeIO.writeContent(writes, item);
    writes.f(amount);
  }

  @Override
  public ResourceStack<UnlockableContent> read(Reads reads){
    UnlockableContent content = (UnlockableContent)TypeIO.readContent(reads);
    float amount = reads.f();
    return ResourceStackManager.getResourceInstanceByClass(content, UnlockableContent.class, amount);
  }
}
