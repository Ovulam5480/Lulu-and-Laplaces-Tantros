package lulu.research;

import arc.struct.*;
import mindustry.ctype.*;

public class ResearchGroup{
  public final String zoneKey;

  private final OrderedMap<String, ResearchProject> projects = new OrderedMap<>();
  private final OrderedMap<RevealGroup, Seq<ResearchProject>> revealGroups = new OrderedMap<>();

  public final ObjectSet<UnlockableContent> scopedUnlocks = new ObjectSet<>();

  public ResearchGroup(String zoneKey){
    this.zoneKey = zoneKey;
  }

  public void addProject(ResearchProject project){
    projects.put(project.name, project);
    project.group = this;
  }

  public ResearchProject getResearch(String name){
    return projects.get(name);
  }

  public Seq<ResearchProject> listResearches(){
    return projects.values().toSeq();
  }

  public ResearchProject getResearchByContent(UnlockableContent content){
    return projects.values().toSeq().find(p -> p.contents.contains(content));
  }

  public void init(){
    load();

    for(ResearchProject value : projects.values()){
      value.init();
      if(value.reveal != null) revealGroups.get(value.reveal, Seq::new).add(value);
    }

    revealGroups.keys().forEach(RevealGroup::init);
  }

  public void reset(){
    projects.values().forEach(ResearchProject::reset);
    revealGroups.keys().forEach(RevealGroup::reset);
  }

  public void save(){
    projects.values().forEach(ResearchProject::save);
  }

  public void load(){
    projects.values().forEach(ResearchProject::load);
  }
}
