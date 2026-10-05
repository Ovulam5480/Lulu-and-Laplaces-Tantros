package LLL.io;

import LLL.*;
import LLL.content.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.io.*;

@SuppressWarnings("unused")
@Annotations.TypeIOHandler
public class OvulamTypeIO{
  TypeIO typeIO;

  public static void writePacketType(Writes writes, PacketType packetType){
    writes.str(packetType.name);
  }

  public static PacketType readPacketType(Reads reads){
    String name = reads.str();
    //todo ohnoPacketType
    return OvulamPacketTypes.packetTypes.find(pt -> pt.name.equals(name));
  }

  public static void writePacketOwner(Writes writes, Teamc teamc){
    TypeIO.writeEntity(writes, teamc);
  }

  public static Teamc readPacketOwner(Reads reads){
    return TypeIO.readEntity(reads);
  }

  public static void writePacketc(Writes writes, Packetc packetc){
    writes.i(packetc.id());
  }

  //不要使用
  @SuppressWarnings("unchecked")
  public static <T extends Packetc> T readPacketc(Reads reads){
    int id = reads.i();
    return (T)LuluMod.oIndexer.all.find(p -> p.id() == id);
  }

  public static PacketBox readPacketBox(Reads reads){
    return new PacketBox(reads.i());
  }

  public static void writePacketEntry(Writes writes, PacketEntry packetEntry){
    writePacketc(writes, packetEntry.packet);
    TypeIO.writeVec2(writes, packetEntry.targetPos);
  }

  //不要使用
  public static PacketEntry readPacketEntry(Reads reads){
    PacketEntry packetEntry = PacketEntry.create();

    packetEntry.packet = readPacketc(reads);
    packetEntry.setTargetPos(TypeIO.readVec2(reads));

    return packetEntry;
  }

  public static PacketEntryBox readPacketEntryBox(Reads reads){
    return new PacketEntryBox(readPacketBox(reads), TypeIO.readVec2(reads));
  }

  public static void writePackets(Writes writes, Seq<Packetc> packets){
    writes.i(packets.size);
    for(Packetc packet : packets){
      writePacketc(writes, packet);
    }
  }

  public static Seq<Packetc> readPackets(Reads reads){
    Seq<Packetc> packets = new Seq<>();

    int size = reads.i();
    for(int i = 0; i < size; i++){
      packets.add((Packetc)readPacketc(reads));
    }
    return packets;
  }

  public static void readPacketBoxes(Reads reads, Seq<PacketBox> packetBoxes){
    int size = reads.i();
    for(int i = 0; i < size; i++){
      packetBoxes.add(readPacketBox(reads));
    }
  }

  public static void writePacketEntrys(Writes writes, Seq<PacketEntry> packetEntrys){
    writes.i(packetEntrys.size);
    for(PacketEntry packetEntry : packetEntrys){
      writePacketEntry(writes, packetEntry);
    }
  }

  public static Seq<PacketEntry> readPacketEntrys(Reads reads){
    Seq<PacketEntry> packetEntrys = new Seq<>();

    int size = reads.i();
    for(int i = 0; i < size; i++){
      PacketEntry packetEntry = readPacketEntry(reads);
      packetEntrys.add(packetEntry);
    }
    return packetEntrys;
  }

  public static void readPacketEntryBoxes(Reads reads, Seq<PacketEntryBox> packetEntryBoxes){
    int size = reads.i();
    for(int i = 0; i < size; i++){
      packetEntryBoxes.add(readPacketEntryBox(reads));
    }
  }

  public static void unboxPacketBoxes(Seq<PacketBox> packetBoxes, Seq<Packetc> packets){
    for(PacketBox packetBox : packetBoxes){
      packets.add(packetBox.unbox());
    }
  }

  public static void unboxPacketEntryBoxes(Seq<PacketEntryBox> packetEntryBoxes, Seq<PacketEntry> packetEntrys){
    for(PacketEntryBox packetEntryBox : packetEntryBoxes){
      packetEntrys.add(packetEntryBox.unbox());
    }
  }

  public static void writeVec3Seq(Writes writes, Seq<Vec3> vec3s){
    writes.i(vec3s.size);
    for(Vec3 vec3 : vec3s){
      writes.f(vec3.x);
      writes.f(vec3.y);
      writes.f(vec3.z);
    }
  }

  public static Seq<Vec3> readVec3Seq(Reads reads){
    Seq<Vec3> vec3s = new Seq<>();
    int size = reads.i();
    for(int i = 0; i < size; i++){
      vec3s.add(new Vec3(reads.f(), reads.f(), reads.f()));
    }
    return vec3s;
  }

  public static void writeResourceStacks(Writes writes, Seq<ResourceStack<?>> resourceStacks){
    ResourceStackManager.writeResourceStacks(writes, resourceStacks);
  }

  public static Seq<ResourceStack<?>> readResourceStacks(Reads reads){
    return ResourceStackManager.readResourceStacks(reads);
  }

  public static class PacketBox implements TypeIO.Boxed<Packetc>{
    public int id;

    public PacketBox(int id){
      this.id = id;
    }

    @Override
    public Packetc unbox(){
      return LuluMod.oIndexer.all.find(p -> p.id() == id);
    }
  }

  public static class PacketEntryBox implements TypeIO.Boxed<PacketEntry>{
    PacketBox packet;
    Vec2 targetPos;

    public PacketEntryBox(PacketBox packet, Vec2 targetPos){
      this.packet = packet;
      this.targetPos = targetPos;
    }

    @Override
    public PacketEntry unbox(){
      return PacketEntry.create(packet.unbox(), targetPos);
    }
  }
}
