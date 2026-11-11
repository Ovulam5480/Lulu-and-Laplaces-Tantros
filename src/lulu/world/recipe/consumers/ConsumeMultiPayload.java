package lulu.world.recipe.consumers;

import arc.struct.*;
import lulu.entities.gen.*;
import lulu.type.*;
import lulu.world.draw.payload.*;
import lulu.world.move.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;

public class ConsumeMultiPayload<T extends Building & MultiPayloadCrafterBuildc<T>> extends ConsumePayload<T>{
  public ObjectMap<UnlockableContent, MovePayload> movePayloads = new ObjectMap<>();

  public DrawRecipePayload drawCrafting = new DrawPayloadDefault();

  public ConsumeMultiPayload(PayloadStack[] payloads){
    super(payloads);
  }

  public ConsumeMultiPayload(UnlockableContent content){
    super(content);
  }

  public ConsumeMultiPayload(UnlockableContent content, int amount){
    super(content, amount);
  }

  public ConsumeMultiPayload(ConsumeMultiPayloadBuilder[] payloads){
    super(Seq.map(payloads, p -> new PayloadStack(p.content, p.amount)).toArray());
    for(ConsumeMultiPayloadBuilder payload : payloads){
      if(payload.movePayload != null) putMovePayloads(payload.content, payload.movePayload);
    }
  }

  public void putMovePayloads(UnlockableContent content, MovePayload movePayload){
    movePayloads.put(content, movePayload);
  }

  public void putMovePayloads(UnlockableContent content){
    putMovePayloads(content, new MoveDefault());
  }

  @Override
  public void update(T entity){
  }

  @Override
  public float efficiency(T entity){
    for(PositionPayload positionPayload : entity.craftPayloads()){
      if(!positionPayload.hasArrived()) return 0;
    }

    return 1;
  }

  public static class ConsumeMultiPayloadBuilder{
    public UnlockableContent content;
    public int amount;
    public MovePayload movePayload;

    public ConsumeMultiPayloadBuilder(UnlockableContent content, int amount, MovePayload movePayload){
      this.content = content;
      this.amount = amount;
      this.movePayload = movePayload;
    }
  }
}
