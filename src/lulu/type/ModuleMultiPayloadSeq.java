package lulu.type;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

public class ModuleMultiPayloadSeq extends PayloadSeq implements Iterable<PositionPayload>{
  public final Seq<PositionPayload> payloads = new Seq<>();
  public Team team;
  public Vec2 center;

  public ModuleMultiPayloadSeq(Building building){
    this.team = building.team;
    this.center = new Vec2().set(building);
  }

  public void add(Payload payload){
    payloads.add(new PositionPayload(payload));
    super.add(payload.content(), 1);
  }

  public void add(PositionPayload payload){
    payloads.add(payload);
    super.add(payload.content(), 1);
  }

  @Override
  public void add(UnlockableContent type){
    if(type instanceof Block b){
      add(new BuildPayload(b, team));
    }else if(type instanceof UnitType u){
      add(new UnitPayload(u.create(team)));
    }
    super.add(type);
  }

  @Override
  public void add(UnlockableContent type, int amount){
    for(int i = 0; i < amount; i++){
      if(type instanceof Block b){
        add(new BuildPayload(b, team));
      }else if(type instanceof UnitType u){
        add(new UnitPayload(u.create(team)));
      }
    }
    super.add(type, amount);
  }

  public PositionPayload getPayload(UnlockableContent type){
    return payloads.find(p -> p.content() == type);
  }

  @Override
  public void remove(UnlockableContent type){
    payloads.remove(t -> t.content() == type);
    super.remove(type);
  }

  public void remove(PositionPayload payload){
    payloads.remove(payload);
    super.remove(payload.content(), 1);
  }

  @Override
  public void remove(UnlockableContent type, int amount){
    int[] count = {0};
    payloads.removeAll(t -> t.content() == type && count[0]++ < amount);
    super.remove(type, amount);
  }

  public void remove(PayloadStack[] stacks){
    for(PayloadStack stack : stacks){
      remove(stack.item, stack.amount);
    }
  }

  @Override
  public void remove(Seq<PayloadStack> stacks){
    stacks.each((b) -> remove(b.item, b.amount));
  }

  @Override
  public ModuleMultiPayloadSeq removeAll(Boolf<UnlockableContent> predicate){
    payloads.removeAll(p -> predicate.get(p.content()));
    super.removeAll(predicate);
    return this;
  }

  public Payload take(){
    return payloads.isEmpty() ? null : payloads.pop().payload;
  }

  public Payload get(){
    return payloads.isEmpty() ? null : payloads.peek().payload;
  }

  public PositionPayload get(int index){
    return payloads.get(index);
  }

  public void each(Cons<PositionPayload> consumer){
    payloads.each(consumer);
  }

  @Override
  public Iterator<PositionPayload> iterator(){
    return payloads.iterator();
  }
}
