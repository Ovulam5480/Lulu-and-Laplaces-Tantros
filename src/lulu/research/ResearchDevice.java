package lulu.research;

import arc.struct.*;
import mindustry.world.*;

public class ResearchDevice extends Block{
  public Seq<ResearchDevice> compatibles = new Seq<>();

  public ResearchDevice(String name){
    super(name);
  }

  public boolean isCompatible(ResearchDevice device){
    return compatibles.contains(device);
  }

  public ResearchDevice setCompatibles(ResearchDevice... compatibles){
    this.compatibles.addAll(compatibles);
    return this;
  }
}
