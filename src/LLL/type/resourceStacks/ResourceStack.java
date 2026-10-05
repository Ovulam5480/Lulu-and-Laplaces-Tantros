package LLL.type.resourceStacks;

import LLL.world.blocks.packet.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.io.*;
import arc.util.pooling.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.*;

import java.lang.reflect.*;

public abstract class ResourceStack<T extends UnlockableContent> implements Pool.Poolable, Comparable<ResourceStack<?>>{
  public T item;
  public float amount;

  public ResourceStack<T> set(T item, float amount){
    this.item = item;
    this.amount = amount;
    return this;
  }

  public void setObject(Object item, float amount){
    set((T)item, amount);
  }

  public static Seq<ResourceStack<?>> list(Object... objects){
    Seq<ResourceStack<?>> list = new Seq<>();

    for(int i = 0; i < objects.length; i += 3){
      ResourceStack<?> stack = ResourceStackManager.resourceMap.get((Class<?>)objects[i]).get();
      Object object = objects[i + 1];

      stack.setObject(object, (float)objects[i + 2]);

      list.add(stack);
    }

    return list;
  }

  public abstract int id();

  public abstract void register();

  public abstract void init();

  public abstract void applyBlock(Block block);

  public TextureRegion getItemIcon(T item){
    return item.fullIcon;
  }

  //todo 不确定是否有必要
  public TextureRegion getIcon(){
    return getItemIcon(item);
  }

  public void update(){
  }

  public abstract String name();

  public abstract boolean discrete();

  public abstract T findAvailableResource(Entityc entityc, float amount);

  public abstract void applyPack(Entityc entityc, Object object, float amount);

  public abstract boolean unpack(Entityc entityc, float amount);

  public abstract boolean canUnpack(Entityc entityc);

  public abstract void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building);

  public abstract void write(Writes writes);

  public abstract ResourceStack<T> read(Reads reads);

  @Override
  public int compareTo(@NotNull ResourceStack<?> o){
    int stackId = Integer.compare(id(), o.id());
    if(stackId != 0) return stackId;

    return Integer.compare(item.id, o.item.id);
  }

  @Override
  public void reset(){
    item = null;
    amount = 0;
  }

  public ResourceStack<T> copy(){
    ResourceStack<T> stack;
    try{
      stack = getClass().getConstructor().newInstance();
    }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
      throw new RuntimeException(e);
    }

    stack.set(item, amount);

    return stack;
  }
}
