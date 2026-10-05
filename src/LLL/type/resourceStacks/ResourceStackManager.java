package LLL.type.resourceStacks;

import arc.func.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.ctype.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class ResourceStackManager{
  public static Seq<Resource<?>> resources = new Seq<>();
  //Item.class -> new ItemResourceStack();
  public static ObjectMap<Class<?>, Prov<ResourceStack<?>>> resourceMap = new ObjectMap<>();
  //Item.class -> ItemResourceStack.class
  public static ObjectMap<Class<?>, Class<? extends ResourceStack<?>>> classMap = new ObjectMap<>();
  //Item.class -> ItemResourceStack
  public static ObjectMap<Class<?>, ResourceStack<?>> instanceMap = new ObjectMap<>();
  //Item.class -> copper,lead, ...
  public static ObjectMap<Class<?>, Seq<?>> resourceInstances = new ObjectMap<>();
  //Item.class, Liquid.class, ...
  public static Seq<Class<?>> resourceClasses = new Seq<>();

  public static Seq<ResourceStack<?>> resourceStackInstances = Seq.with(
    new ItemResourceStack(),
    new LiquidResourceStack(),
    new PowerResourceStack(),
    new PayloadResourceStack()
    //new SectorResourceStack()
  );

  public static <ObjectType extends UnlockableContent> void register(Class<ObjectType> objectClass,
                                                                     Class<? extends ResourceStack<ObjectType>> stackClass,
                                                                     Prov<ResourceStack<ObjectType>> resourceProv,
                                                                     ResourceStack<ObjectType> resourceStackInstance,
                                                                     int id){
    resources.add(new Resource<>(objectClass, null, resourceProv, stackClass, resourceStackInstance, id));
    resources.sort(Comparator.comparingInt(a -> a.id));

    resources.add(
      new Resource<>(objectClass,
        null,
        resourceProv,
        stackClass,
        (ResourceStack<ObjectType>)resourceStackInstances.find(stack -> stack.getClass() == stackClass),
        resources.size));

    resourceMap.put(objectClass, resourceProv::get);
    classMap.put(objectClass, stackClass);
    resourceClasses.add(objectClass);
    instanceMap.put(objectClass, resourceStackInstances.find(stack -> stack.getClass() == stackClass));
  }

  public static <ObjectType extends UnlockableContent> void init(Class<ObjectType> objectClass, Seq<ObjectType> instances){
    resourceInstances.put(objectClass, instances);
  }

  public static <ObjectType extends UnlockableContent> ResourceStack<ObjectType> getResourceInstance(ObjectType object, float amount){
    Class<ObjectType> objectClass = (Class<ObjectType>)(object.getClass().isAnonymousClass() ? object.getClass().getSuperclass() : object.getClass());
    return getResourceInstance(object, objectClass, amount);
  }

  public static <ObjectType extends UnlockableContent> ResourceStack<ObjectType> getResourceInstance(ObjectType object, Class<ObjectType> objectClass, float amount){
//    ResourceStack<ObjectType> stack = (ResourceStack<ObjectType>)Pools.obtain(
//      classMap.get(objectClass), () -> resourceMap.get(objectClass).get());
    ResourceStack<ObjectType> stack = (ResourceStack<ObjectType>)resourceMap.get(objectClass).get();

    stack.set(object, amount);
    return stack;
  }

  public static <ObjectType extends UnlockableContent> ResourceStack<ObjectType> getResourceInstanceByClass(Object object, Class<ObjectType> objectClass, float amount){
    return getResourceInstance((ObjectType)object, objectClass, amount);
  }

  public static <ObjectType extends UnlockableContent> ResourceStack<ObjectType> getResourceInstanceByType(Object object, float amount){
    Class<ObjectType> objectClass = (Class<ObjectType>)resourceMap.keys().toSeq().find(clazz -> clazz.isInstance(object));
    if(objectClass == null) return null;

    return getResourceInstanceByClass(object, objectClass, amount);
  }

  public static Class<?> getResourceInstanceClass(Object object){
    return resourceClasses.find(clazz -> clazz.isInstance(object));
  }

  public static int getResourceInstanceID(Object object){
    return instanceMap.get(getResourceInstanceClass(object)).id();
  }

  public static void writeResourceStacks(Writes writes, Seq<ResourceStack<?>> resourceStacks){
    writes.i(resourceStacks.size);

    for(ResourceStack<?> stack : resourceStacks){
      writes.i(stack.id());
      stack.write(writes);
    }
  }

  public static Seq<ResourceStack<?>> readResourceStacks(Reads reads){
    Seq<ResourceStack<?>> resourceStacks = new Seq<>();

    int size = reads.i();
    for(int i = 0; i < size; i++){
      int id = reads.i();
      for(ResourceStack<?> instance : resourceStackInstances){
        if(instance.id() == id){
          ResourceStack<?> stack = instance.read(reads);

          resourceStacks.add(stack);
          break;
        }
      }
    }

    return resourceStacks;
  }

  public static class Resource<T extends UnlockableContent>{
    //Item.class
    public Class<T> resourceClass;
    //copper, lead, ...
    public Seq<T> resourceInstances;
    //new ItemResourceStack()
    public Prov<ResourceStack<T>> resourceProv;
    //ItemResourceStack.class
    public Class<? extends ResourceStack<T>> resourceStackClass;
    //ItemResourceStack
    public ResourceStack<T> resourceStackInstance;
    //0
    public int id;

    public Resource(Class<T> resourceClass,
                    Seq<T> resourceInstances,
                    Prov<ResourceStack<T>> resourceProv,
                    Class<? extends ResourceStack<T>> resourceStackClass,
                    ResourceStack<T> resourceStackInstance,
                    int id){
      this.resourceClass = resourceClass;
      this.resourceInstances = resourceInstances;
      this.resourceProv = resourceProv;
      this.resourceStackClass = resourceStackClass;
      this.resourceStackInstance = resourceStackInstance;
      this.id = id;
    }
  }
}
