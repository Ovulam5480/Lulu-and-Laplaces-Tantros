package lulu.lib.singularity.game.researchs;

import arc.*;
import arc.struct.*;
import lulu.lib.singularity.core.*;
import mindustry.ctype.*;

public class Inspire{
  public final String name;
  public final int provTechPoints;
  public final int provResearchMultiplier;
  public final Seq<UnlockableContent> contents = new Seq<>();

  public boolean applied;
  public String localized;

  public Inspire(String name, int provTechPoints, int provResearchMultiplier, UnlockableContent... contents){
    this.name = name;
    this.localized = name;
    this.provTechPoints = provTechPoints;
    this.provResearchMultiplier = provResearchMultiplier;
    this.contents.addAll(contents);
  }

  public float provProgress;

  public void init(ResearchProject project){
  }

  public void applyTrigger(ResearchProject project){
  }

  public void apply(ResearchProject project){
    if(applied) return;

    applied = true;
    provProgress = 1;

    for(UnlockableContent content : contents){
      content.unlock();
    }

    Events.fire(new SglEventTypes.ResearchInspiredEvent(this, project));
  }

  public void reset(){
    applied = false;
    provProgress = 0;
  }

  public boolean isApplied(){
    return applied;
  }
}
