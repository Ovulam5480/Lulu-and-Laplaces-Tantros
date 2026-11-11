package lulu.entities.comp.entitycomps;

import ent.anno.*;
import lulu.graphics.renderer.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class StaticDrawComp implements Drawc{
  @Override
  public void add(){
    StaticMeshRenderer.register(self());
    recache();
  }

  @Override
  public void remove(){
    StaticMeshRenderer.unregister(self());
  }

  public void recache(){
    StaticMeshRenderer.recache(self());
  }

  public boolean shouldDrawStatic(){
    return true;
  }

  public int staticLayer(){
    return 1;
  }

  public void drawCached(){
  }
}
