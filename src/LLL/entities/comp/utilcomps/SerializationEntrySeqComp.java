package LLL.entities.comp.utilcomps;

import LLL.ctype.packet.*;
import LLL.io.*;
import arc.struct.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class SerializationEntrySeqComp implements Entityc{
  public transient Seq<PacketEntry> packets = new Seq<>();
  public transient Seq<OvulamTypeIO.PacketEntryBox> tmpBoxes = new Seq<>();

  @Override
  public void write(Writes write){
    OvulamTypeIO.writePacketEntrys(write, packets);
  }

  @Annotations.Extend(Buildingc.class)
  public void read(Reads read, byte revision){
    OvulamTypeIO.readPacketEntryBoxes(read, tmpBoxes);
  }

  @Override
  public void read(Reads read){
    OvulamTypeIO.readPacketEntryBoxes(read, tmpBoxes);
  }

  @Override
  public void afterReadAll(){
    OvulamTypeIO.unboxPacketEntryBoxes(tmpBoxes, packets);
  }
}
