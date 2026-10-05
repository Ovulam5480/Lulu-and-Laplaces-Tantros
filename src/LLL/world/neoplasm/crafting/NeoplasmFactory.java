package LLL.world.neoplasm.crafting;

import LLL.content.*;
import LLL.lib.singularity.world.blocks.product.*;
import LLL.world.blocks.module.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

@Annotations.ImplEntries
public class NeoplasmFactory extends NormalCrafter implements NeoplasmBlockModule, FactoryBlockComp{
  public float neoplasmScaleSpeed = 0.02f;
  public float targetScale = 0.7f;

  public NeoplasmFactory(String name){
    super(name);
    size = 3;
    itemCapacity = 20;
    liquidCapacity = 50;

    autoSelect = true;
    canSelect = false;
    shouldConfig = false;
    warmupSpeed = 0.008f;

    craftedSound = OvulamSounds.factorySounds.random();
  }

  @Override
  public void setBars(){
    super.setBars();

    addBar("sleepTime", (NeoplasmFactoryBuild b) -> new Bar("bar.neoplasm", Pal.accent, () -> b.degenerate() / 600));
  }

  @Annotations.ImplEntries
  public class NeoplasmFactoryBuild extends NormalCrafterBuild implements NeoplasmOrganModule{

    @Override
    public void draw(){
      Draw.scl(neoplasmScale());
      Draw.rect(baseRegion(), x, y);
      Draw.scl();
    }

    @Override
    public void onCraftingUpdate(){
      super.onCraftingUpdate();
    }

    @Override
    public void craftTrigger(){
      super.craftTrigger();
      setNeoplasmScale(targetScale);
      setUpdateScale(true);
    }
  }
}
