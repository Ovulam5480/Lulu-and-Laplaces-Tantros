package lulu.world.components.blockComps;

import lulu.entities.gen.*;
import lulu.world.recipe.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;
import universecore.annotations.*;

public interface UnitCrafterBlockComp<T extends Building & ProducerBuildc<T>> extends PayloadCrafterBlockComp<T>{
  FieldAccessor<UnitType, ItemStack[]> totalRequirements = Reflection.accessField(UnitType.class, ItemStack[].class, "totalRequirements");
  FieldAccessor<UnitType, ItemStack[]> cachedRequirements = Reflection.accessField(UnitType.class, ItemStack[].class, "cachedRequirements");
  FieldAccessor<UnitType, ItemStack[]> firstRequirements = Reflection.accessField(UnitType.class, ItemStack[].class, "firstRequirements");

  @Annotations.BindField(value = "accepts", initialize = "new arc.struct.ObjectIntMap<>()")
  default ObjectIntMap<UnlockableContent> accepts(){
    return null;
  }

  @Annotations.BindField("accepts")
  default void accepts(ObjectIntMap<UnlockableContent> value){
  }

  @Annotations.MethodEntry(entryMethod = "init")
  default void initUnitRequirements(){
    for(BaseConsumers<T> consumer : recipeConsumers()){
      consumer.forAll(consume -> {
        if(consume instanceof ConsumePayload<T> c){
          for(PayloadStack payloadStack : c.payloads){
            accepts().put(payloadStack.item, Math.max(payloadStack.amount, accepts().get(payloadStack.item, 0)));
          }
        }
      });
    }

    for(BaseProducers<T> producer : recipeProducers()){
      producer.forAll(produce -> {
        if(!(produce instanceof ProducePayload<T> p)){
          return;
        }

        if(p.payloads.length == 1
          && ((p.payloads[0].item instanceof UnitType u && u.getFirstRequirements() == null)
          || p.payloads[0].item instanceof Block b && b.requirements.length == 0)){
          ItemSeq seq = new ItemSeq();

          producer.cons.forAll(consume -> {
            if(consume instanceof ConsumeItemBase<T> i){
              seq.add(i.consItems);
            }else if(consume instanceof ConsumePayload<T> cp){
              for(PayloadStack stack : cp.payloads){
                ItemStack[] requirements;

                if(stack.item instanceof Block b){
                  requirements = b.requirements;
                }else if(stack.item instanceof UnitType u){
                  requirements = u.getFirstRequirements();
                }else{
                  requirements = new ItemStack[0];
                }

                for(ItemStack itemStack : requirements){
                  seq.add(itemStack.item, itemStack.amount * stack.amount);
                }
              }
            }
          });

          Seq<ItemStack> requirementSeq = new Seq<>();

          for(ItemStack stack : seq){
            requirementSeq.add(new ItemStack(stack.item, stack.amount / p.payloads[0].amount));
          }

          ItemStack[] requirements = requirementSeq.toArray(ItemStack.class);

          if(p.payloads[0].item instanceof UnitType u){
            totalRequirements.set(u, requirements);
            cachedRequirements.set(u, requirements);
            firstRequirements.set(u, requirements);
          }else if(p.payloads[0].item instanceof Block b){
            b.requirements = requirements;
          }
        }
      });
    }
  }
}
