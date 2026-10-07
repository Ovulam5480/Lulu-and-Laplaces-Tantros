package LLL.packet.ResourceStacks;

import LLL.world.blocks.packet.*;
import LLL.world.recipe.consumers.*;
import LLL.world.recipe.producers.*;
import arc.graphics.g2d.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;

public class NonStackableResource<T, C extends BaseConsume, P extends BaseProduce> extends ResourceStack<T, C, P>{
  @Override
  public int id(){
    return -1;
  }

  @Override
  public void register(){
  }

  @Override
  public void init(){
  }

  @Override
  public void applyBlock(Block block){
  }

  @Override
  public String name(){
    return "";
  }

  @Override
  public boolean discrete(){
    return true;
  }

  @Override
  public T findAvailableResource(Entityc entityc, float amount){
    return null;
  }

  @Override
  public void applyPack(Entityc entityc, Object object, float amount){
  }

  @Override
  public boolean unpack(Entityc entityc, float amount){
    return false;
  }

  @Override
  public boolean canUnpack(Entityc entityc){
    return false;
  }

  @Override
  public void dumpOutputs(UnpackerBlock.UnpackerBuild building){
  }

  @Override
  public void write(Writes writes){
  }

  @Override
  public ResourceStack<T, ?, ?> read(Reads reads){
    return null;
  }

  @Override
  public TextureRegion getItemIcon(Object item){
    return null;
  }
}
