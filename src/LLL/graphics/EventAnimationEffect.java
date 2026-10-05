package LLL.graphics;


import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

//废案, 但是指不定有能利用的地方
public class EventAnimationEffect extends Effect{
  public static TextureRegion icon;

  public Cons<EffectContainer> r = e -> {
    Fill.circle(getLerpX(e.fin()), getLerpY(e.fin()), tilesizeScl() * 16);
    Draw.scl(tilesizeScl());
    Draw.rect(icon, getLerpX(e.fin()), getLerpY(e.fin()), Time.time * 16);
    Draw.scl();
  };

  public EventAnimationEffect(){
    layer = Layer.playerName;
    lifetime = 300f;
    clip = 65535;
    //EventEffects.effectSeq.add(this);
  }

  public void load(){
    icon = UnitTypes.mono.fullIcon;
    renderer = r;
  }


  public void create(){
    super.at(0, 0);
  }

  public static float cameraX(){
    return camera.position.x;
  }

  public static float cameraY(){
    return camera.position.y;
  }

  public static float cameraW(){
    return camera.width;
  }

  public static float cameraH(){
    return camera.height;
  }

  public static float tilesizeScl(){
    return tilesize / Vars.renderer.getDisplayScale();
  }

  public static float cameraRight(){
    return camera.position.x + cameraW() / 2;
  }

  public static float cameraLeft(){
    return camera.position.x - cameraW() / 2;
  }

  public static float cameraTop(){
    return camera.position.y + cameraH() / 2;
  }

  public static float cameraBottom(){
    return camera.position.y - cameraH() / 2;
  }

  public static float getLerpX(float progress){
    return Mathf.lerp(cameraLeft(), cameraRight(), progress);
  }

  public static float getLerpY(float progress){
    return Mathf.lerp(cameraBottom(), cameraTop(), progress);
  }
}
