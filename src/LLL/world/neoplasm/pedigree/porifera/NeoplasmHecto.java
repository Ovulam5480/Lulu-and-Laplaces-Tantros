package LLL.world.neoplasm.pedigree.porifera;

import LLL.world.blocks.module.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@Annotations.ImplEntries
public class NeoplasmHecto extends NeoplasmSyncytium implements TrophosomeTargetBlockModule{
  public ObjectFloatMap<UnlockableContent> unitValue = new ObjectFloatMap<>();
  public int priority = -1;

  public UnitType targetUnit;
  public float produceOnceValue = 100;
  public float produceTime = 60 * 3;

  public Block triaxType = Blocks.air;

  public NeoplasmHecto(String name){
    super(name);
  }

  @Annotations.ImplEntries
  public class NeoplasmHectoBuild extends NeoplasmSyncytiumBuild implements TrophosomeTargetBuildModule{
    public float totalValues;
    public float progress;

    public Building triax;

    @Override
    public boolean canAcceptPayload(UnlockableContent type){
      return unitValue.containsKey(type);
    }

    @Override
    public int getPayloadCapacity(UnlockableContent type){
      return unitValue.containsKey(type) ? 999 : 0;
    }

    @Override
    public int getPayloadCount(UnlockableContent type){
      return 0;
    }

    @Annotations.EntryBlocked
    @Override
    public boolean acceptPayload(Building source, Payload payload){
      if(source == this) return true;

      return unitValue.containsKey(payload.content());
    }

    @Annotations.EntryBlocked
    @Override
    public void handlePayload(Building source, Payload payload){
      totalValues += unitValue.get(payload.content(), 0);
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(triax == null) triax = triaxType.newBuilding().create(triaxType, team);
      if(!triax.isAdded()) initTriax(triax);

      if(totalValues > produceOnceValue){
        progress += totalValues / produceOnceValue * delta();

        if(progress > produceTime){
          progress = 0;
          totalValues -= produceOnceValue;

          Unit unit = targetUnit.create(team);
          unit.set(x, y);//todo
          unit.add();

          Events.fire(new EventType.UnitCreateEvent(unit, this));
        }
      }
    }

    public void initTriax(Building triax){
      triax.init(emptyTile, team, true, 0);
      triax.set(x - 6 * 8, y);
    }

    @Override
    public void draw(){
      super.draw();

      if(triax != null) triax.draw();
    }

    @Override
    public void remove(){
      super.remove();

      if(triax != null) triax.remove();
    }

    @Override
    public void write(Writes write){
      super.write(write);

      write.f(progress);
      write.f(totalValues);

      write.b(triax.version());
      Log.info(triax.version());
      triax.write(write);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);

      progress = read.f();
      totalValues = read.f();

      triax = triaxType.newBuilding().create(triaxType, team);
      initTriax(triax);

      triax.read(read, read.b());
    }
  }
}
