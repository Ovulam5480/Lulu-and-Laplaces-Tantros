package LLL.type.biomeplanet;

import arc.math.geom.*;

public class BiomeBox implements QuadTree.QuadTreeObject{
  public Biome biome;
  public Rect bound;

  public BiomeBox(Rect bound, Biome biome){
    this.biome = biome;
    this.bound = bound;
  }

  public BiomeBox(Biome biome, float x, float y, float width, float height){
    this(new Rect(x, y, width, height), biome);
  }

  @Override
  public void hitbox(Rect out){
    out.set(bound);
  }
}
