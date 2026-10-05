package LLL.world.neoplasm.effect;

import LLL.lib.multiblock.extend.*;
import LLL.world.blocks.module.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmLinkBlock extends LinkBlock implements NeoplasmBlockModule{
  public NeoplasmLinkBlock(String name){
    super(name);
  }

  @Override
  public void initNeoplasticBlock(){
    NeoplasmBlockModule.super.initNeoplasticBlock();
    replaceable = false;
  }

  @Annotations.ImplEntries
  public class NeoplasmLinkBuild extends LinkBuild implements NeoplasmBuildModule{
    public NeoplasmBuildModule linkNeoplasm;

    @Override
    public void updateLink(Building link){
      super.updateLink(link);

      if(link instanceof NeoplasmBuildModule n){
        linkNeoplasm = n;

        if(n.neuron() != null){
          configured(null, n.neuron());
          setNeoplasm(n.neoplasm());
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
