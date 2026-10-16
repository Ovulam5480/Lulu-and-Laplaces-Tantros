package lulu.ctype.packet.packetTypes;

import lulu.*;
import lulu.entities.gen.*;
import lulu.graphics.*;
import mindustry.gen.*;
import mindustry.type.*;

public class MetaBallPacketType extends SinglePacketType{
  public MetaBallPacketType(String name){
    super(name);

    resourceClass = Liquid.class;
  }

  @Override
  public void draw(Packetc packet){
    super.draw(packet);
  }

  @Override
  public Packetc create(Teamc owner, float x, float y){
    Packetc packet = super.create(owner, x, y);
    LuluMod.graphics.manager.metaBalls.add((MetaBallManager.MetaBall)packet);
    return packet;
  }
}
