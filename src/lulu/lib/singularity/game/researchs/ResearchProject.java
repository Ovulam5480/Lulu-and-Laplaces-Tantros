package lulu.lib.singularity.game.researchs;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import lulu.*;
import lulu.lib.singularity.*;
import lulu.lib.singularity.core.*;
import lulu.lib.singularity.world.blocks.research.*;
import lulu.type.resourceStacks.*;
import mindustry.ctype.*;

public class ResearchProject{
  public final String name;
  public final int techRequires;
  public final int techRequiresRandom;

  public final Seq<ResearchProject> dependencies = new Seq<>();
  public final Seq<UnlockableContent> contents = new Seq<>();
  public final Seq<ResearchDevice> requireDevices = new Seq<>();

  /** 完成本研究所需消耗的资源（由 DSL 的 {@code cost(...)} 设置），研究台将消耗这些资源以推进研究。 */
  public ResourceStack<?>[] researchCost = new ResourceStack<?>[0];

  public String localizedName;
  public String description;
  public String slogan = "slogan";
  public TextureRegion icon;

  /**
   * BOSS 战区块专属研究：完成后的解锁内容仅在该区块（当前 zoneKey 对应的组）内可用。
   */
  public boolean sectorScoped = false;

  @Nullable
  public Inspire inspire;
  @Nullable
  public RevealGroup reveal;

  public boolean processing;

  public boolean showIfRevealess;
  public boolean hideTechs;
  public ResearchGroup group;

  protected boolean isCompleted;
  protected int techRequiresReal;
  protected int researched;

  public ResearchProject(String name, int techRequires, int techRequiresRandom){
    this.name = name;
    this.techRequires = techRequires;
    this.techRequiresRandom = techRequiresRandom;

    localizedName = Core.bundle.get("research." + name + ".name");
    description = Core.bundle.get("research." + name + ".description");
    icon = Singularity.getModAtlas("research_" + name, null);
  }

  public ResearchProject(String name, int techRequires){
    this(name, techRequires, 0);
  }

  public ResearchProject hideTechs(){
    hideTechs = true;
    return this;
  }

  public ResearchProject showRevealess(){
    showIfRevealess = true;
    return this;
  }

  public ResearchProject setInspire(Inspire inspire){
    this.inspire = inspire;
    return this;
  }

  public ResearchProject setReveal(RevealGroup reveal){
    this.reveal = reveal;
    return this;
  }

  public ResearchProject addDependency(ResearchProject... dependencies){
    this.dependencies.addAll(dependencies);
    return this;
  }

  public ResearchProject addContent(UnlockableContent... contents){
    this.contents.addAll(contents);
    return this;
  }

  public ResearchProject addRequireDevice(ResearchDevice... requireDevices){
    this.requireDevices.addAll(requireDevices);
    return this;
  }

  public boolean checkDeviceValid(Seq<ResearchDevice> devices){
    o:
    for(ResearchDevice requireDevice : requireDevices){
      for(ResearchDevice device : devices){
        if(device.isCompatible(requireDevice)) continue o;
      }

      return false;
    }

    return true;
  }

  public void init(){
    if(inspire != null){
      inspire.init(this);
      inspire.applyTrigger(this);
    }

    if(dependenciesCompleted() && researched >= techRequiresReal){
      isCompleted = true;
    }
  }

  public boolean dependenciesCompleted(){
    for(ResearchProject dependency : dependencies){
      if(!dependency.isCompleted()) return false;
    }

    return true;
  }

  public boolean isRevealed(){
    return reveal == null || reveal.isRevealed();
  }

  public boolean requiresRevealed(){
    return reveal == null || reveal.require == null || reveal.require.isRevealed();
  }

  public boolean isCompleted(){
    return isCompleted;
  }

  public int getRealRequireTechs(){
    return techRequiresReal;
  }

  public int getResearched(){
    return researched;
  }

  public float progress(){
    return (float)researched / techRequiresReal;
  }

  public boolean isProcessing(){
    return processing;
  }

  public boolean researchProcess(int techPoints){
    if(isCompleted()) return true;
    researched += techPoints;

    boolean res = checkComplete();

    save();

    return res;
  }

  public boolean checkComplete(){
    if(isCompleted()) return true;

    for(ResearchProject dependency : dependencies){
      if(!dependency.checkComplete()) return false;
    }

    if(researched >= techRequiresReal){
      completeNow();
      return true;
    }

    return false;
  }

  public void completeNow(){
    isCompleted = true;

    researched = techRequiresReal;

    for(UnlockableContent content : contents){
      if(sectorScoped) group.scopedUnlocks.add(content);
      else content.unlock();
    }

    Events.fire(new SglEventTypes.ResearchCompletedEvent(this));

    save();
  }

  public void applyInspireNow(){
    if(inspire != null) inspire.apply(this);
  }

  public void revealNow(){
    if(reveal != null) reveal.reveal();
  }

  public void reset(){
    techRequiresReal = techRequires + Mathf.random(techRequiresRandom);
    researched = 0;
    isCompleted = false;

    for(UnlockableContent content : contents){
      if(sectorScoped) group.scopedUnlocks.remove(content);
      else content.clearUnlock();
    }

    if(inspire != null) inspire.reset();

    save();
  }

  public void load(){
    techRequiresReal = LuluMod.globals.getInt("research_" + name + "_requireReal", techRequires + Mathf.random(techRequiresRandom));
    researched = LuluMod.globals.getInt("research_" + name + "_researched", 0);

    if(sectorScoped && LuluMod.globals.getBool("research_scoped_" + group.zoneKey + "_" + name, false)){
      group.scopedUnlocks.addAll(contents);
    }
  }

  public void save(){
    LuluMod.globals.put("research_" + name + "_requireReal", techRequiresReal);
    LuluMod.globals.put("research_" + name + "_researched", researched);
    if(sectorScoped) LuluMod.globals.put("research_scoped_" + group.zoneKey + "_" + name, true);
  }
}
