package LLL.entities.types;

import LLL.content.extensions.*;
import LLL.entities.ai.*;
import LLL.entities.weapon.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.blocks.payloads.*;

public class NemerteaUnitType extends OvulamNeoplasmUnitType{
  public NemerteaUnitType(String name){
    super(name);

    targetAir = false;

    aiController = () -> new ConditionalPathGroundAI((unit, v) -> {
      Seq<Building> buildings = Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.buildPayloadProducer);

      for(Building building : buildings){
        if(building.getPayload() instanceof BuildPayload bp && NemerteaProboscis.canPick(unit, bp)){
          return v.set(building);
        }
      }

      return v.set(buildings.isEmpty() ? unit : buildings.first());
    }, unit -> {
      for(WeaponMount mount : unit.mounts){
        if(mount instanceof NemerteaProboscis.Proboscis p && p.owner == null){
          return false;
        }
      }
      return true;
    }){
      @Override
      public Teamc target(float x, float y, float range, boolean air, boolean ground){
        return Vars.indexer.findEnemyTile(unit.team, x, y, range,
          build -> build.proximity.sumf(b -> b.health) + build.health,
          b -> b.proximity.size > 2);
      }
    };
  }
}
