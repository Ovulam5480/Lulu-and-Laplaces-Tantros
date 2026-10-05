package LLL.world.neoplasm.pedigree.nemertinea;

import LLL.content.blocks.*;
import LLL.world.blocks.module.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmActiniaria extends PayloadDeconstructor implements NeoplasmBlockModule{
  public NeoplasmActiniaria(String name){
    super(name);

    dumpRate = 0;
    deconstructSpeed = 2f;
  }

  @Annotations.ImplEntries
  public class NeoplasmActiniariaBuild extends PayloadDeconstructorBuild implements NeoplasmBuildModule{
    @Override
    public void draw(){
      Draw.rect(baseRegion(), x, y);
    }

    @Override
    public void updateTile(){
      for(IntMap.Entry<Item> entry : Neoplasm.transform){
        Item material = Vars.content.item(entry.key);
        ItemStack stack = Neoplasm.all.get(entry.value).find(is -> is.item == material);

        if(items.has(material, stack.amount) && acceptItem(null, entry.value)){
          items.remove(material, stack.amount);
          items.add(entry.value, 1);
        }
      }

      super.updateTile();
    }

    @Override
    public void updateItemDump(Building self, float delta){
      float timer = dumpTimer() + delta;
      if(timer > 1f){
        if(neuron() == null){
          for(Item item : Vars.content.items()){
            if(items().has(item) && !Neoplasm.transform.containsKey(item.id)){
              dump(item);
              break;
            }
          }
        }else if(items().any()){
          Item todump = null;

          for(Item item : Vars.content.items()){
            if(items().has(item) && !Neoplasm.transform.containsKey(item.id)){
              todump = item;
              break;
            }
          }

          if(todump != null){
            Building target = dumpTarget();

            if(target != null){
              if(target.acceptItem(self, todump)){
                target.handleItem(self, todump);
                items().remove(todump, 1);
              }
            }else{
              cortex().getItemDumpTarget(this);
            }
          }
        }
        timer = 0;
      }
      setDumpTimer(timer);
    }
  }
}
