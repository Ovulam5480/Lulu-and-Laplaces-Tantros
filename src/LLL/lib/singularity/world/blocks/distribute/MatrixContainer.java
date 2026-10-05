package LLL.lib.singularity.world.blocks.distribute;

import LLL.lib.singularity.*;
import LLL.lib.singularity.world.blocks.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.distribution.DistSupportContainerTable.*;
import mindustry.gen.*;
import mindustry.type.*;

public class MatrixContainer extends SglBlock{
  public boolean isIntegrate = true;

  public MatrixContainer(String name){
    super(name);

    update = false;
    destructible = true;
    unloadable = false;
    outputItems = false;
  }

  @Override
  public void init(){
    super.init();
    setDistSupport();
  }

  public void setDistSupport(){
    Container cont = Sgl.matrixContainers.getContainer(this, () -> new Container(this, isIntegrate));
    if(hasItems) cont.setCapacity(DistBufferType.itemBuffer, itemCapacity);
    if(hasLiquids) cont.setCapacity(DistBufferType.liquidBuffer, liquidCapacity);
  }

  public class MatrixContainerBuild extends SglBuilding{
    @Override
    public boolean acceptItem(Building source, Item item){
      if(!isIntegrate) return super.acceptItem(source, item);
      return interactable(source.team) && items.total() < itemCapacity;
    }

    @Override
    public int acceptStack(Item item, int amount, Teamc source){
      if(!isIntegrate) return super.acceptStack(item, amount, source);
      return interactable(source.team()) ? Math.min(amount, itemCapacity - items.total()) : 0;
    }

    @Override
    public boolean acceptLiquid(Building source, Liquid liquid){
      if(!isIntegrate) return super.acceptLiquid(source, liquid);
      return interactable(source.team) && liquids().total() < liquidCapacity;
    }
  }
}
