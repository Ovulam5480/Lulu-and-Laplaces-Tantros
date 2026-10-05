package LLL.util;

import arc.*;
import arc.math.*;
import mindustry.*;

public class Converter{
  public static float worldCoordToGLSL(float worldCoord, boolean isX){
    return worldCoordCurve(worldCoord, isX) * 2 - 1;
  }

  public static float worldCoordToScreen(float worldCoord, boolean isX){
    return (isX ? Core.graphics.getWidth() : Core.graphics.getHeight()) * worldCoordCurve(worldCoord, isX);
  }

  public static float worldCoordCurve(float worldCoord, boolean isX){
    if(isX){
      return Mathf.curve(worldCoord, Core.camera.position.x - Core.camera.width / 2, Core.camera.position.x + Core.camera.width / 2);
    }else{
      return Mathf.curve(worldCoord, Core.camera.position.y - Core.camera.height / 2, Core.camera.position.y + Core.camera.height / 2);
    }
  }

  public static float worldSizeToScreen(float worldSize){
    return worldSize * Vars.renderer.camerascale;
  }
}
