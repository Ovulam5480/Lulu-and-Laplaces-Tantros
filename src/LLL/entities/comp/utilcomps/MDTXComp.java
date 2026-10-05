package LLL.entities.comp.utilcomps;

import arc.struct.*;
import ent.anno.*;
import mindustry.entities.units.*;

@Annotations.EntityComponent
public class MDTXComp{
  Seq<StatusEntry> statuses(){
    return new Seq<>();
  }

  float healthBalance(){
    return 0;
  }
}
