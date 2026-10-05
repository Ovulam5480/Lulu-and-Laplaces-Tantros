package LLL.world.blocks.distribution;

import LLL.world.blocks.packet.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

//封包中继器, 负责作为无向图的节点
public class PacketRelay extends PacketBlock{
  public float range = 80f;

  public PacketRelay(String name){
    super(name);

    configurable = true;
    rotate = false;
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);

    Drawf.circles(x * tilesize + offset, y * tilesize + offset, range * tilesize);
  }

  public class PacketRelayBuild extends PacketBlockBuild{
  }
}
