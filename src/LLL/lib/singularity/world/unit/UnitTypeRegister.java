package LLL.lib.singularity.world.unit;

import LLL.lib.singularity.*;
import LLL.lib.singularity.contents.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import universecore.util.handler.*;

import java.lang.reflect.*;
import java.util.*;

public class UnitTypeRegister{
  private static final ObjectMap<Class<? extends Unit>, Unit> classCache = new ObjectMap<>();

  public static void registerAll(){
    for(Field field : SglUnits.class.getFields()){
      if(UnitType.class.isAssignableFrom(field.getType())){
        UnitEntityType anno = field.getAnnotation(UnitEntityType.class);
        if(anno != null){
          Class<? extends Unit> type = anno.value();

          int id = classCache.get(type, () -> MethodHandler.newInstanceDefault(type)).classId();

          if(EntityMapping.idMap.length <= id){
            EntityMapping.idMap = Arrays.copyOf(EntityMapping.idMap, EntityMapping.idMap.length * 2);
          }

          if(EntityMapping.idMap[id] == null)
            EntityMapping.idMap[id] = () -> MethodHandler.newInstanceDefault(type);

          String name;
          EntityMapping.nameMap.put(name = Sgl.modName + "-" + field.getName(), EntityMapping.map(id));
          EntityMapping.customIdMap.put(id, name);
        }
      }
    }
  }
}
