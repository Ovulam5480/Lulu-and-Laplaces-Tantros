package lulu.entities.comp.entitycomps;

import ent.anno.*;
import lulu.*;
import lulu.entities.gen.*;
import lulu.world.blocks.entity.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

@Annotations.EntityDef(TileEntityc.class)
@Annotations.EntityComponent
abstract class TileEntityComp implements StaticDrawc, Hitboxc{
  public TileEntityBlock block;
  public Tile tile;

  @Override
  public void add(){
    LuluMod.oIndexer.tileEntity.add(self());

    hitSize(block.size * tilesize);
    LuluMod.oIndexer.tileEntity.updatePhysics();
  }

  @Override
  public void remove(){
    LuluMod.oIndexer.tileEntity.remove(self());
  }

  @Override
  public void draw(){
  }

  @Override
  public void drawCached(){
  }

  @Annotations.Replace
  @Override
  public boolean shouldDrawStatic(){
    return block.drawCached;
  }

  @Annotations.Replace
  @Override
  public int staticLayer(){
    return block.staticDrawLayer.ordinal();
  }

  @Override
  @Annotations.Replace
  public float clipSize(){
    return block.clipSize;
  }
}