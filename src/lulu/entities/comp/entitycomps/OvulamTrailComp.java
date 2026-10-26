package lulu.entities.comp.entitycomps;

import arc.graphics.g2d.*;
import arc.struct.*;
import ent.anno.*;
import lulu.entities.types.typeComps.*;
import lulu.graphics.*;
import mindustry.gen.*;

@Annotations.EntityComponent
public abstract class OvulamTrailComp implements Drawc, Rotc{
  @Annotations.Import
  public float x, y, rotation;
  public OvulamTrailTypeComp typeComp;
  public transient ObjectMap<OvulamTrailTypeComp.OvulamTrailType, OvulamTrail> trails = new ObjectMap<>();

  @Override
  public void add(){
    for(OvulamTrailTypeComp.OvulamTrailType type : typeComp.ovulamTrailTypes()){
      trails.put(type, type.create());
    }
  }

  @Override
  public void update(){
    for(ObjectMap.Entry<OvulamTrailTypeComp.OvulamTrailType, OvulamTrail> trail : trails){
      trail.key.update(trail.value, self());
    }
  }

  @Override
  public void draw(){
    Draw.z(Draw.z() - 1);
    for(OvulamTrailTypeComp.OvulamTrailType trailType : typeComp.ovulamTrailTypes()){
      trailType.draw(trails.get(trailType));
    }
  }
}
