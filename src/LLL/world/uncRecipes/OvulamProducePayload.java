package LLL.world.uncRecipes;

import arc.func.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.components.blockcomp.*;
import universecore.world.producers.*;

public class OvulamProducePayload<T extends Building & ProducerBuildComp> extends ProducePayload<T>{
  public OvulamProducePayload(PayloadStack[] payloads, Boolf2<T, UnlockableContent> valid){
    super(payloads, valid);
  }
}
