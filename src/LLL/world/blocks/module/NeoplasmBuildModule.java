package LLL.world.blocks.module;

import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.type.neoplasm.*;
import LLL.world.modules.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import universecore.annotations.*;

@SuppressWarnings("unused")
public interface NeoplasmBuildModule extends SerializationGameEntityModule, Posc{
  float scl = 60 / 3.14f * 1.6f;
  GrowBulletDamage.NeoplasmBulletDamage bulletDamage = new GrowBulletDamage.NeoplasmBulletDamage();

  default NeoplasmBlockModule blockAs(){
    return (NeoplasmBlockModule)getBlock();
  }

  default PrefrontalCortex cortex(){
    return neuron().cortex;
  }

  @Annotations.BindField("readSize")
  default int readSize(){
    return 0;
  }

  @Annotations.BindField("readSize")
  default void setReadSize(int readSize){
  }

  @Annotations.BindField("neuron")
  default NeoplasmNeuron.NeoplasmNeuronBuild neuron(){
    return null;
  }

  @Annotations.BindField("neuron")
  default void setNeuron(NeoplasmNeuron.NeoplasmNeuronBuild neuron){
  }

  @Annotations.BindField(value = "neuronValid", initialize = "true")
  default boolean neuronValid(){
    return false;
  }

  @Annotations.BindField("neuronValid")
  default void setNeuronValid(boolean neuron){
  }

  @Annotations.BindField("neoplasmScale")
  default void setNeoplasmScale(float scale){
  }

  @Annotations.BindField("neoplasmScale")
  default float neoplasmScale(){
    return 0;
  }

  @Annotations.BindField("dumpLiquidTimer")
  default void setDumpLiquidTimer(float dumpLiquidTimer){
  }

  @Annotations.BindField("dumpLiquidTimer")
  default float dumpLiquidTimer(){
    return 0;
  }

  @Annotations.BindField("suicide")
  default boolean getSuicide(){
    return false;
  }

  @Annotations.BindField("suicide")
  default void setSuicide(boolean suicide){
  }

  @Annotations.BindField("dumpTimer")
  default void setDumpTimer(float dumpTimer){
  }

  @Annotations.BindField("dumpTimer")
  default float dumpTimer(){
    return 0;
  }

  @Annotations.BindField("environmentHandler")
  default float environmentHandler(){
    return 0;
  }

  @Annotations.BindField("environmentHandler")
  default void setEnvironmentHandler(float environmentHandler){
  }

  @Annotations.BindField("neoplasmActivity")
  default void setNeoplasmActivity(boolean activity){
  }

  @Annotations.BindField("neoplasmActivity")
  default boolean neoplasmActivity(){
    return false;
  }

  @Annotations.BindField("thrombus")
  default boolean thrombus(){
    return false;
  }

  @Annotations.BindField("thrombus")
  default void setThrombus(boolean thrombus){
  }

  @Annotations.BindField(value = "isSourceOrgan")
  default boolean isSourceOrgan(){
    return false;
  }

  @Annotations.BindField(value = "isSourceOrgan")
  default void setIsSourceOrgan(boolean isSourceOrgan){
  }

  default void changeThrombus(boolean thrombus){
    setThrombus(thrombus);
    cortex().handleThrombusChange(this, thrombus);
  }

  default boolean shouldNetworkUpdate(){
    return !Vars.net.active() || Vars.net.server();
  }

  @Annotations.MethodEntry(entryMethod = "onRemoved")
  default void handleReomve(){
    if(neuron() != null && shouldNetworkUpdate()){
      cortex().handleOwnedBlock(this, tileX(), tileY(), getBuilding().rotation, false);
      if(getSuicide()){
        cortex().handleSuicide(this);
      }else{
        cortex().handleKill(this);
      }

      changeThrombus(false);
    }

    PrefrontalCortex.environmentState.remove(this);
  }

  @Annotations.MethodEntry(entryMethod = "canPickup")
  default boolean canPickupNeoplasm(){
    return false;
  }

  @Annotations.MethodEntry(entryMethod = "updatePayload", paramTypes = {"mindustry.gen.Unit -> unitHolder", "mindustry.gen.Building -> buildingHolder"}, override = true)
  default void updatePayloadNeoplasm(Unit unitHolder, Building buildingHolder){
    if(neuron() != null){
      suicide();
    }else{
      getBuilding().update();
    }
  }

  default void suicide(){
    setSuicide(true);
    getBuilding().kill();
  }

  @Annotations.MethodEntry(entryMethod = "productionValid", override = true)
  default boolean neoplasmValid(){
    return neoplasmActivity();
  }

//    @Annotations.MethodEntry(entryMethod = "acceptLiquid", override = true, paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Liquid -> liquid"})
//    default boolean acceptNeoplastic(Building source, Liquid liquid){
//        return liquid == neoplasmLiquid;
//    }

  @Annotations.MethodEntry(entryMethod = "acceptItem", override = true, paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Item -> item"})
  default boolean acceptNeoplasticItem(Building source, Item item){
    return items().get(item) < getBuilding().getMaximumAccepted(item);
  }

  @Annotations.MethodEntry(entryMethod = "acceptLiquid", override = true, paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Liquid -> liquid"})
  default boolean acceptNeoplasticLiquid(Building source, Liquid liquid){
    return liquid == Liquids.neoplasm;
  }

  @Annotations.MethodEntry(entryMethod = "handleLiquid", override = true, paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Liquid -> liquid", "float -> amount"})
  default void handleNeoplasticLiquid(Building source, Liquid liquid, float amount){
    if(liquid == Liquids.neoplasm){
      neoplasm().addClamp(amount, neoplasm().liquidCapacity);
    }else{
      getBuilding().liquids.add(liquid, amount);
    }
  }

  @Annotations.BindField("dumpTarget")
  default Building dumpTarget(){
    return null;
  }

  @Annotations.BindField("dumpTarget")
  default void setDumpTarget(Building dumpTarget){
  }

  @Annotations.BindField("parent")
  default NeoplasmBuildModule parent(){
    return null;
  }

  @Annotations.BindField("parent")
  default void setParent(NeoplasmBuildModule parent){
  }

  @Annotations.BindField(value = "children", initialize = "new arc.struct.Seq<>()")
  default Seq<NeoplasmBuildModule> children(){
    return null;
  }

  @Annotations.BindField("neoplasm")
  default void setNeoplasm(NeoplasmLiquidModule neoplasm){
  }

  @Annotations.BindField("neoplasm")
  default NeoplasmLiquidModule neoplasm(){
    return null;
  }

  @Annotations.BindField("updateScale")
  default void setUpdateScale(boolean updateScale){
  }

  @Annotations.BindField("updateScale")
  default boolean updateScale(){
    return false;
  }


  @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  default void opu(){
    if(dumpTarget() != null && dumpTarget().tile.build != dumpTarget()){
      setDumpTarget(null);
    }

    if(neuron() != null){
      cortex().addParentChildren(this);
    }
  }

  @Annotations.MethodEntry(entryMethod = "damage", paramTypes = {"mindustry.gen.Bullet -> bullet", "mindustry.game.Team -> source", "float -> damage"})
  default void damageEvents(Bullet bullet, Team source, float damage){
    bulletDamage.set(neuron(), this, bullet);
    Events.fire(bulletDamage);
  }

  @Annotations.MethodEntry(entryMethod = "update")
  default void consumeNeoplastic(){
    if(isSourceOrgan() || updateScale()) updateNeoplasmScale();

    Building self = getBuilding();
    float delta = self.delta();

    if(neuron() != null && !cortex().checkValid(this)){//例如载荷
      suicide();
      return;
    }

    float liquidAmount = neoplasm().amount();

    setNeoplasmActivity(liquidAmount != 0);

    liquidAmount = updateEnvironmentHandler(self, delta, liquidAmount);
    liquidAmount = updateNeoplasmHeal(self, delta, liquidAmount);

    neoplasm().setClamp(liquidAmount, neoplasm().liquidCapacity);

//        if(neuron() == null && neoplasmActivity()) {
//            updateLiquidDump(self, delta);
//        }
    updateItemDump(self, delta);
  }

//    default void updateLiquidDump(Building self, float delta){
//        float timer = dumpLiquidTimer() + delta;
//
//        if (timer > 1) {
//            self.dumpLiquid(neoplasmLiquid);
//            timer--;
//        }
//
//        setDumpLiquidTimer(timer);
//    }


  default float updateEnvironmentHandler(Building self, float delta, float liquidAmount){
    if(isSourceOrgan()){
      liquidAmount += blockAs().neoplasmAbsorbMulti() * 100 / 60f * delta;
    }else{
      liquidAmount += environmentHandler() * delta;
    }

    return liquidAmount;
  }

  default float updateNeoplasmHeal(Building self, float delta, float liquidAmount){
    if(neoplasmActivity()){
      if(self.health < getBlock().health){
        float canHeal = Math.min(getBlock().health - self.health, liquidAmount * blockAs().neoplasmHealScale());
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

  default void updateItemDump(Building self, float delta){
    float timer = dumpTimer() + delta;
    if(timer > 1f){
      if(neuron() == null){
        self.dump();
      }else if(items().any()){
        Item todump = null;

        for(Item item : Vars.content.items()){
          if(items().has(item)){
            todump = item;
            break;
          }
        }

        if(todump != null){
          Building target = dumpTarget();

          if(target != null){
            if(target.acceptItem(self, todump)){
              target.handleItem(self, todump);
              items().remove(todump, 1);
            }
          }else{
            cortex().getItemDumpTarget(this);
          }
        }
      }
      timer = 0;
    }
    setDumpTimer(timer);
  }

  default void updateNeoplasmScale(){
    setNeoplasmScale(Mathf.approach(neoplasmScale(), isSourceOrgan() ? Mathf.sin(Time.time, scl / 2, 0.1f) + 1.1f : 1f, blockAs().neoplasmScaleSpeed() * Time.delta));
    if(!isSourceOrgan() && Mathf.equal(neoplasmScale(), 1f, 0.01f)){
      setUpdateScale(false);
      setNeoplasmScale(1);
    }
  }

  @Annotations.MethodEntry(entryMethod = "created")
  default void initalNeoplasm(){
    setNeoplasm(new NeoplasmLiquidModule(getBlock().liquidCapacity));
    neoplasm().add(1.0E-4F);
    setUpdateScale(true);

    boolean source = getTile().floor().liquidDrop == Liquids.neoplasm
      || getTile().floor().liquidDrop == Liquids.water
      || (getTile().floor() instanceof SteamVent sv && sv.isCenterVent(getTile()));

    setIsSourceOrgan(source);

    PrefrontalCortex.environmentState.add(this);
  }

  @Annotations.MethodEntry(entryMethod = "configured", paramTypes = {"mindustry.gen.Unit -> builder", "java.lang.Object -> value"})
  default void configureNeuron(Unit builder, Object value){
    if(value instanceof NeoplasmNeuron.NeoplasmNeuronBuild neuron){
      setNeuron(neuron);

      cortex().handleOwnedBlock(this, tileX(), tileY(), getBuilding().rotation, true);
      if(blockAs().requestStructure()){
        cortex().requestStructure(tileX(), tileY(), false);
      }

      if(isSourceOrgan()){
        cortex().handleSource(this);
      }

      getBuilding().updateProximity();
    }
  }

  @Override
  default void addToWrite(Seq<Entityc> toWrite){
    toWrite.add(neuron());
  }

  @Override
  default void getFromRead(Queue<Entityc> toRead){
    configureNeuron(null, toRead.first());
    toRead.removeFirst();
    getBuilding().onProximityUpdate();

    if(thrombus()){
      changeThrombus(true);
    }
  }

  @Override
  default void writeSerialization(Writes write){
    SerializationGameEntityModule.super.writeSerialization(write);
    write.f(neoplasm().amount());
    write.f(neoplasmScale());
    write.f(dumpLiquidTimer());
    write.f(dumpTimer());
    write.f(environmentHandler());
    write.i(children().size);
    write.bool(thrombus());
  }

  @Override
  default void readSerialization(Reads read, byte revision){
    SerializationGameEntityModule.super.readSerialization(read, revision);
    neoplasm().set(read.f());
    setNeoplasmScale(read.f());
    setDumpLiquidTimer(read.f());
    setDumpTimer(read.f());
    setEnvironmentHandler(read.f());
    setReadSize(read.i());
    setThrombus(read.bool());
  }
}
