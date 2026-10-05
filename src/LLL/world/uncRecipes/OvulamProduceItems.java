package LLL.world.uncRecipes;

import arc.math.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.components.blockcomp.*;
import universecore.world.producers.*;

public class OvulamProduceItems<T extends Building & ProducerBuildComp> extends ProduceItems<T>{
  public OvulamProduceItems(ItemStack[] items){
    super(items);
  }

  public OvulamProduceItems(Item item, int amount){
    this(new ItemStack[]{new ItemStack(item, amount)});
  }

  @Override
  public void produce(T entity){
    float f = multiple(entity);
    if(!random){
      for(ItemStack stack : items){
        int amount = stack.amount * ((int)Math.floor(f)) + Mathf.num(Math.random() < f % 1);
        amount = Math.min(amount, entity.getMaximumAccepted(stack.item) - entity.items.get(stack.item));
        for(int i = 0; i < amount; i++){
          entity.handleItem(entity, stack.item);
        }
      }
    }
    /*随机产出一种产物，amount参数变更为权*/
    else{
      int sum = 0;
      for(ItemStack stack : items){
        sum += stack.amount;
      }

      int i = Mathf.random(sum);
      int count = 0;
      Item item = null;

      for(ItemStack stack : items){
        if(i >= count && i < count + stack.amount){
          item = stack.item;
          break;
        }
        count += stack.amount;
      }
      if(item != null){
        //todo
        int amount = (int)(Math.floor(f) + Mathf.num(Math.random() < f % 1));
        amount = Math.min(amount, entity.getMaximumAccepted(item) - entity.items.get(item));
        for(int l = 0; l < amount; l++){
          entity.handleItem(entity, item);
        }
      }
    }
  }

  @Override
  public boolean valid(T entity){
    if(entity.items == null) return false;

    boolean res = false;
    for(ItemStack stack : items){
      if(stack.amount * multiple(entity) > entity.getMaximumAccepted(stack.item) - entity.items.get(stack.item)){
        if(blockWhenFull) return false;
      }else res = true;
    }
    return res;
  }
}
