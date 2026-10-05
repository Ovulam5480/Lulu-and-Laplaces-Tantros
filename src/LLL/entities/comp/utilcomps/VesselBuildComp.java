package LLL.entities.comp.utilcomps;

import ent.anno.*;
import mindustry.gen.*;

@Annotations.EntityComponent
public abstract class VesselBuildComp implements Buildingc{
  @Annotations.Import
  boolean enabled;

  @Annotations.Replace(999)
  @Override
  public void update(){
    if(enabled){
      updateTile();
    }
  }

  public void updateTile(){

  }
}
