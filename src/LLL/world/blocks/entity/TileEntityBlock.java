package LLL.world.blocks.entity;

import LLL.*;
import LLL.content.extensions.*;
import LLL.entities.gen.*;
import LLL.world.blocks.module.*;
import arc.*;
import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class TileEntityBlock extends LegacyBlock implements MultiSizeBlock{
  public Prov<TileEntityc> entityProvider;
  public float entityLayer = Layer.blockOver;

  protected Seq<Rect> boxes = new Seq<>();

  public int sized;

  public TileEntityBlock(String name){
    super(name);

    generateIcons = true;
    inEditor = true;
    hasShadow = false;

    category = OvulamCategory.tileEntity;

    //todo
    buildVisibility = BuildVisibility.shown;
  }

  @Override
  public void init(){
    super.init();

    Events.run(EventType.Trigger.newGame, () -> {
      for(Rect box : boxes){
        if(!LuluMod.oIndexer.tileEntity.contains(te -> {
          te.hitbox(Tmp.r1);
          return Tmp.r1.overlaps(box);
        })){
          createEntity(Vars.world.tileWorld(box.x + box.width / 2 - 0.1f, box.y + box.height / 2 - 0.1f));
        }
      }

      boxes.clear();
    });
  }

  @Override
  public void drawBase(Tile tile){
  }

  @Override
  public void placeEnded(Tile tile, Unit builder, int rotation, Object config){
    createEntity(tile);
  }

  //方法调用时未加载实体, 所以需要临时创建矩形
  public void removeSelf(Tile tile){
    if(!boxes.contains(r -> r.contains(tile.worldx(), tile.worldy()))){
      boxes.add(new Rect().set(tile.worldx() - 4, tile.worldy() - 4, sized * 8, sized * 8));
    }
  }

  public void createEntity(Tile tile){
    TileEntityc entity = entityProvider.get();

    entity.tile(tile);
    entity.block(this);
    entity.set(tile.drawx(), tile.drawy());
    entity.add();
  }
}
