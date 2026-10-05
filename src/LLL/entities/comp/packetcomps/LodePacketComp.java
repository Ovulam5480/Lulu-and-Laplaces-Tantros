package LLL.entities.comp.packetcomps;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import arc.graphics.g2d.*;
import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class LodePacketComp implements Packetc, Drawc{
  @Annotations.Import
  PacketType packetType;
  TextureRegion LodeRegion;

  @Override
  public void add(){
    LuluMod.oIndexer.lodePacketTree.insert(self());
  }

  //todo 总不能所有封包子类都写一遍
  @Override
  public void draw(){
    packetType.draw(self());
  }

  @Override
  @Annotations.Replace
  public float clipSize(){
    return packetType.clipSize;
  }
}
