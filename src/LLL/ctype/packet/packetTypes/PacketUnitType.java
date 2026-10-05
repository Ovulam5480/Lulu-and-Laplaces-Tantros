package LLL.ctype.packet.packetTypes;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import mindustry.gen.*;
import mindustry.type.*;

public abstract class PacketUnitType extends PacketType{
  public UnitType unitType;

  public PacketUnitType(String name, UnitType unitType){
    super(name);
    this.unitType = unitType;

    unitType.constructor = EntityMapping.map(LuluMod.modName + name);
  }

  public PacketUnitType(UnitType unitType){
    this(unitType.name, unitType);
  }

  @Override
  public void draw(Packetc packet){
  }

//    private Packetc setType(Team team){
//        PacketUnit packetUnit = (PacketUnit) unitType.setType(team);
//        packetUnit.setPacketType(this);
//        return packetUnit;
//    }

//    @Override
//    public Packetc setType(Teamc owner, float x, float y){
//        Packetc packet = setType(owner.team());
//
//        packet.owner(owner);
//        packet.set(x, y);
//        packet.add();
//
//        return packet;
//    }
}
