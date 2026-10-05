package LLL.world.blocks.module;

import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

//仅限于建筑和单位的序列化
@SuppressWarnings("unused")
public interface SerializationGameEntityModule extends BuildCompBase{
  @Annotations.BindField(value = "toWrite", initialize = "new arc.struct.Seq<>()")
  default Seq<Entityc> toWrite(){
    return null;
  }

  @Annotations.BindField(value = "isBuildings", initialize = "new arc.struct.BoolSeq()")
  default BoolSeq isBuildings(){
    return null;
  }

  @Annotations.BindField(value = "isBuildings")
  default void isBuildings(BoolSeq isBuildings){
  }

  @Annotations.BindField(value = "posOrId", initialize = "new arc.struct.IntSeq()")
  default IntSeq posOrId(){
    return null;
  }

  @Annotations.BindField(value = "posOrId")
  default void posOrId(IntSeq posOrId){
  }

  default void addToWrite(Seq<Entityc> toWrite){
  }

  default void getFromRead(Queue<Entityc> toRead){
  }

  @Annotations.MethodEntry(entryMethod = "write", paramTypes = {"arc.util.io.Writes -> write"})
  default void writeSerialization(Writes write){
    toWrite().clear();
    addToWrite(toWrite());

    write.i(toWrite().size);
    int i = 0;
    for(Entityc entityc : toWrite()){
      write.i(i++);

      boolean isBuilding = entityc instanceof Building;
      write.bool(isBuilding);

      if(isBuilding){
        write.i(((Building)entityc).pos());
      }else{
        write.i(entityc == null ? -1 : entityc.id());
      }
    }
  }

  @Annotations.MethodEntry(entryMethod = "read", paramTypes = {"arc.util.io.Reads -> read", "byte -> revision"})
  default void readSerialization(Reads read, byte revision){
    int size = read.i();
    boolean[] isBuildings = new boolean[size];
    int[] posOrId = new int[size];

    for(int i = 0; i < size; i++){
      int index = read.i();
      isBuildings[index] = read.bool();
      posOrId[index] = read.i();
    }

    isBuildings(BoolSeq.with(isBuildings));
    posOrId(IntSeq.with(posOrId));
  }

  @Annotations.MethodEntry(entryMethod = "afterReadAll")
  default void afterReadAllSerialization(){
    Queue<Entityc> queue = new Queue<>();

    for(int i = 0; i < isBuildings().size; i++){
      boolean isBuilding = isBuildings().get(i);
      int posOrId = posOrId().get(i);

      queue.add(isBuilding ? Vars.world.build(posOrId) : Groups.unit.getByID(posOrId));
    }

    getFromRead(queue);
  }
}
