package LLL.world.buildComp;

import LLL.entities.gen.*;
import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.type.neoplasm.*;
import LLL.world.blockcomp.*;
import LLL.world.modules.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.modules.*;

@Annotations.EntityComponent
abstract class NeoplasmBuildComp implements SerializationGameEntityc, OvulamBuildingc{
  static float scl = 60 / 3.14f * 1.6f;
  static GrowBulletDamage.NeoplasmBulletDamage bulletDamage = new GrowBulletDamage.NeoplasmBulletDamage();

  @Annotations.Import
  Block block;
  @Annotations.Import
  Tile tile;
  @Annotations.Import
  ItemModule items;
  @Annotations.Import
  LiquidModule liquids;
  @Annotations.Import
  float health;
  @Annotations.Import
  int rotation;

  public transient int readSize;
  public transient NeoplasmNeuron.NeoplasmNeuronBuild neuron;
  public transient boolean neuronValid = true;
  public transient Building dumpTarget;
  public transient NeoplasmBuildc parent;
  public transient Seq<NeoplasmBuildc> children = new Seq<>();
  public transient boolean suicide;
  public transient boolean updateScale;

  //todo r/w
  public NeoplasmLiquidModule neoplasm;
  public float neoplasmScale;
  public float dumpLiquidTimer;
  public float dumpTimer;
  public float environmentHandler;
  public boolean neoplasmActivity;
  public boolean thrombus;
  public boolean isSourceOrgan;

  public NeoplasmBlockComp blockAs(){
    return (NeoplasmBlockComp)block;
  }

  public PrefrontalCortex cortex(){
    return neuron.cortex;
  }

  public void changeThrombus(boolean thrombus){
    this.thrombus = thrombus;
    cortex().handleThrombusChange(self(), thrombus);
  }

  public boolean shouldNetworkUpdate(){
    return !Vars.net.active() || Vars.net.server();
  }

  public void onRemoved(){
    if(neuron != null && shouldNetworkUpdate()){
      cortex().handleOwnedBlock(self(), tileX(), tileY(), rotation, false);
      if(suicide){
        cortex().handleSuicide(self());
      }else{
        cortex().handleKill(self());
      }

      changeThrombus(false);
    }

    PrefrontalCortex.environmentState.remove(self());
  }

  @Annotations.Replace(999)
  public boolean canPickup(){
    return false;
  }

  @Annotations.Replace(999)
  public void updatePayload(Unit unitHolder, Building buildingHolder){
    if(neuron != null){
      suicide();
    }else{
      update();
    }
  }

  public void suicide(){
    suicide = true;
    kill();
  }

  @Annotations.Replace
  public boolean productionValid(){
    return neoplasmActivity;
  }

  @Annotations.Replace
  public boolean acceptItem(Building source, Item item){
    return items.get(item) < getMaximumAccepted(item);
  }

  @Annotations.Replace(999)
  public boolean acceptLiquid(Building source, Liquid liquid){
    return liquid == Liquids.neoplasm;
  }

  @Annotations.Replace(999)
  public void handleLiquid(Building source, Liquid liquid, float amount){
    if(liquid == Liquids.neoplasm){
      neoplasm.addClamp(amount, neoplasm.liquidCapacity);
    }else{
      liquids.add(liquid, amount);
    }
  }

  public void onProximityUpdate(){
    if(dumpTarget != null && dumpTarget.tile.build != dumpTarget){
      dumpTarget = null;
    }

    if(neuron != null){
      cortex().addParentChildren(self());
    }
  }

  public void damage(Bullet bullet, Team source, float damage){
    bulletDamage.set(neuron, self(), bullet);
    Events.fire(bulletDamage);
  }

  public void update(){
    if(isSourceOrgan || updateScale) updateNeoplasmScale();

    Building self = self();
    float delta = self.delta();

    if(neuron != null && !cortex().checkValid(self())){//例如载荷
      suicide();
      return;
    }

    float liquidAmount = neoplasm.amount();

    neoplasmActivity = liquidAmount != 0;

    liquidAmount = updateEnvironmentHandler(self, delta, liquidAmount);
    liquidAmount = updateNeoplasmHeal(self, delta, liquidAmount);

    neoplasm.setClamp(liquidAmount, neoplasm.liquidCapacity);

//        if(neuron == null && neoplasmActivity){
//            updateLiquidDump(self, delta);
//        }
    updateItemDump(self, delta);
  }

//    public void updateLiquidDump(Building self, float delta){
//        dumpLiquidTimer += delta;
//
//        if(dumpLiquidTimer > 1){
//            self.dumpLiquid(Liquids.neoplasm);
//            dumpLiquidTimer--;
//        }
//    }

  public float updateEnvironmentHandler(Building self, float delta, float liquidAmount){
    if(isSourceOrgan){
      liquidAmount += blockAs().neoplasmAbsorbMulti() * 100 / 60f * delta;
    }else{
      liquidAmount += environmentHandler * delta;
    }

    return liquidAmount;
  }

  public float updateNeoplasmHeal(Building self, float delta, float liquidAmount){
    if(neoplasmActivity){
      if(health < block.health){
        float canHeal = Math.min(block.health - health, liquidAmount * blockAs().neoplasmHealScale());
        float toHeal = Mathf.clamp(canHeal, 0, blockAs().maxHealOnce() * delta);

        self.heal(toHeal);
        liquidAmount -= toHeal / blockAs().neoplasmHealScale();
        return liquidAmount;
      }
    }else{
      self.damage(2f * delta);
    }

    return liquidAmount;
  }

  public void updateItemDump(Building self, float delta){
    dumpTimer += delta;
    if(dumpTimer > 1f){
      if(neuron == null){
        self.dump();
      }else if(items.any()){
        Item todump = null;

        for(Item item : Vars.content.items()){
          if(items.has(item)){
            todump = item;
            break;
          }
        }

        if(todump != null){
          if(dumpTarget != null){
            if(dumpTarget.acceptItem(self, todump)){
              dumpTarget.handleItem(self, todump);
              items.remove(todump, 1);
            }
          }else{
            cortex().getItemDumpTarget(self());
          }
        }
      }
      dumpTimer = 0;
    }
  }

  public void updateNeoplasmScale(){
    neoplasmScale = Mathf.approach(neoplasmScale, isSourceOrgan ? Mathf.sin(Time.time, scl / 2, 0.1f) + 1.1f : 1f, blockAs().neoplasmScaleSpeed() * Time.delta);
    if(!isSourceOrgan && Mathf.equal(neoplasmScale, 1f, 0.01f)){
      updateScale = false;
      neoplasmScale = 1;
    }
  }

  public void created(){
    neoplasm = new NeoplasmLiquidModule(block.liquidCapacity);
    neoplasm.add(1.0E-4F);
    updateScale = true;

    isSourceOrgan = tile.floor().liquidDrop == Liquids.neoplasm
      || tile.floor().liquidDrop == Liquids.water
      || (tile.floor() instanceof SteamVent sv && sv.isCenterVent(tile));

    PrefrontalCortex.environmentState.add(self());
  }

  public void configured(Unit builder, Object value){
    if(value instanceof NeoplasmNeuron.NeoplasmNeuronBuild neuron){
      this.neuron = neuron;

      cortex().handleOwnedBlock(self(), tileX(), tileY(), rotation, true);
      if(blockAs().requestStructure()){
        cortex().requestStructure(tileX(), tileY(), false);
      }

      if(isSourceOrgan){
        cortex().handleSource(self());
      }

      updateProximity();
    }
  }

  abstract void updateProximity();

  @Override
  public void addToWrite(Seq<Entityc> toWrite){
    toWrite.add(neuron);
  }

  @Override
  public void getFromRead(Queue<Entityc> toRead){
    configured(null, toRead.first());
    toRead.removeFirst();
    onProximityUpdate();

    if(thrombus){
      changeThrombus(true);
    }
  }

  public abstract int getMaximumAccepted(Item item);
}
