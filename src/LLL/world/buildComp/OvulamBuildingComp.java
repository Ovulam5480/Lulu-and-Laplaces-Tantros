package LLL.world.buildComp;

import arc.graphics.*;
import arc.util.*;
import ent.anno.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class OvulamBuildingComp implements Buildingc{
  @Annotations.Import
  Block block;
  @Annotations.Import
  PowerModule power;
  @Annotations.Import
  ItemModule items;
  @Annotations.Import
  LiquidModule liquids;
  @Annotations.Import
  boolean initialized, enabled;
  @Annotations.Import
  float x, y, health, maxHealth;
  @Annotations.Import
  int rotation;
  @Annotations.Import
  Team team;
  @Annotations.Import
  float timeScale, timeScaleDuration;
  @Annotations.Import
  float efficiency;

  @Annotations.Replace
  public Building create(Block block, Team team){
    beforeCreate();
    createBuild(block, team);
    afterCreate();

    initialized = true;

    return self();
  }

  public void beforeCreate(){
  }

  public void createBuild(Block block, Team team){
    this.block = block;
    this.team = team;

    health = block.health;
    maxHealth(block.health);
    timer(new Interval(block.timers));

    if(block.hasItems) items = new ItemModule();
    if(block.hasLiquids) liquids = new LiquidModule();
    if(block.hasPower){
      power = new PowerModule();
      power.graph.add(self());
    }
  }

  public void afterCreate(){
  }

  @Annotations.Replace
  public void update(){
    updateBefore();
    updateBuild();
    updateAfter();
  }

  public void updateBefore(){
  }

  public void updateBuild(){
    if((timeScaleDuration -= Time.delta) <= 0f || !block.canOverdrive){
      timeScale = 1f;
    }

    //TODO separate multithreaded system for sound? AudioSource, etc
    if(!headless && block.ambientSound != Sounds.none && shouldAmbientSound()){
      control.sound.loop(block.ambientSound, self(), block.ambientSoundVolume * ambientVolume());
    }

    updateConsumption();

    if(enabled || !block.noUpdateDisabled){
      updateTile();
    }
  }

  public void updateConsumption(){
  }

  public void updateTile(){
  }

  @Annotations.Replace
  public boolean shouldAmbientSound(){
    return shouldConsume();
  }

  @Annotations.Replace
  public float ambientVolume(){
    return efficiency;
  }

  @Annotations.Replace
  public boolean shouldConsume(){
    return enabled;
  }

  public void updateAfter(){
  }

  @Annotations.Replace
  @Override
  public double sense(LAccess sensor){
    return senseBuilding(sensor);
  }

  public double senseBuilding(LAccess sensor){
    return switch(sensor){
      case x -> World.conv(x);
      case y -> World.conv(y);
      case color -> Color.toDoubleBits(team.color.r, team.color.g, team.color.b, 1f);
      case dead -> !isValid() ? 1 : 0;
      case solid -> block.solid || checkSolid() ? 1 : 0;
      case team -> team.id;
      case health -> health;
      case maxHealth -> maxHealth;
      case efficiency -> efficiency;
      case timescale -> timeScale;
      case range -> this instanceof Ranged r ? r.range() / tilesize : 0;
      case rotation -> rotation;
      case totalItems -> items == null ? 0 : items.total();
      //totalLiquids is inherently bad design, but unfortunately it is useful for conduits/tanks
      case totalLiquids -> liquids == null ? 0 : liquids.currentAmount();
      case totalPower ->
        power == null || block.consPower == null ? 0 : power.status * (block.consPower.buffered ? block.consPower.capacity : 1f);
      case itemCapacity -> block.hasItems ? block.itemCapacity : 0;
      case liquidCapacity -> block.hasLiquids ? block.liquidCapacity : 0;
      case powerCapacity -> block.consPower != null ? block.consPower.capacity : 0f;
      case powerNetIn -> power == null ? 0 : power.graph.getLastScaledPowerIn() * 60;
      case powerNetOut -> power == null ? 0 : power.graph.getLastScaledPowerOut() * 60;
      case powerNetStored -> power == null ? 0 : power.graph.getLastPowerStored();
      case powerNetCapacity -> power == null ? 0 : power.graph.getLastCapacity();
      case enabled -> enabled ? 1 : 0;
      case controlled -> this instanceof ControlBlock c && c.isControlled() ? GlobalVars.ctrlPlayer : 0;
      case payloadCount -> (getPayloads() != null ? getPayloads().total() : 0) + (getPayload() != null ? 1 : 0);
      case size -> block.size;
      case cameraX, cameraY, cameraWidth, cameraHeight -> this instanceof ControlBlock c ? c.unit().sense(sensor) : 0;
      default -> Float.NaN; //gets converted to null in logic
    };
  }

  public abstract boolean checkSolid();

  public abstract Payload getPayload();

  public abstract Payload takePayload();

  public abstract PayloadSeq getPayloads();
}
