package lulu.world.blocks.storage;

import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.blocks.storage.*;

public class OvulamCoreBlock extends CoreBlock{
  public OvulamCoreBlock(String name){
    super(name);
    allowConfigInventory = false;
    configurable = true;

    launchEffect= Fx.none;
  }

  public class OvulamCoreBuild extends CoreBuild{
    @Override
    public void updateTile(){
      iframes -= Time.delta;
      thrusterTime -= Time.delta/90f;
    }

    @Override
    public void created(){
      super.created();
      configurable = true;
    }

    @Override
    public boolean shouldHideConfigure(Player player){
      return false;
    }
  }
}
