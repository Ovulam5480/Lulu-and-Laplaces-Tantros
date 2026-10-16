package lulu.world.components.blockComps;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.recipe.*;
import lulu.world.recipe.consumers.*;
import arc.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import universecore.annotations.*;

public interface FloorCrafterBlockComp<T extends Building & FloorCrafterBuildc<T>> extends ProducerBlockComp<T>{
  Table iconsTable = new Table();

  @Annotations.MethodEntry(entryMethod = "drawPlace", paramTypes = {"int -> x", "int -> y", "int -> rotation", "boolean -> valid"})
  default void drawPlaceFloorCrafter(int x, int y, int rotation, boolean valid){
    Block block = self();
    float eff = 0;
    int c = 0;
    int line = 0;

    Tile t = Vars.world.tile(x, y);
    if(t != null){
      for(BaseConsumers<T> consumer : consumers()){
        ConsumeFloor<T> cons = consumer.get(ResourceType.floor);
        if(cons != null){
          c++;
          eff = cons.getEff(getFloors(t, block));
        }
      }

      if(c == 0) eff = 1;
      for(ObjectFloatMap.Entry<BaseConsumers<T>> boost : boosts()){
        ConsumeFloor<T> cons = boost.key.get(ResourceType.floor);
        if(cons != null){
          c++;
          eff *= cons.getEff(getFloors(t, block));
        }
      }

      for(ObjectMap.Entry<BaseConsumers<T>, BaseProducers<T>> product : optionalProducts()){
        if(!valid && !product.key.optionalAlwaysValid) continue;

        ConsumeFloor<T> cons = product.key.get(ResourceType.floor);
        if(cons != null){
          float optEff = cons.getEff(getFloors(t, block));
          if(optEff <= 0) continue;

          Table ta = buildIconsTable(product);

          float width = block.drawPlaceText(
            Core.bundle.format("bar.efficiency", (int)(optEff * 100f)),
            x, y + line, valid
          );
          float dx = x * Vars.tilesize + block.offset - width / 2f, dy = y * Vars.tilesize + block.offset + block.size * Vars.tilesize / 2f + 5 - line * 8f;
          ta.setPosition(dx - ta.getWidth() / 8f, dy - ta.getHeight() / 16f);
          ta.setTransform(true);
          ta.setScale(1f / 8f);
          ta.draw();
          line++;
        }
      }
    }

    block.drawPlaceText(
      c == 1 && valid ? Core.bundle.format("bar.efficiency", (int)(eff * 100f)) : valid ? Core.bundle.get("infos.placeValid") : Core.bundle.get("infos.placeInvalid"),
      x, y + line, valid
    );
  }

  private static <T extends Building & FloorCrafterBuildc<T>> Table buildIconsTable(ObjectMap.Entry<BaseConsumers<T>, BaseProducers<T>> product){
    iconsTable.clear();

    boolean[] first = {true};
    product.value.forAll(produce -> {
      if(!produce.hasIcons()) return;

      if(!first[0]) iconsTable.add("+").fillX().pad(4);
      iconsTable.table(ca -> {
        ca.defaults().padLeft(3).fill();

        produce.buildIcons(ca);
      }).fill();

      first[0] = false;
    });
    iconsTable.pack();
    return iconsTable;
  }

  @Annotations.MethodEntry(entryMethod = "canPlaceOn", paramTypes = {"mindustry.world.Tile -> tile", "mindustry.game.Team -> team", "int -> rotation"})
  default boolean canPlaceOnFloorCrafter(Tile tile, Team team, int rotation){
    Block block = self();
    float eff = 0;
    int c = 0;

    for(ObjectMap.Entry<BaseConsumers<T>, BaseProducers<T>> product : optionalProducts()){
      ConsumeFloor<T> cons = product.key.get(ResourceType.floor);
      if(cons != null){
        if(product.key.optionalAlwaysValid && cons.getEff(getFloors(tile, block)) > 0) return true;
      }
    }

    for(BaseConsumers<T> consumer : consumers()){
      ConsumeFloor<T> cons = consumer.get(ResourceType.floor);
      if(cons != null){
        c++;
        eff = cons.getEff(getFloors(tile, block));
      }
    }

    if(c == 0) eff = 1;
    for(ObjectFloatMap.Entry<BaseConsumers<T>> boost : boosts()){
      ConsumeFloor<T> cons = boost.key.get(ResourceType.floor);
      if(cons != null){
        c++;
        eff *= cons.getEff(getFloors(tile, block));
      }
    }

    return c > 0 && eff > 0;
  }

  ObjectIntMap<Floor> count = new ObjectIntMap<>();

  static ObjectIntMap<Floor> getFloors(Tile tile, Block block){
    count.clear();
    tile.getLinkedTilesAs(block, t -> {
      Floor f;
      if((f = t.floor()) != null){
        count.increment(f, 0, 1);
      }
    });
    return count;
  }
}
