package lulu.world.components.buildEntityComps;

import lulu.ctype.*;
import lulu.entities.gen.*;
import lulu.world.components.blockComps.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.util.*;
import ent.anno.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class PayloadCrafterBuildComp<T extends Building & ProducerBuildc<T>> implements ProducerBuildc<T>, PayloadBuildc{
  @Annotations.Import
  Block block;
  @Annotations.Import
  float x, y;
  @Annotations.Import
  Team team;
  @Annotations.Import
  ItemModule items;

  public PayloadCrafterBlockComp<T> getPayloadCrafterBlock(){
    return (PayloadCrafterBlockComp<T>)block;
  }

  @Annotations.Replace(3)
  public boolean acceptUnitPayload(Unit unit){
    PayloadCrafterBlockComp<T> crafterBlock = getPayloadCrafterBlock();
    return inputting() == null && !consumer().hasConsume() || crafterBlock.filter().filter(self(), ResourceType.payload, unit.type, true);
  }

  @Annotations.Replace(3)
  public boolean canControlSelect(Unit unit){
    return block.acceptsPayload && !unit.spawnedByCore && unit.type.allowedInPayloads && payloads().isEmpty() && acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build == self();
  }

  @Annotations.Replace(3)
  public void onControlSelect(Unit player){
    handleUnitPayload(player, p -> payloads().add(p));
  }

  @Annotations.Replace(3)
  public boolean shouldConsume(){
    if(!shouldConsumeProducer()) return false;
    return outputting() == null || Math.abs(outputting().x() - x) >= block.size * tilesize / 2f + 1 || Math.abs(outputting().y() - y) >= block.size * tilesize / 2f + 1;
  }

  @Annotations.Replace(3)
  public boolean acceptPayload(Building source, Payload payload){
    PayloadCrafterBlockComp<T> crafterBlock = getPayloadCrafterBlock();
    return (source == self() || (block.acceptsPayload && inputting() == null && (!consumer().hasConsume()
      || crafterBlock.filter().filter(self(), ResourceType.payload, payload.content(), true)))) && payloads().total() < payloadCapacity();
  }

  @Annotations.Replace(3)
  @Override
  public double sense(LAccess sensor){
    return sensePayloadCrafter(sensor);
  }

  public double sensePayloadCrafter(LAccess sensor){
    if(sensor == LAccess.payloadCount) return payloads().total();
    return senseCrafter(sensor);
  }

  @Override
  public void craftTrigger(){
    if(!payloads().isEmpty()) getPayload().set(x, y, rotdeg());
  }

  public void drawConstructingPayload(){
    ProducePayload<T> p;
    if(producer().current != null && (p = producer().current.get(ResourceType.payload)) != null){
      Draw.draw(Layer.blockOver, () -> Drawf.construct(self(), p.payloads[0].item, rotdeg() - 90f, progress(), workEfficiency(), totalProgress()));
    }
  }

  @Annotations.Replace(3)
  public boolean acceptItem(Building source, Item item){
    PayloadCrafterBlockComp<T> crafterBlock = getPayloadCrafterBlock();
    ItemStack stack;
    return source.interactable(team) && block.hasItems
      && (source == self() || (!(consumer().hasConsume() || consumer().hasOptional()) || crafterBlock.filter().filter(self(), ResourceType.item, item, acceptAll(ResourceType.item))))
      && items.get(item) < ((stack = Structs.find(consumer().current.<ConsumeItemBase<T>>get(ResourceType.item).consItems, e -> e.item == item)) != null ? stack.amount * crafterBlock.itemCapacityMulti() : 0);
  }

  public abstract float rotdeg();

  public abstract void handleUnitPayload(Unit unit, Cons<Payload> grabber);
}
