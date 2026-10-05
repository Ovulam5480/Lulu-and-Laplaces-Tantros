package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.lib.gdxAI.btree.*;
import LLL.util.struct.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

public class GrowBulletDamage extends GrowLeafTask{
  public GridIntMap<Float> damageHeatmap = new GridIntMap<>();
  //public Ability

  public GrowBulletDamage(BaseTurret turret){

  }

  public GrowBulletDamage(Block block, float range){
    Events.on(NeoplasmBulletDamage.class, e -> {
      if(e.neuron == neuron){

      }
    });
  }

  @Override
  public Status grow(){
    return null;
  }

  @Override
  public void write(Writes write){

  }

  @Override
  public void read(Reads read){

  }

  @Override
  protected Task<NeoplasmBehavior> copyTo(Task<NeoplasmBehavior> task){
    return null;
  }

  @Override
  public void draw(){

  }

  @Override
  public void initVisible(){
  }

  public static class NeoplasmBulletDamage{
    public NeoplasmNeuron.NeoplasmNeuronBuild neuron;
    public NeoplasmBuildModule build;
    public Bullet bullet;

    public void set(NeoplasmNeuron.NeoplasmNeuronBuild neuron, NeoplasmBuildModule build, Bullet bullet){
      this.neuron = neuron;
      this.build = build;
      this.bullet = bullet;
    }
  }
}
