package LLL.lib.singularity.world.modules;

import arc.struct.*;
import arc.util.io.*;
import mindustry.ctype.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;

import java.util.*;

public class PayloadModule extends BlockModule{
  Seq<Payload> payloads = new Seq<>();

  public int total(){
    return payloads.size;
  }

  public void add(Payload payload){
    payloads.add(payload);
  }

  public int amountOf(UnlockableContent type){
    int res = 0;
    for(Payload payload : payloads){
      if(payload.content() == type) res++;
    }
    return res;
  }

  public Payload take(){
    return payloads.isEmpty() ? null : payloads.pop();
  }

  public Payload get(){
    return payloads.isEmpty() ? null : payloads.peek();
  }

  public Payload get(UnlockableContent type){
    for(Payload payload : payloads){
      if(payload.content() == type) return payload;
    }
    return null;
  }

  public Payload remove(UnlockableContent type){
    Iterator<Payload> itr = payloads.iterator();
    while(itr.hasNext()){
      Payload p = itr.next();
      if(p.content() == type){
        itr.remove();
        return p;
      }
    }
    return null;
  }

  public void removeAll(UnlockableContent type){
    Iterator<Payload> itr = payloads.iterator();
    while(itr.hasNext()){
      Payload p = itr.next();
      if(p.content() == type){
        itr.remove();
      }
    }
  }

  public void clear(){
    payloads.clear();
  }

  public boolean isEmpty(){
    return payloads.isEmpty();
  }

  public Iterable<Payload> iterate(){
    return payloads;
  }

  @Override
  public void write(Writes write){
    write.i(payloads.size);
    for(Payload payload : payloads){
      Payload.write(payload, write);
    }
  }

  @Override
  public void read(Reads read, boolean legacy){
    int len = read.i();
    payloads.clear();
    for(int i = 0; i < len; i++){
      payloads.add((Payload)Payload.read(read));
    }
  }
}
