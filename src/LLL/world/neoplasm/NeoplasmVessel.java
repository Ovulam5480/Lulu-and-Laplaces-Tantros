package LLL.world.neoplasm;

import LLL.entities.gen.*;
import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.type.neoplasm.*;
import LLL.world.blocks.module.*;
import LLL.world.modules.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@Annotations.ImplEntries
public class NeoplasmVessel extends Block implements NeoplasmBlockModule, TileSpriteBlockModule{
  public static GrowItemFilter.VesselFlowTrigger trigger = new GrowItemFilter.VesselFlowTrigger();
  public float speed = 3f;

  public NeoplasmVessel(String name){
    super(name);
    health = 200;
    armor = 5f;
    liquidCapacity = 50f;

    isDuct = true;
    solid = false;
    conveyorPlacement = true;
    unloadable = false;
    itemCapacity = 1;
    noUpdateDisabled = true;
    rotate = true;

    drawTeamOverlay = false;
    customShadow = true;
    solid = true;
    sync = true;
  }

  @Override
  public void initNeoplasticBlock(){
    NeoplasmBlockModule.super.initNeoplasticBlock();
    replaceable = true;
  }

  @Override
  public TextureRegion getToSplit(){
    return Core.atlas.find(name + "-toSplit");
  }

  @Override
  public void drawShadow(Tile tile){
    drawScaledShadow(tile);//todo绘制阴影
  }

  @Annotations.ImplEntries
  public class NeoplasmVesselBuild extends DisplayFlowBuildingBuilding implements NeoplasmBuildModule, TileSpriteBuildModule{
    public float progress;
    public @Nullable Item current;
    public @Nullable Building next;

    public IntFloatMap sleepTimes = new IntFloatMap();
    public boolean isEmpty = true;
    public boolean updateScale = true;

    public TextureRegion tileRegion;
    public Vec2 fromVec = new Vec2();
    public Vec2 toVec = new Vec2();

    @Annotations.EntryBlocked
    @Override
    public boolean acceptItem(Building source, Item item){
      return current == null && items.total() == 0 &&
        (!(source.block.rotate && next == source) && Edges.getFacingEdge(source.tile, tile) != null && Math.abs(Edges.getFacingEdge(source.tile, tile).relativeTo(tile.x, tile.y) - rotation) != 2);
    }

    @Annotations.EntryBlocked
    @Override
    public void handleItem(Building source, Item item){
      if(proximity.size > 2 && !sleepTimes.containsKey(item.id)){
        sleepTimes.put(item.id, 0f);
        isEmpty = false;

        trigger.set(this, item, true);
        Events.fire(trigger);
      }

      current = item;
      progress = -1f;
      int recDir = relativeToEdge(source.tile);
      fromVec.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f);
      items.add(item, 1);
      noSleep();
    }

    @Override
    public void onProximityUpdate(){
      super.onProximityUpdate();

      if(neuron() != null){
        rotation = cortex().getMinDirectionIndex(tile.x, tile.y);
        cortex().addParentChildren(this);
      }
      next = front();

      //tileRegion = getTileRegion();
      toVec.set(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f);
    }

    @Override
    public void draw(){
      Draw.rect(getTileRegion(), x, y);
      if(current != null){
        float z = Draw.z();

        Draw.z(z + 0.1f);
        Draw.alpha(0.5f);

        Tmp.v1.set(fromVec).lerp(toVec, Mathf.clamp((progress + 1f) / (2f - 1f / speed)));

        Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);

        Draw.reset();
        Draw.z(z);
      }
    }

    @Override
    public void update(){
      //不再对效率以及消耗器进行更新
      if(enabled){
        updateTile();
      }
    }

    @Override
    public void updateTile(){
      if(current != null){
        moveItem();
      }

      if(!isEmpty){
        int currentID = current != null ? current.id : -1;
        for(IntFloatMap.Entry time : sleepTimes){
          if(time.key == currentID) continue;

          float sleepTime = time.value + Time.delta;

          if(sleepTime > 300){
            trigger.set(this, Vars.content.item(time.key), false);
            Events.fire(trigger);

            sleepTimes.remove(time.key, 0);
          }else{
            sleepTimes.put(time.key, sleepTime);
          }
        }
        isEmpty = sleepTimes.isEmpty();
      }
    }

    @Override
    public boolean neoplasmActivity(){
      return neoplasm().amount() != 0;
    }

    @Override
    public void consumeNeoplastic(){
      final NeoplasmLiquidModule neoplasm = neoplasm();
      float liquidAmount = neoplasm.amount();
      float delta = delta();

      if(isSourceOrgan()){
        liquidAmount += neoplasmAbsorbMulti() * 100 / 60f * delta;
      }else{
        liquidAmount += environmentHandler() * delta;
      }

      if(liquidAmount > 0){
        float lost = maxHealth - health;
        if(lost != 0){
          float heal = Mathf.clamp(lost, maxHealOnce() * delta, liquidAmount * neoplasmHealScale());

          heal(heal);
          liquidAmount -= heal / neoplasmHealScale();
        }
        neoplasm.setClamp(liquidAmount, liquidCapacity);
      }else{
        damage(2f * delta);
        neoplasm.set(0);
      }

      if(isSourceOrgan() || updateScale()) updateNeoplasmScale();
      //if(neuron() == null)updateLiquidDump(this, delta);
    }

    @Override
    public void updateNeoplasmScale(){
      setNeoplasmScale(Mathf.approach(neoplasmScale(), 1f, blockAs().neoplasmScaleSpeed() * Time.delta));
      if(Mathf.equal(neoplasmScale(), 1f, 0.01f)){
        setUpdateScale(false);
        setNeoplasmScale(1);
      }
    }

    public void moveItem(){
      progress += delta() / speed;

      if(current != null && next != null){
        if(progress >= (1f - 1f / speed) && moveForward(current)){
          items.remove(current, 1);
          current = null;
          progress %= (1f - 1f / speed);
        }
      }else{
        progress = 0;
      }

      if(current == null && items.total() > 0){
        current = items.first();
      }
    }

    @Override
    public boolean canLink(Building other){
      return other.team == team && other.block instanceof NeoplasmBlockModule;
    }

//        @Override
//        public void updateLiquidDump(Building self, float delta){
//            float timer = dumpLiquidTimer() + delta;
//
//            if (timer > 1) {
//                self.dumpLiquid(Liquids.neoplasm);
//                timer--;
//            }
//
//            setDumpLiquidTimer(timer);
//        }

    @Override
    public void remove(){
      super.remove();

      if(neuron() != null && shouldNetworkUpdate() && !getSuicide()){
        cortex().handleKill(this);
      }

      if(neuron() != null){
        neuron().cortex.removeStructure(this);
        cortex().handleOwnedBlock(this, tileX(), tileY(), getBuilding().rotation, false);
        changeThrombus(false);

        IntFloatMap.Keys keys = sleepTimes.keys();
        while(keys.hasNext){
          trigger.set(this, Vars.content.item(keys.next()), false);
          Events.fire(trigger);
        }
      }

      PrefrontalCortex.environmentState.remove(this);
    }

    @Override
    public void handleReomve(){
    }

    @Override
    public void configureNeuron(Unit builder, Object value){
      if(value instanceof NeoplasmNeuron.NeoplasmNeuronBuild neuron){
        setNeuron(neuron);
        neuron.cortex.addStructure(this);
        cortex().handleOwnedBlock(this, tileX(), tileY(), getBuilding().rotation, true);
        updateProximity();
      }
    }

    @Override
    public void write(Writes write){
      super.write(write);

      TypeIO.writeItem(write, current);
      write.f(progress);
      write.bool(isEmpty);

      write.i(sleepTimes.size);
      for(IntFloatMap.Entry time : sleepTimes){
        write.i(time.key);
        write.f(time.value);
      }
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);

      current = TypeIO.readItem(read);
      progress = read.f();
      isEmpty = read.bool();

      int size = read.i();
      for(int i = 0; i < size; i++){
        sleepTimes.put(read.i(), read.f());
      }
    }
  }
}
