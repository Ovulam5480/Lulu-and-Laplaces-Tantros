package LLL.world.blocks.distribution;

import LLL.ctype.packet.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.world.*;

import java.util.*;

public class PacketConveyorDiverter extends PacketConveyor{
  public PacketConveyorDiverter(String name){
    super(name);
    outputTop = false;
  }

  public class PacketConveyorDiverterBuild extends PacketConveyorBuild{
    @Override
    public void setPacketTarget(PacketEntry packetEntry, Vec2 targetToSet){
      boolean rightBlends = hasBlends[1], leftBlends = hasBlends[3];
      boolean inRight = getQuadrant(packetEntry) < 2;

      if((!inRight && leftBlends) || (inRight && rightBlends)){
        packetTarget(packetEntry, targetToSet, false, inRight);
      }else{
        packetTarget(packetEntry, targetToSet, true, false);
      }
    }


    public void setEdges(){
      Arrays.fill(hadEdge, true);

      for(int i = 0; i < edges.length; i++){
        Point2 edge = edges[i];
        Tile t = Vars.world.tile(tile.x + edge.x, tile.y + edge.y);

        if((t != null && hasEdgeFrom(t.build) || (forceOutput && rotation == i / size))
          && !(hasBlends[1] && hasBlends[3] && rotation == i / size)){
          hadEdge[i] = false;
        }
      }
    }
  }
}
