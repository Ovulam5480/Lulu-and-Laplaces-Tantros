package lulu.entities.types;

import lulu.entities.gen.*;
import lulu.entities.types.typeComps.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class OvulamUnitType extends UnitType implements OvulamTrailTypeComp{
  public OvulamUnitType(String name){
    super(name);
  }

  @Override
  public Unit create(Team team){
    Unit unit = super.create(team);

    if(unit instanceof OvulamTrailc c){
      c.typeComp(this);
    }

    return unit;
  }
}
