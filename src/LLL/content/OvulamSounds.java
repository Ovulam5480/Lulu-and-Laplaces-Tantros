package LLL.content;

import arc.audio.*;
import arc.struct.*;
import mindustry.*;

public class OvulamSounds{
  public static ObjectMap<String, Seq<Sound>> soundMap = new ObjectMap<>();
  public static Seq<Sound>
    spawnSounds = new Seq<>(),
    factorySounds = new Seq<>(),
    unitDeathSounds = new Seq<>(),
    trichimellaShootSounds = new Seq<>(),
  //            blockBreakSounds = new Seq<>(),
  shootSounds = new Seq<>(),
  //            shootSoundsLarge = new Seq<>(),
  placeSounds = new Seq<>(),
    breakSounds = new Seq<>();

  public static void put(){
    putSounds("哔哔嗞", 1, trichimellaShootSounds);
//        putSounds("发射什么的声音大", 5, shootSoundsLarge);
    putSounds("发射什么的声音小", 5, shootSounds);
    putSounds("呼噜噜", 2, factorySounds);
    putSounds("就是那个的声音", 3, breakSounds);
    putSounds("什么东西在叫", 1, unitDeathSounds);
    putSounds("水", 5, placeSounds);
    putSounds("应该是呕吐", 5, spawnSounds);
//        putSounds("粘糊糊", 2, blockBreakSounds);
  }

  public static void putSounds(String name, int max, Seq<Sound> sounds){
    soundMap.put(name, sounds);

    for(int i = 1; i <= max; i++){
      Sound sound = Vars.tree.loadSound(name + i);

      sound.setMinInterval((long)(sound.getLength() * 1000 * 1.1f));

      sounds.add(sound);
    }
  }

  public static void load(){
    put();
    soundMap = null;
  }
}
