package lulu.type;

import arc.func.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

public class ModulePayloadSeq extends PayloadSeq implements Iterable<Payload>{
  public final Seq<Payload> payloads = new Seq<>(Payload.class);
  public Team team;

  public ModulePayloadSeq(Team team){
    this.team = team;
  }

  public void add(Payload payload){
    payloads.add(payload);
    super.add(payload.content(), 1);
  }

  @Override
  public void add(UnlockableContent block, int amount){
    if(block instanceof Block b){
      for(int i = 0; i < amount; i++){
        add(new BuildPayload(b, team));
      }
    }
    super.add(block, amount);
  }

  public Payload getPayload(UnlockableContent type){
    return payloads.find(p -> p.content() == type);
  }

  public Payload peek(){
    return payloads.peek();
  }

  public Payload take(){
    Payload payload = payloads.peek();
    remove(payload.content());
    return payload;
  }

  @Override
  public void remove(UnlockableContent type){
    payloads.remove(t -> t.content() == type);
    super.remove(type);
  }

  public void removeLast(UnlockableContent type){
    for(int i = 0; i < payloads.size; ++i){
      int index = payloads.size - 1 - i;
      if(payloads.get(index).content() == type){
        payloads.remove(index);
        break;
      }
    }

    super.remove(type);
  }

  private Payload removeAt(int index){
    Payload payload = payloads.get(index);
    super.remove(payload.content());
    return payload;
  }

  @Override
  public void remove(UnlockableContent type, int amount){
    int[] count = {0};
    payloads.removeAll(t -> t.content() == type && count[0]++ < amount);
    super.remove(type);
  }

  public void removeAll(UnlockableContent type){
    payloads.removeAll(t -> t.content() == type);
    super.remove(type);
  }

  @Override
  public ModulePayloadSeq removeAll(Boolf<UnlockableContent> predicate){
    payloads.removeAll(p -> predicate.get(p.content()));
    super.removeAll(predicate);
    return this;
  }

  @Override
  public void clear(){
    payloads.clear();
    super.clear();
  }

  @Override
  public Iterator<Payload> iterator(){
    return new Iterator<>(){
      int index;
      boolean removable;

      @Override
      public boolean hasNext(){
        return index < payloads.size;
      }

      @Override
      public Payload next(){
        if(!hasNext()) throw new NoSuchElementException();
        removable = true;
        return payloads.get(index++);
      }

      @Override
      public void remove(){
        if(!removable) throw new IllegalStateException();
        removeAt(--index);
        removable = false;
      }
    };
  }

  @Override
  public void write(Writes write){
    write.i(payloads.size);
    for(Payload payload : payloads){
      Payload.write(payload, write);
    }
  }

  @Override
  public void read(Reads read){
    int len = read.i();
    for(int i = 0; i < len; i++){
      add(Payload.<Payload>read(read));
    }
  }
}
