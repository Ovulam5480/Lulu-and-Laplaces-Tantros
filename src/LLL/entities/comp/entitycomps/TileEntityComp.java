package LLL.entities.comp.entitycomps;

import LLL.*;
import LLL.entities.gen.*;
import LLL.world.blocks.entity.*;
import arc.graphics.g2d.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

@Annotations.EntityDef(TileEntityc.class)
@Annotations.EntityComponent
abstract class TileEntityComp implements Drawc, Hitboxc{
  public Block block;
  public Tile tile;

  public <T extends TileEntityBlock> T blockAs(){
    return (T)block;
  }

  @Override
  public void add(){
    LuluMod.oIndexer.tileEntity.add(self());
    hitSize(block.size * tilesize);
    LuluMod.oIndexer.tileEntity.updatePhysics();
  }

  @Annotations.MethodPriority(-999)
  @Override
  public void draw(){
    Draw.z(blockAs().entityLayer);
  }

  @Override
  public void remove(){
    LuluMod.oIndexer.tileEntity.remove(self());
  }

  @Override
  @Annotations.Replace
  public float clipSize(){
    return block.clipSize;
  }
}