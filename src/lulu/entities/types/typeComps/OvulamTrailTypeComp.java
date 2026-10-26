package lulu.entities.types.typeComps;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import lulu.entities.gen.*;
import lulu.graphics.*;
import universecore.annotations.*;

public interface OvulamTrailTypeComp {
  @Annotations.BindField(value = "OvulamTrailTypes", initialize = "new arc.struct.Seq<>()")
  default Seq<OvulamTrailType> ovulamTrailTypes(){
    return null;
  }

  @Annotations.BindField(value = "OvulamTrailTypes")
  default void ovulamTrailTypes(Seq<OvulamTrailType> types){}

  @Annotations.MethodEntry(entryMethod = "init")
  default void initOvulamTrailType(){
    Seq<OvulamTrailType> mapped = new Seq<>();
    for(OvulamTrailType type : ovulamTrailTypes()){
      if(type.mirror){
        mapped.add(new OvulamTrailType(type.length, -type.bias, type.width, -type.offsetX, type.offsetY, type.trainColor));
      }
      mapped.add(type);
    }

    ovulamTrailTypes(mapped);
  }

  class OvulamTrailType{
    public int length;
    public float bias;
    public float width;
    public float offsetX, offsetY;
    public Color trainColor;
    public boolean mirror = true;

    public OvulamTrailType(int length, float bias, float width, float offsetX, float offsetY, Color color){
      this.length = length;
      this.bias = bias;
      this.width = width;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.trainColor = color;
    }

    public OvulamTrailType(int length, float width, int offsetX, int offsetY, Color color){
      this(length, 0, width, offsetX, offsetY, color);
    }

    public void update(OvulamTrail trail, float x, float y, float rotation){
      Tmp.v1.set(offsetX, offsetY).rotate(rotation - 90);
      trail.update(x + Tmp.v1.x, y + Tmp.v1.y);
    }

    public void update(OvulamTrail trail, OvulamTrailc trailc){
      update(trail, trailc.x(), trailc.y(), trailc.rotation());
    }

    public void draw(OvulamTrail trail){
      trail.draw(trainColor);
    }

    public OvulamTrail create(){
      return new OvulamTrail(length, bias){{
        trailWidth = width;
      }};
    }
  }
}
