package LLL.type;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;

public class BuildingModule implements Cloneable{
  public String name;
  public boolean display = true;
  public float data;

  public String name(){
    return name == null ? getClass().getName() : name;
  }

  public String localized(){
    return Core.bundle.get("building-module." + name(), getClass().getSimpleName());
  }

  public void init(Block block){
  }

  public void load(Block block){
  }

  public void addStats(Table table){
    table.add(localized()).left().row();
  }

  public void created(Building build){
  }

  public void update(Building build){
  }

  public void draw(Building build){
  }

  public void drawSelect(Building build){
  }

  public void displayBars(Building build, Table table){
  }

  public void buildConfiguration(Building build, Table table){
  }

  public void removed(Building build){
  }

  public void death(Building build){
  }

  public int version(){
    return 0;
  }

  public void write(Building build, Writes write){
    write.f(data);
  }

  public void read(Building build, Reads read, int revision){
    data = read.f();
  }

  public BuildingModule copy(){
    try{
      return (BuildingModule)clone();
    }catch(CloneNotSupportedException e){
      throw new AssertionError(e);
    }
  }
}
