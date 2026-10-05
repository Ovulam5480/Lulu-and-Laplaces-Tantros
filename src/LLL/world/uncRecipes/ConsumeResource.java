package LLL.world.uncRecipes;

import LLL.lib.singularity.world.consumers.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.module.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import universecore.components.blockcomp.*;
import universecore.world.consumers.*;

public class ConsumeResource<T extends Building & ResourceBuildModule & ConsumerBuildComp> extends BaseConsume<T>{
  private static final ObjectMap<Object, ResourceStack<?>> TMP = new ObjectMap<>();
  public ResourceStack<?>[] resourceStacks;
  public int displayLim = 4;

  public ConsumeResource(Seq<ResourceStack<?>> resourceStacks){
    this.resourceStacks = resourceStacks.toArray();
  }

  @Override
  public ConsumeType<?> type(){
    return SglConsumeType.resource;
  }

  @Override
  public void buildIcons(Table table){
    buildResourceIcons(table, resourceStacks, displayLim);
  }

  public static void buildResourceIcons(Table table, ResourceStack<?>[] resourceStacks, int displayLim){
    int count = 0;

    for(ResourceStack<?> stack : resourceStacks){
      count++;
      if(displayLim >= 0 && count > displayLim){
        table.add("...");
        break;
      }

      table.stack(
        new Table(o -> {
          o.left();
          o.add(new Image(stack.getIcon())).size(32f).scaling(Scaling.fit);
        }),
        new Table(t -> {
          t.left().bottom();
          t.add(stack.amount + "").style(Styles.outlineLabel);
          t.pack();
        })
      );
    }
  }

  @Override
  public void merge(BaseConsume<T> other){
    if(other instanceof ConsumeResource<?> cr){
      TMP.clear();
      for(ResourceStack<?> stack : resourceStacks){
        TMP.put(stack.item, stack);
      }

      for(ResourceStack<?> stack : cr.resourceStacks){
        TMP.get(stack.item, () -> ResourceStackManager.getResourceInstance(stack.item, stack.amount));
      }


      resourceStacks = TMP.values().toSeq().sort().toArray(ResourceStack.class);
      return;
    }
    throw new IllegalArgumentException("only merge consume with same type");
  }

  @Override
  public void consume(T entity){
    for(ResourceStack<?> resourceStack : resourceStacks){
      entity.resources().remove(resourceStack);
    }
  }

  //todo 资源持续消耗型?
  @Override
  public void update(T entity){
  }

  @Override
  public void display(Stats stats){

  }

  @Override
  public void build(T entity, Table table){

  }

  @Override
  public float efficiency(T entity){
    return Mathf.num(entity.resources().has(resourceStacks));
  }

  @Override
  public Seq<Content> filter(){
    return Seq.with(resourceStacks).map(r -> r.item);
  }
}
