package LLL.lib.multiblock;

import LLL.lib.multiblock.extend.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.world.*;

import java.lang.reflect.*;

public class MultiBlockLib{

  public static final Seq<Block> mirrorList = new Seq<>();
  public static Block block;

  public static final int maxsize = 8;
  public static LinkBlock[] linkEntity, linkEntityLiquid, linkNeoplasm;
  public static PlaceholderBlock[] placeholderEntity;

  public void postLoad(){
    Events.on(EventType.ContentInitEvent.class, e -> {
      mirrorList.each(block -> {
        Block mirror = new Block(block.name + "-mirror");
        Field[] fields = Block.class.getFields();
        for(Field field : fields){
          try{
            if(field.getModifiers() == Modifier.PUBLIC && !(field.getName().equals("name") || field.getName().equals("mirror"))){
              field.set(mirror, field.get(block));
            }

            if(field.getModifiers() == Modifier.PUBLIC && field.getName().equals("mirror")){
              field.set(mirror, true);
            }
          }catch(IllegalAccessException ex){
            throw new ArcRuntimeException(ex);
          }
        }
        mirror.init();
        mirror.loadIcon();
        mirror.load();
      });
    });
  }

  public static void loadBlock(){
    linkEntity = new LinkBlock[maxsize];
    linkEntityLiquid = new LinkBlock[maxsize];
    placeholderEntity = new PlaceholderBlock[maxsize];
    linkNeoplasm = new LinkBlock[maxsize];
    for(int i = 0; i < maxsize; i++){
      int s = i + 1;
      linkEntity[i] = new LinkBlock("link-entity-" + s){{
        size = s;
      }};
      linkEntityLiquid[i] = new LinkBlock("link-entity-liquid-" + s){{
        size = s;
        outputsLiquid = true;
      }};
      linkNeoplasm[i] = new NeoplasmLinkBlock("link-neoplasm-" + s){{
        size = s;
      }};
      placeholderEntity[i] = new PlaceholderBlock("placeholder-entity-" + s){{
        size = s;
      }};
    }
  }
}
