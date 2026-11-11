package lulu.world.recipe.consumers;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.research.*;
import lulu.type.resourceStacks.*;
import lulu.world.blocks.*;
import lulu.world.components.buildComps.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

/**
 * 研究台专用消耗器：其消耗配方并非固定，而是取决于建筑当前选中的研究（{@link ResearchProject#researchCost}）。
 * <p>效率（efficiency）仅在满足以下条件时为 1，否则为 0：
 * <ol>
 *   <li>已选中某个未完成的研究；</li>
 *   <li>建筑周围存在相邻的研究模块（{@link ResearchDevice}），且该研究要求的模块均兼容；</li>
 *   <li>建筑资源模块中拥有完成该研究所需的全部资源。</li>
 * </ol>
 * 当效率为 1 并持续足够时长后，资源被一次性消耗，并在 {@code craftTrigger()} 中推进/完成研究。
 */
public class ConsumeResearch<T extends Building & ConsumerBuildc<T>> extends BaseConsume<T>{
  private ResearchPlatform.ResearchPlatformBuild asPlatform(T entity){
    return (ResearchPlatform.ResearchPlatformBuild)entity;
  }

  private ResourceBuildComp asResource(T entity){
    return (ResourceBuildComp)entity;
  }

  @Override
  public ResourceType type(){
    return ResourceType.resource;
  }

  @Override
  public boolean hasIcons(){
    return false;
  }

  @Override
  public void buildIcons(Table table){
  }

  @Override
  public void merge(BaseConsume<T> other){
  }

  @Override
  public void consume(T entity){
    ResearchPlatform.ResearchPlatformBuild b = asPlatform(entity);
    if(b.selected == null) return;

    for(ResourceStack<?> s : b.selected.researchCost){
      asResource(entity).resources().remove(s);
    }
  }

  @Override
  public void update(T entity){
  }

  @Override
  public void display(Stats stats){
  }

  @Override
  public void build(T entity, Table table){
    ResearchPlatform.ResearchPlatformBuild b = asPlatform(entity);
    if(b.selected == null || b.selected.researchCost.length == 0) return;

    ConsumeResource.buildResourceIcons(table, b.selected.researchCost, -1);
  }

  @Override
  public float efficiency(T entity){
    ResearchPlatform.ResearchPlatformBuild b = asPlatform(entity);
    if(b.selected == null || b.selected.isCompleted()) return 0;

    /*校验相邻研究模块，并要求该研究所需的模块均兼容*/
    Seq<ResearchDevice> devices = new Seq<>();
    for(Building adj : entity.proximity){
      if(adj != null && adj.block instanceof ResearchDevice rd) devices.add(rd);
    }
    if(devices.isEmpty()) return 0;
    if(!b.selected.checkDeviceValid(devices)) return 0;

    return Mathf.num(asResource(entity).resources().has(b.selected.researchCost));
  }

  @Override
  public Seq<Content> filter(){
    return new Seq<>();
  }
}
