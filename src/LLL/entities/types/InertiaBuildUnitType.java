package LLL.entities.types;

import arc.func.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

public class InertiaBuildUnitType extends UnitType{
  public Func<Team, Building> buildingProvider;
  public Block block = Blocks.router;

  public InertiaBuildUnitType(String name){
    super(name);

    speed = 0;
    hidden = true;
    useUnitCap = false;

    drag = 0.05f;
  }

  @Override
  public void init(){
    super.init();

    if(buildingProvider == null){
      buildingProvider = t -> block.newBuilding().create(block, t);
    }
  }

  @Override
  public Unit create(Team team){
    BlockUnitc entity = (BlockUnitc)super.create(team);
    Building building = buildingProvider.get(team);

    entity.tile(building);

    return (Unit)entity;
  }
}
