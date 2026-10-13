package LLL.world.blocks.distribution;

import LLL.ctype.packet.*;
import arc.math.geom.*;

//todo 应用场景疑似被分流器覆盖
public class PacketConveyorRouter extends PacketConveyor{
  public PacketConveyorRouter(String name){
    super(name);

    outputTop = false;
  }

  public class PacketConveyorRouterBuild extends PacketConveyorBuild{
    public int nextDir = 0;

    @Override
    public void setPacketTarget(PacketEntry packetEntry, Vec2 targetToSet){//todo 前端封闭时的补充?
      packetTarget(packetEntry, targetToSet, nextDir == 0, nextDir == 1);
      while(true){
        nextDir++;
        if(nextDir == 1 && hasBlends[1]){
          break;
        }else if(nextDir == 2 && hasBlends[3]){
          break;
        }else if(nextDir == 3){
          nextDir = 0;
          break;
        }
      }
    }
  }
}
