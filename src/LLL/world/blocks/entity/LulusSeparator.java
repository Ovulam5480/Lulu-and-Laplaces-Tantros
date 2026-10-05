package LLL.world.blocks.entity;

import LLL.entities.gen.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;

public class LulusSeparator extends LargeTileEntityBlock{
  public float landDuration = 320f;
  public TextureRegion shadowRegion;
  public TextureRegion[] launchingRegions;

  public LulusSeparator(String name, int entitySize){
    super(name, entitySize);
    entityProvider = LulusSeparatorEntity::create;
  }

  @Override
  public void load(){
    super.load();

    shadowRegion = Core.atlas.find(name + "-shadow");

    Seq<TextureRegion> regions = new Seq<>();
    regions.add(region);
    int i = 0;
    while(Core.atlas.has(name + "-" + i)){
      regions.add(Core.atlas.find(name + "-" + i));
      i++;
    }
    launchingRegions = regions.toArray(TextureRegion.class);

  }
}
