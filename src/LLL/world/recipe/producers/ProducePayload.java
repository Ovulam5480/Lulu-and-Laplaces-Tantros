package LLL.world.recipe.producers;

import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.world.recipe.consumers.*;
import arc.*;
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import java.util.*;

public class ProducePayload<T extends Building & ProducerBuildc<T>> extends BaseProduce<T>{
  private static final ObjectMap<UnlockableContent, PayloadStack> TMP = new ObjectMap<>();

  public int displayLim = 4;
  public PayloadStack[] payloads;
  public Func2<T, UnlockableContent, Payload> payloadMaker = this::makePayloadDef;
  public Boolf2<T, UnlockableContent> valid;

  public ProducePayload(PayloadStack[] payloads, Boolf2<T, UnlockableContent> valid){
    this.payloads = payloads;
    this.valid = valid;
  }

  private Payload makePayloadDef(T ent, UnlockableContent type){
    if(type instanceof UnitType unitType){
      Unit unit = unitType.create(ent.team);
      Events.fire(new EventType.UnitCreateEvent(unit, ent));
      return new UnitPayload(unit);
    }else if(type instanceof Block block){
      return new BuildPayload(block, ent.team);
    }
    throw new IllegalArgumentException("default payload maker can only make 'Building' and 'Unit', if you want to make other things, please use custom payload maker to field 'payloadMaker'");
  }

  @Override
  public ResourceType type(){
    return ResourceType.payload;
  }

  @Override
  public void buildIcons(Table table){
    ConsumePayload.buildPayloadIcons(table, payloads, displayLim);
  }

  @SuppressWarnings({"rawtypes"})
  @Override
  public void merge(BaseProduce other){
    if(other instanceof ProducePayload prod){
      TMP.clear();
      for(PayloadStack stack : payloads){
        TMP.put(stack.item, stack);
      }

      for(PayloadStack stack : prod.payloads){
        TMP.get(stack.item, () -> new PayloadStack(stack.item, 0)).amount += stack.amount;
      }

      payloads = TMP.values().toSeq().sort(Comparator.comparingInt(a -> a.item.id)).toArray(PayloadStack.class);
    }else throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public void produce(T entity){
    for(PayloadStack stack : payloads){
      for(int i = 0; i < stack.amount; i++){
        Payload payload = payloadMaker.get(entity, stack.item);
        payload.set(entity.x, entity.y, entity.rotdeg());
        if(entity.acceptPayload(entity, payload)) entity.handlePayload(entity, payload);
      }
    }
  }

  @Override
  public boolean valid(T entity){
    for(PayloadStack stack : payloads){
      if(!valid.get(entity, stack.item)) return false;
    }
    return true;
  }

  @Override
  public void update(T entity){
  }

  @Override
  public void display(Stats stats){
    for(PayloadStack stack : payloads){
      stats.add(Stat.output, t -> {
        t.add(StatValues.stack(stack));
        t.add(stack.item.localizedName).padLeft(4).padRight(4);
      });
    }
  }
}
