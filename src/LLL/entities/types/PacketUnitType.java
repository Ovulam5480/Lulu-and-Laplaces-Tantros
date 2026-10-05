package LLL.entities.types;

import mindustry.type.*;

public class PacketUnitType extends UnitType{
  public float packetDragResist = 1;
  public float packetBindRange = 100;

  public PacketUnitType(String name){
    super(name);
  }
}
