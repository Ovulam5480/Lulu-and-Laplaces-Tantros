package LLL.content.extensions;

import arc.func.*;
import arc.struct.*;
import mindustry.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

public class OvulamBlockFlags{
  private static final EnumAccessor0<BlockFlag> handler = Enums.accessEnum(BlockFlag.class);
  private static final ObjectMap<Boolf<Block>, BlockFlag> flagBlocks = new ObjectMap<>();

  public static BlockFlag trophosomeTarget = add("trophosomeTarget");
  public static BlockFlag buildPayloadProducer = add("buildPayloadProducer", b -> {
    return b instanceof BlockProducer || b instanceof PayloadSource || b instanceof PayloadCrafter;
  });
  public static BlockFlag payloadConsumer = add("payloadConsumer", b -> {
    return b instanceof PayloadDeconstructor;
  });

  public static BlockFlag add(String addition){
    return handler.appendEnumInstance(addition, false);
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
    //    FieldHandler.setValueDefault(Vars.indexer, "flagMap", new Seq[Team.all.length][BlockFlag.all.length]);
    Reflection.accessStaticField(BlockFlag.class, BlockFlag[].class, "all").set(BlockFlag.values());
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
