package LLL.world.neoplasm.units;

import LLL.content.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.lib.singularity.world.consumers.*;
import LLL.world.blocks.module.*;
import LLL.world.uncRecipes.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;
import universecore.util.handler.*;
import universecore.world.consumers.*;
import universecore.world.producers.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.payloads.PayloadBlock.*;

@Annotations.ImplEntries
public class NeoplasmLarvaCyst extends PayloadCrafter implements NeoplasmBlockModule{
  protected ObjectIntMap<UnlockableContent> accepts = new ObjectIntMap<>();

  public float degenerateTime = 60 * 60;

  public NeoplasmLarvaCyst(String name){
    super(name);
    size = 3;

    itemCapacity = 85;
    liquidCapacity = 50;
    rotate = false;

    autoSelect = true;
    canSelect = false;
    shouldConfig = false;
    warmupSpeed = 0.008f;

    solid = false;

    craftedSound = OvulamSounds.spawnSounds.random();
  }

  @Override
  public void afterInit(){
    NeoplasmBlockModule.super.afterInit();

    for(BaseConsumers consumer : consumers){
      for(BaseConsume<? extends ConsumerBuildComp> baseConsume : consumer.all()){
        if(baseConsume instanceof OvulamConsumePayload){
          OvulamConsumePayload<NeoplasmLarvaCystBuild> c = (OvulamConsumePayload<NeoplasmLarvaCystBuild>)baseConsume;
          for(PayloadStack payloadStack : c.payloads){
            accepts.put(payloadStack.item, Math.max(payloadStack.amount, accepts.get(payloadStack.item, 0)));
          }
        }
      }
    }
  }

  @Override
  public void initProduct(){
    super.initProduct();

    for(BaseProducers producer : producers()){
      for(BaseProduce<?> produce : producer.all()){
        if(produce instanceof ProducePayload p){

          if(p.payloads.length == 1
            && ((p.payloads[0].item instanceof UnitType u && u.getFirstRequirements() == null)
            || p.payloads[0].item instanceof Block b && b.requirements.length == 0)){
            ItemSeq seq = new ItemSeq();

            for(BaseConsume<? extends ConsumerBuildComp> consume : producer.cons.all()){
              if(consume instanceof ConsumeItemBase i){
                seq.add(i.consItems);
              }else if(consume instanceof OvulamConsumePayload cp){
                for(PayloadStack stack : cp.payloads){
                  ItemStack[] requirements;

                  if(stack.item instanceof Block b){
                    requirements = b.requirements;
                  }else if(stack.item instanceof UnitType u){
                    requirements = u.getFirstRequirements();
                  }else{
                    requirements = new ItemStack[0];
                  }

                  for(ItemStack itemStack : requirements){
                    seq.add(itemStack.item, itemStack.amount * stack.amount);
                  }
                }
              }
            }

            Seq<ItemStack> requirementSeq = new Seq<>();

            for(ItemStack stack : seq){
              requirementSeq.add(new ItemStack(stack.item, stack.amount / p.payloads[0].amount));
            }

            ItemStack[] requirements = requirementSeq.toArray(ItemStack.class);

            if(p.payloads[0].item instanceof UnitType u){
              FieldHandler.setValueDefault(u, "totalRequirements", requirements);
              FieldHandler.setValueDefault(u, "cachedRequirements", requirements);
              FieldHandler.setValueDefault(u, "firstRequirements", requirements);
            }else if(p.payloads[0].item instanceof Block b){
              b.requirements = requirements;
            }
          }
        }
      }
    }
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

  @Annotations.ImplEntries
  public class NeoplasmLarvaCystBuild extends PayloadCrafterBuild implements NeoplasmOrganModule{
    @Override
    public void craftTrigger(){
      super.craftTrigger();

      while(payloads().get() instanceof UnitPayload u
        && !filter().filter(this, ConsumeType.payload, u.content(), true)
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
        && (!consumer.hasConsume() || filter().filter(this, ConsumeType.payload, payload.content(), true))
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
        payload.update(null, getBuilding());
      }

      Vec2 offset = outputtingOffset();
      Building targetTile;
      boolean front = false;
      if(Math.max(Math.abs(offset.x), Math.abs(offset.y)) <= getBlock().size / 2f * tilesize + 0.5f){
        targetTile = getBuilding().front();
        front = true;
      }else targetTile = Vars.world.buildWorld(getBuilding().x + offset.x, getBuilding().y + offset.y);

      boolean canDump = targetTile == null || !targetTile.tile.solid();
      boolean canMove = targetTile != null && (targetTile.block.acceptsPayload || targetTile.block.outputsPayload) && targetTile.interactable(getBuilding().team);

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
          if(targetTile.acceptPayload(getBuilding(), outputting())){
            float rot = outputting().rotation();
            targetTile.handlePayload(getBuilding(), outputting());

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

    @Override
    public float getDegenerateTime(){
      return degenerateTime;
    }

    @Override
    public void popPayload(){
      if(outputLocking()) return;

      for(Payload payload : payloads().iterate()){
        if(!filter().filter(this, SglConsumeType.payload, payload.content(), false)){
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
