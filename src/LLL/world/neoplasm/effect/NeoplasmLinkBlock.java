package LLL.world.neoplasm.effect;

import LLL.lib.multiblock.extend.*;
import LLL.world.blockcomp.*;
import LLL.world.buildComp.module.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmLinkBlock extends LinkBlock implements NeoplasmBlockComp{
  public NeoplasmLinkBlock(String name){
    super(name);
  }

  @Override
  public void initNeoplasticBlock(){
    NeoplasmBlockComp.super.initNeoplasticBlock();
    replaceable = false;
  }

  @Annotations.ImplEntries
  public class NeoplasmLinkBuild extends LinkBuild implements NeoplasmBuildComp{
    public NeoplasmBuildComp linkNeoplasm;

    @Override
    public void updateLink(Building link){
      super.updateLink(link);

      if(link instanceof NeoplasmBuildComp n){
        linkNeoplasm = n;

        if(n.neuron() != null){
          configured(null, n.neuron());
          neoplasm(n.neoplasm());
          n.cortex().requestStructure(tile.x, tile.y, false);
        }
      }
    }

    @Override
    public void consumeNeoplastic(){
    }

    @Annotations.EntryBlocked
    @Override
    public boolean acceptItem(Building source, Item item){
      return super.acceptItem(source, item);
    }

    @Annotations.EntryBlocked
    @Override
    public int acceptStack(Item item, int amount, Teamc source){
      return super.acceptStack(item, amount, source);
    }
  }
}
