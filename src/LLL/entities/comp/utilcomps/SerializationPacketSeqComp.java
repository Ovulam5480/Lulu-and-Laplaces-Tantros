package LLL.entities.comp.utilcomps;

import LLL.entities.gen.*;
import LLL.io.*;
import arc.struct.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class SerializationPacketSeqComp implements Entityc{
  public transient Seq<Packetc> packets = new Seq<>();
  public transient Seq<OvulamTypeIO.PacketBox> tmpBoxes = new Seq<>();

  @Override
  public void write(Writes write){
    OvulamTypeIO.writePackets(write, packets);
  }

  @Annotations.Extend(Buildingc.class)
  public void read(Reads read, byte revision){
    OvulamTypeIO.readPacketBoxes(read, tmpBoxes);
  }

  @Override
  public void read(Reads read){
    OvulamTypeIO.readPacketBoxes(read, tmpBoxes);
  }

  @Override
  public void afterReadAll(){
    OvulamTypeIO.unboxPacketBoxes(tmpBoxes, packets);
  }
}