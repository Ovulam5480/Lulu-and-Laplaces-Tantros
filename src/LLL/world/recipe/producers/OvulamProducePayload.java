package LLL.world.recipe.producers;

import LLL.entities.gen.*;
import arc.func.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;

public class OvulamProducePayload<T extends Building & ProducerBuildc> extends ProducePayload<T>{
  public OvulamProducePayload(PayloadStack[] payloads, Boolf2<T, UnlockableContent> valid){
    super(payloads, valid);
  }
}
