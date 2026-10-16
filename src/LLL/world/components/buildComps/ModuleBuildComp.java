package LLL.world.components.buildComps;

import LLL.type.*;
import arc.scene.ui.layout.*;
import universecore.annotations.*;

public interface ModuleBuildComp extends BuildCompBase{
  @Annotations.BindField(value = "buildingModules")
  default BuildingModule[] buildingModules(){
    return null;
  }

  @Annotations.BindField(value = "buildingModules")
  default void buildingModules(BuildingModule[] buildingModules){
  }

  BuildingModule[] getModules();

  @Annotations.MethodEntry(entryMethod = "created")
  default void createdModuleBuild(){
    buildingModules(getModules());
    for(BuildingModule module : buildingModules()){
      module.created(getBuilding());
    }
  }

  @Annotations.MethodEntry(entryMethod = "updateTile")
  default void updateModuleBuild(){
    for(BuildingModule module : buildingModules()){
      module.update(getBuilding());
    }
  }

  @Annotations.MethodEntry(entryMethod = "draw")
  default void drawModuleBuild(){
    for(BuildingModule module : buildingModules()){
      module.draw(getBuilding());
    }
  }

  @Annotations.MethodEntry(entryMethod = "drawSelect")
  default void drawSelectModuleBuild(){
    for(BuildingModule module : buildingModules()){
      module.drawSelect(getBuilding());
    }
  }

  @Annotations.MethodEntry(entryMethod = "displayBars", paramTypes = "arc.scene.ui.layout.Table -> table")
  default void displayBarsModuleBuild(Table table){
    for(BuildingModule module : buildingModules()){
      if(module.display) module.displayBars(getBuilding(), table);
    }
  }

  @Annotations.MethodEntry(entryMethod = "buildConfiguration", paramTypes = "arc.scene.ui.layout.Table -> table")
  default void buildConfigurationModuleBuild(Table table){
    for(BuildingModule module : buildingModules()){
      module.buildConfiguration(getBuilding(), table);
    }
  }

  @Annotations.MethodEntry(entryMethod = "onRemoved")
  default void removedModuleBuild(){
    for(BuildingModule module : buildingModules()){
      module.removed(getBuilding());
    }
  }

  @Annotations.MethodEntry(entryMethod = "killed", insert = Annotations.InsertPosition.HEAD)
  default void deathModuleBuild(){
    for(BuildingModule module : buildingModules()){
      module.death(getBuilding());
    }
  }
}