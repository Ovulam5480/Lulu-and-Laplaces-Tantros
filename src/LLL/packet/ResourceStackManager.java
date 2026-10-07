package LLL.packet;

import LLL.packet.ResourceStacks.*;
import arc.func.*;
import arc.struct.*;
import arc.util.io.*;
import arc.util.pooling.*;
import ent.anno.*;

@SuppressWarnings("unchecked")
@Annotations.TypeIOHandler
public class ResourceStackManager{
  //Item.class -> new ItemResourceStack();
  public static ObjectMap<Class<?>, Prov<ResourceStack<?, ?, ?>>> resourceMap = new ObjectMap<>();
  //Item.class -> ItemResourceStack.class
  public static ObjectMap<Class<?>, Class<ResourceStack<?, ?, ?>>> classMap = new ObjectMap<>();
  //Item.class -> copper,lead, ...
  public static ObjectMap<Class<?>, Seq<?>> resourceInstances = new ObjectMap<>();

  public static Seq<ResourceStack<?, ?, ?>> all = Seq.with(
    new ItemResourceStack(),
    new LiquidResourceStack(),
    new PowerResourceStack(),
    new PayloadResourceStack(),
    new SectorResourceStack());

  public static <ObjectType> void register(Class<ObjectType> objectClass,
                                           Class<? extends ResourceStack<?, ?, ?>> stackClass,
                                           Prov<ResourceStack<?, ?, ?>> resourceProv){
    resourceMap.put(objectClass, resourceProv);
    classMap.put(objectClass, (Class<ResourceStack<?, ?, ?>>)stackClass);
  }

  public static <ObjectType> void init(Class<ObjectType> objectClass, Seq<ObjectType> instances){
    resourceInstances.put(objectClass, instances);
  }

  public static <ObjectType> ResourceStack<ObjectType, ?, ?> getResourceInstance(ObjectType object, float amount){
    Class<ObjectType> objectClass = (Class<ObjectType>)(object.getClass().isAnonymousClass() ? object.getClass().getSuperclass() : object.getClass());
    return getResourceInstance(object, objectClass, amount);
  }

  public static <ObjectType> ResourceStack<ObjectType, ?, ?> getResourceInstance(ObjectType object, Class<ObjectType> objectClass, float amount){
    ResourceStack<ObjectType, ?, ?> stack = (ResourceStack<ObjectType, ?, ?>)Pools.obtain(
      classMap.get(objectClass),
      () -> resourceMap.get(objectClass).get());

    stack.set(object, amount);
    return stack;
  }

  public static <ObjectType> ResourceStack<ObjectType, ?, ?> getResourceInstanceByClass(Object object, Class<ObjectType> objectClass, float amount){
    return getResourceInstance((ObjectType)object, objectClass, amount);
  }

  public static <ObjectType> ResourceStack<ObjectType, ?, ?> getResourceInstanceByType(Object object, float amount){
    Class<ObjectType> objectClass = (Class<ObjectType>)resourceMap.keys().toSeq().find(clazz -> clazz.isInstance(object));
    if(objectClass == null) return null;

    return getResourceInstanceByClass(object, objectClass, amount);
  }

  public static void writeResourceStacks(Writes writes, Seq<ResourceStack<?, ?, ?>> resourceStacks){
    writes.i(resourceStacks.size);

    for(ResourceStack<?, ?, ?> stack : resourceStacks){
      writes.i(stack.id());
      stack.write(writes);
    }
  }

  public static Seq<ResourceStack<?, ?, ?>> readResourceStacks(Reads reads){
    Seq<ResourceStack<?, ?, ?>> resourceStacks = new Seq<>();

    int size = reads.i();
    for(int i = 0; i < size; i++){
      int id = reads.i();
      for(ResourceStack<?, ?, ?> instance : all){
        if(instance.id() == id){
          ResourceStack<?, ?, ?> stack = instance.read(reads);

          resourceStacks.add(stack);
          break;
        }
      }
    }

    return resourceStacks;
  }
}
