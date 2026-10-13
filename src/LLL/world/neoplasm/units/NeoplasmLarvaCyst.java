package LLL.world.neoplasm.units;

import LLL.content.*;
import LLL.ctype.*;
import LLL.entities.gen.*;
import LLL.world.blockcomp.*;
import LLL.world.buildComp.module.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.payloads.PayloadBlock.*;

@Annotations.ImplEntries
public class NeoplasmLarvaCyst extends Block implements UnitCrafterBlockComp<PayloadCrafterBuildBuilding>, NeoplasmBlockComp{
  protected ObjectIntMap<UnlockableContent> accepts = new ObjectIntMap<>();

  public float degenerateTime = 60 * 60;

  public NeoplasmLarvaCyst(String name){
    super(name);
    size = 3;

    itemCapacity = 85;
    liquidCapacity = 50;
    rotate = false;

    autoSelect(true);
    canSelect(false);
    shouldConfig(false);
    warmupSpeed(0.008f);

    solid = false;

    craftedSound(OvulamSounds.spawnSounds.random());
  }

  @Override
  public void setBars(){
    super.setBars();

    addBar("progress", (NeoplasmLarvaCystBuild entity) -> new Bar(
      () -> Strings.autoFixed(entity.progress() * 100, 1) + "%",
      () -> Pal.ammo,
      () -> entity.progress()
    ));

    addBar("sleepTime", (NeoplasmLarvaCystBuild b) -> new Bar("bar.neoplasm", Pal.accent, () -> b.degenerate() / degenerateTime));
  }

  //NeoplasmOrganBuild
  public class NeoplasmLarvaCystBuild extends PayloadCrafterBuildBuilding implements NeoplasmBuildComp{
    public float degenerate;

    public float degenerate(){
      return degenerate;
    }

    @Override
    public void update(){
      super.update();
      if(neuron() != null){
        if(warmup() > 0.01f){
          degenerate = 0;
        }else{
          degenerate += Time.delta;
          if(degenerate > getDegenerateTime()) suicide();
        }
      }
    }

    @Override
    public void craftTrigger(){
      super.craftTrigger();

      while(payloads().get() instanceof UnitPayload u
        && !filter().filter(this, ResourceType.payload, u.content(), true)
        && (u.unit.type.flying || !solid)){
        payloads().take().dump();
      }
    }

    @Annotations.EntryBlocked
    public boolean acceptPayload(Building source, Payload payload){
      boolean isUnit = payload instanceof UnitPayload;
      if(source == this && isUnit ? !((UnitPayload)payload).unit.isAdded() : !((BuildPayload)payload).build.isAdded()){
        return true;
      }

      return acceptsPayload
        && inputting() == null
        && (!consumer.hasConsume() || filter().filter(this, ResourceType.payload, payload.content(), true))
        && payloads().amountOf(payload.content()) < accepts.get(payload.content(), 0);
    }

    @Override
    public Payload getPayload(){
      return outputting() != null ? outputting() : super.getPayload();
    }

    @Override
    public Payload takePayload(){
      if(outputting() != null){
        Payload outputting = outputting();

        released(outputting());
        outputting(null);
        outputLocking(false);

        return outputting;
      }
      return super.takePayload();
    }

    //        @Override
//        public void handlePayload(Building source, Payload payload) {
//            super.handlePayload(source, payload);
//        }

    public void updatePayloads(){
      if(!outputLocking()){
        stackAlpha(Mathf.approachDelta(stackAlpha(), inputting() != null && outputting() == null ? 0 : 1, payloadSpeed() / (getBlock().size * tilesize / 2f)));
      }

      for(Payload payload : payloads().iterate()){
        payload.update(null, this);
      }

      Vec2 offset = outputtingOffset();
      Building targetTile;
      boolean front = false;
      if(Math.max(Math.abs(offset.x), Math.abs(offset.y)) <= getBlock().size / 2f * tilesize + 0.5f){
        targetTile = this.front();
        front = true;
      }else targetTile = Vars.world.buildWorld(this.x + offset.x, this.y + offset.y);

      boolean canDump = targetTile == null || !targetTile.tile.solid();
      boolean canMove = targetTile != null && (targetTile.block.acceptsPayload || targetTile.block.outputsPayload) && targetTile.interactable(this.team);

      if(!outputLocking() && (canDump || canMove)) popPayload();

      float inputProgress = handleInputPayload();
      float outputProgress = handleOutputPayload();

      if(inputProgress >= 0.999f){
        if(payloads().amountOf(inputting().content()) < accepts.get(inputting().content(), 0)){
          payloads().add(inputting());
          stackAlpha(1);
          inputting(null);
        }
      }

      if(canDump && !canMove){
        pushOutput(outputting(), outputProgress);
      }

      if(outputProgress >= 0.999f){
        if(canMove){
          if(targetTile.acceptPayload(this, outputting())){
            float rot = outputting().rotation();
            targetTile.handlePayload(this, outputting());

            if(!front && targetTile instanceof PayloadBlock.PayloadBlockBuild<?> build){
              build.payload.set(build.x, build.y, rot);
              build.payVector.setZero();
              build.payRotation = rot;
            }

            released(outputting());
            outputting(null);
            outputLocking(false);
          }
        }else if(canDump){
          if(outputting().dump()){
            released(outputting());
            outputting(null);
            outputLocking(false);
          }
        }
      }
    }

    @Override
    public Vec2 outputtingOffset(){
      return Vec2.ZERO;
    }

    public float getDegenerateTime(){
      return degenerateTime;
    }

    @Override
    public void popPayload(){
      if(outputLocking()) return;

      for(Payload payload : payloads().iterate()){
        if(!filter().filter(this, ResourceType.payload, payload.content(), false)){
          outputting(payload);
          outputLocking(true);
          stackAlpha(0);

          payloads().remove(payload.content());

          return;
        }
      }
    }

    @Override
    public void draw(){
      Draw.scl(neoplasmScale());
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }
  }
}
