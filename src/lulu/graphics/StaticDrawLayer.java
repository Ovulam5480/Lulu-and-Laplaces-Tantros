package lulu.graphics;

import mindustry.graphics.Layer;

public enum StaticDrawLayer{
  under(Layer.blockUnder),
  normal(Layer.block),
  over(Layer.blockOver),
  prop(Layer.blockProp),
  top(Layer.turret);

  public final float z;

  public final static StaticDrawLayer[] all = values();
  public final static int amount = all.length;

  StaticDrawLayer(float z){
    this.z = z;
  }
}
