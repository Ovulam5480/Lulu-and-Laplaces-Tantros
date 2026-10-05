package LLL.world.neoplasm.effect;

import LLL.content.blocks.*;
import LLL.world.blocks.module.*;
import arc.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmGanglion extends CoreBlock implements NeoplasmBlockModule{
  public boolean requestStructure = true;
  public float maxHealOnce = 5f;

  public NeoplasmGanglion(String name){
    super(name);
    health = 2000;

    liquidCapacity = 500;
    itemCapacity = 200;

    size = 2;
    solid = true;

    allowSpawn = false;
    requiresCoreZone = false;
  }

  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return true;
  }

  @Override
  public void placeBegan(Tile tile, Block previous, Unit builder){
  }

  @Annotations.ImplEntries
  public class NeoplasmGanglionBuild extends CoreBuild implements NeoplasmBuildModule{
    @Override
    public void drawSelect(){
      block.drawOverlay(x, y, rotation);
    }

    @Override
    public void updateItemDump(Building self, float delta){
    }

    @Annotations.EntryBlocked
    @Override
    public boolean acceptItem(Building source, Item item){
      return true;
    }

    @Override
    public void handleItem(Building source, Item item){
      boolean incinerate = !Neoplasm.element.contains(item);

      if(items.get(item) < getMaximumAccepted(item) && !incinerate){
        items.add(item, 1);
      }else{
        if(!noEffect){
          incinerateEffect(this, source);
        }
        noEffect = false;
      }
    }

    @Override
    public void onDestroyed(){
      buildOnDestroyed();
      Events.fire(new EventType.CoreChangeEvent(this));
    }

    public void buildOnDestroyed(){
      if(block.createRubble && !floor().solid && !floor().isLiquid){
        Effect.rubble(x, y, block.size);
      }
    }
  }
}
