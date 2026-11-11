package lulu.world.recipe.producers;

import arc.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import lulu.entities.gen.*;
import lulu.type.*;
import lulu.world.draw.payload.*;
import lulu.world.move.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

public class ProduceMultiPayload<T extends Building & MultiPayloadCrafterBuildc<T>> extends ProducePayload<T>{
  public ObjectMap<UnlockableContent, MovePayload> movePayloads = new ObjectMap<>();

  public DrawRecipePayload drawCrafting = new DrawPayloadDefault();

  public ProduceMultiPayload(PayloadStack[] payloads, Boolf2<T, UnlockableContent> valid){
    super(payloads, valid);
  }

  @Override
  public void produce(T entity){
    Team team = entity.team();

    for(PayloadStack output : payloads){
      for(int i = 0; i < output.amount; i++){
        Payload payload;
        if(output.item instanceof Block b){
          payload = new BuildPayload(b, team);
        }else if(output.item instanceof UnitType u){
          Unit unit = u.create(team);
          unit.rotation = entity.rotdeg();
          payload = new UnitPayload(unit);
          Events.fire(new EventType.UnitCreateEvent(unit, entity));
        }else{
          return;
        }

        MovePayload movePayload = movePayloads.get(output.item);
        PositionPayload cp = new PositionPayload(payload, entity.setTargetPosition(i, movePayload), Vec2.ZERO);

        entity.handlePositionPayload(cp);
      }

    }
  }

  @Override
  public boolean valid(T entity){
    return entity.outputPayloads().size <= entity.getOutMoverCapacity();
  }
}
