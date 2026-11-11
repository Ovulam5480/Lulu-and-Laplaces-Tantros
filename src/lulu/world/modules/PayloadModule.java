package lulu.world.modules;

import arc.util.io.*;
import lulu.type.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;

public class PayloadModule extends BlockModule{
  public ModulePayloadSeq payloads;

  public PayloadModule(Team team){
    payloads = new ModulePayloadSeq(team);
  }

  public int total(){
    return payloads.total();
  }

  public void add(Payload payload){
    payloads.add(payload);
  }

  public int amountOf(UnlockableContent type){
    return payloads.get(type);
  }

  public Payload take(){
    return payloads.isEmpty() ? null : payloads.take();
  }

  public Payload get(){
    return payloads.isEmpty() ? null : payloads.peek();
  }

  public Payload get(UnlockableContent type){
    return payloads.getPayload(type);
  }

  public Payload get(int i){
    return payloads.payloads.get(i);
  }

  public void remove(UnlockableContent type){
    payloads.remove(type);
  }

  public void remove(Payload payload){
    payloads.remove(payload);
  }

  public void removeAll(UnlockableContent type){
    payloads.removeAll(type);
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
    payloads.write(write);
  }

  @Override
  public void read(Reads read, boolean legacy){
    payloads.read(read);
  }
}
