package LLL.content.extensions;

import LLL.lib.singularity.world.blocks.product.*;
import arc.func.*;
import arc.struct.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;
import universecore.util.handler.*;

public class OvulamBlockFlags{
  private static final EnumHandler<BlockFlag> handler = new EnumHandler<>(BlockFlag.class);
  private static ObjectMap<Boolf<Block>, BlockFlag> flagBlocks = new ObjectMap<>();

  public static BlockFlag trophosomeTarget = add("trophosomeTarget");
  public static BlockFlag buildPayloadProducer = add("buildPayloadProducer", b -> {
    return b instanceof BlockProducer || b instanceof PayloadSource || b instanceof PayloadCrafter;
  });
  public static BlockFlag payloadConsumer = add("payloadConsumer", b -> {
    return b instanceof PayloadDeconstructor;
  });

  public static BlockFlag add(String addition){
    return handler.addEnumItemTail(addition);
  }

  public static BlockFlag add(String addition, Boolf<Block> flagBlock){
    BlockFlag flag = add(addition);

    flagBlocks.put(flagBlock, flag);

    return flag;
  }

  public static void addFlagBlock(BlockFlag flag, Boolf<Block> flagBlock){
    flagBlocks.put(flagBlock, flag);
  }

  public static void load(){
    FieldHandler.setValueDefault(BlockFlag.class, "all", BlockFlag.values());
    FieldHandler.setValueDefault(Vars.indexer, "flagMap", new Seq[Team.all.length][BlockFlag.all.length]);
  }

  public static void init(){
    Seq<BlockFlag> flags = new Seq<>(false);
    for(Block block : Vars.content.blocks()){
      EnumSet<BlockFlag> set = block.flags;
      boolean fit = false;

      for(ObjectMap.Entry<Boolf<Block>, BlockFlag> entry : flagBlocks){
        if(entry.key.get(block)){
          fit = true;
          if(!set.contains(entry.value)){
            flags.addUnique(entry.value);
          }
        }
      }

      if(fit){
        flags.addAll(set.array);
        block.flags = EnumSet.of(flags.toArray(BlockFlag.class));

        flags.clear();
      }
    }
  }
}
