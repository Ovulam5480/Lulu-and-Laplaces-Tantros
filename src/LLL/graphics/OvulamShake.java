package LLL.graphics;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.game.*;

import static arc.Core.*;

public class OvulamShake{
  public float
    //intensity for screen shake
    shakeIntensity,
  //reduction rate of screen shake
  shakeReduction,
  //current duration of screen shake
  shakeTime;
  private Vec2 camShakeOffset = new Vec2();

  public OvulamShake(){
    Events.on(EventType.ResetEvent.class, e -> {
      shakeTime = shakeIntensity = shakeReduction = 0f;
      camShakeOffset.setZero();
    });

    Events.run(EventType.Trigger.preDraw, () -> {
      if(shakeTime > 0){
        float intensity = shakeIntensity * 0.75f;
        camShakeOffset.setToRandomDirection().scl(Mathf.random(intensity));
        camera.position.add(camShakeOffset);
        shakeIntensity -= shakeReduction * Time.delta;
        shakeTime -= Time.delta;
        shakeIntensity = Mathf.clamp(shakeIntensity, 0f, 100f);
      }else{
        camShakeOffset.setZero();
        shakeIntensity = 0f;
      }
    });

    Events.run(EventType.Trigger.postDraw, () -> {
      camera.position.sub(camShakeOffset);
    });
  }

  public void shake(float intensity, float duration){
    shakeIntensity = Math.max(shakeIntensity, Mathf.clamp(intensity, 0, 100));
    shakeTime = Math.max(shakeTime, duration);
    shakeReduction = shakeIntensity / shakeTime;
  }

  public void update(){
    if(shakeTime > 0){
      float intensity = shakeIntensity * (settings.getInt("screenshake", 4) / 4f) * 0.75f;
      camShakeOffset.setToRandomDirection().scl(Mathf.random(intensity));
      camera.position.add(camShakeOffset);
      shakeIntensity -= shakeReduction * Time.delta;
      shakeTime -= Time.delta;
      shakeIntensity = Mathf.clamp(shakeIntensity, 0f, 100f);
    }else{
      camShakeOffset.setZero();
      shakeIntensity = 0f;
    }
  }
}
