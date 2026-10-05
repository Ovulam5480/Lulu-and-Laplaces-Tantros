package LLL.lib.singularity.world.unit;

import LLL.lib.singularity.world.unit.abilities.*;
import arc.util.io.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import universecore.annotations.*;
import universecore.components.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Annotations.ImplEntries
public class SglUnitEntity extends UnitEntity implements ExtraVariableComp{

  @Override
  public int classId(){
    return 51;
  }

  @Override
  public boolean collides(Hitboxc other){
    for(Ability ability : abilities){
      if(ability instanceof ICollideBlockerAbility blocker && blocker.blockedCollides(this, other)) return false;
    }

    return super.collides(other);
  }

  @Override
  public void add(){
    super.add();
    if(type instanceof SglUnitType sglUnitType) sglUnitType.init(this);
    else throw new RuntimeException("Unit type must be SglUnitType");
  }

  @Override
  public void read(Reads read){
    super.read(read);
    if(type instanceof SglUnitType sglUnitType) sglUnitType.read(this, read, read.i());
    else throw new RuntimeException("Unit type must be SglUnitType");
  }

  @Override
  public void write(Writes write){
    super.write(write);
    if(type instanceof SglUnitType sglUnitType){
      write.i(sglUnitType.version());
      sglUnitType.write(this, write);
    }else throw new RuntimeException("Unit type must be SglUnitType");
  }
}
