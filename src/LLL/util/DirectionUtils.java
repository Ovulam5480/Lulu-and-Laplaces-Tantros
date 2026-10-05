package LLL.util;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

public class DirectionUtils{
  public static int getDirection(Position self, Position target, float rotDeg){
    return Mathf.floor(Tmp.v1.set(target).sub(self).rotate(-rotDeg + 45).angle() % 360 / 90);
  }

  public static int getQuadrant(Position self, Position target, float rotDeg){
    return Mathf.floor(Tmp.v1.set(target).sub(self).rotate(-rotDeg).angle() / 90);
  }

  public static boolean isInDirection(Position self, Position target, float rotDeg, int direction){
    return getDirection(self, target, rotDeg) == direction;
  }

  public static boolean inTop(Position self, Position target, float rotDeg){
    return isInDirection(self, target, rotDeg, 0);
  }

  public static boolean inLeft(Position self, Position target, float rotDeg){
    return isInDirection(self, target, rotDeg, 1);
  }

  public static boolean inBottom(Position self, Position target, float rotDeg){
    return isInDirection(self, target, rotDeg, 2);
  }

  public static boolean inRight(Position self, Position target, float rotDeg){
    return isInDirection(self, target, rotDeg, 3);
  }
}