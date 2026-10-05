package LLL.entities.comp.utilcomps;

import arc.struct.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.*;
import mindustry.gen.*;

//仅限于建筑和单位的序列化
@Annotations.EntityComponent
abstract class SerializationGameEntityComp implements Entityc{
  public transient Seq<Entityc> toWrite = new Seq<>();
  public transient ObjectMap<Integer, Boolean> toRead = new ObjectMap<>();

  public void addToWrite(Seq<Entityc> toWrite){
  }

  public void getFromRead(Queue<Entityc> toRead){
  }

  @Override
  public void write(Writes write){
    addToWrite(toWrite);
    write.i(toWrite.size);

    int i = 0;
    for(Entityc entityc : toWrite){
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

  @Annotations.Extend(Buildingc.class)
  public void read(Reads read, byte revision){
    read(read);
  }

  @Override
  public void read(Reads read){
    int size = read.i();
    boolean[] isBuildings = new boolean[size];
    int[] posOrId = new int[size];

    for(int i = 0; i < size; i++){
      int index = read.i();

      isBuildings[index] = read.bool();
      posOrId[index] = read.i();
    }

    for(int i = 0; i < size; i++){
      toRead.put(posOrId[i], isBuildings[i]);
    }
  }

  @Override
  public void afterReadAll(){
    Queue<Entityc> queue = new Queue<>();

    for(ObjectMap.Entry<Integer, Boolean> entry : toRead){
      queue.add(entry.value ? Vars.world.build(entry.key) : Groups.unit.getByID(entry.key));
    }

    getFromRead(queue);
  }
}
