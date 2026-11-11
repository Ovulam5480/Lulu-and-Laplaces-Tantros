package lulu.world.modules;

import arc.func.*;
import arc.math.geom.*;
import arc.util.io.*;
import lulu.type.*;
import mindustry.gen.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;

public class PositionPayloadModule extends BlockModule{
  public final ModuleMultiPayloadSeq payloads;

  public PositionPayloadModule(Building building){
    payloads = new ModuleMultiPayloadSeq(building);
  }

  public int total(){
    return payloads.total();
  }

  public void add(PositionPayload payload){
    payloads.add(payload);
  }

  public void add(Payload payload){
    payloads.add(payload);
  }

  public void add(Payload payload, Vec2 currentPosition, Vec2 targetPosition){
    payloads.add(new PositionPayload(payload, currentPosition, targetPosition));
  }

  public void remove(PositionPayload payload){
    payloads.remove(payload);
  }

  public Payload take(){
    return payloads.take();
  }

  public Payload get(){
    return payloads.get();
  }

  public PositionPayload get(int index){
    return payloads.get(index);
  }

  public void clear(){
    payloads.clear();
  }

  public void each(Cons<PositionPayload> cons){
    payloads.each(cons);
  }

  public boolean isEmpty(){
    return payloads.isEmpty();
  }

  public Iterable<PositionPayload> iterate(){
    return payloads;
  }

  @Override
  public void write(Writes write){
  }

  @Override
  public void read(Reads read, boolean legacy){
  }
}
