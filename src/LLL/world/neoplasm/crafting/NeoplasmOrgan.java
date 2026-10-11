package LLL.world.neoplasm.crafting;

import LLL.content.*;
import LLL.world.blockcomp.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmOrgan extends Block implements NeoplasmBlockComp, ProducerBlockComp{
  public float neoplasmScaleSpeed = 0.02f;
  public float targetScale = 0.7f;
  public float degenerateTime = 600f;
  public boolean autoSelect = true;
  public boolean canSelect = false;
  public boolean shouldConfig = false;
  public float warmupSpeed = 0.008f;

  public Sound craftedSound = OvulamSounds.factorySounds.random();

  public NeoplasmOrgan(String name){
    super(name);
    size = 3;
    itemCapacity = 20;
    liquidCapacity = 50;
  }

  @Override
  public void setBars(){
    super.setBars();

    addBar("sleepTime", (NeoplasmOrganBuild b) -> new Bar("bar.neoplasm", Pal.accent, () -> b.degenerate / 600));
  }

  public class NeoplasmOrganBuild extends ProducerBuildNeoplasmBuildBuilding{
    public float degenerate;

    @Override
    public void draw(){
      Draw.scl(neoplasmScale());
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }

    @Override
    public void update(){
      if(neuron() != null){
        if(warmup() > 0.01f){
          degenerate = 0;
        }else{
          float degenerated = degenerate + Time.delta;

          if(degenerated > degenerateTime){
            suicide();
          }else{
            degenerate = degenerated;
          }
        }
      }
    }

    @Override
    public void craftTrigger(){
      super.craftTrigger();
      neoplasmScale(targetScale);
      updateScale(true);
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.f(degenerate);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      degenerate = read.f();
    }
  }
}
