package LLL.content;

import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

import java.util.*;

/**
 * contentType处理对象，用于创建新的内容类型，以及处理类型的显示排序
 *
 * @author EBwilson
 * @since 1.0
 */
public class UncContentType{
  private final static FieldAccessor<ContentLoader, ObjectMap[]> contentNameMapField = Reflection.accessField(ContentLoader.class, ObjectMap[].class, "contentNameMap");
  private final static FieldAccessor<ContentLoader, Seq[]> contentMapField = Reflection.accessField(ContentLoader.class, Seq[].class, "contentMap");
  private final static FieldAccessorStatic<ContentType[]> allField = Reflection.accessStaticField(ContentType.class, ContentType[].class, "all");
  private final static EnumAccessor1<ContentType, Class> handler = Enums.accessEnum(ContentType.class, Class.class);

  public static final ObjectMap<ContentType, UncContentType> allUncContentType = new ObjectMap<>();

  public static ContentType[] displayContentList = ContentType.all;

  static{
    for(ContentType value : ContentType.values()){
      allUncContentType.put(value, new UncContentType(value));
    }
  }

  public final ContentType value;
  public final int ordinal;

  public final boolean display;

  /**
   * 内部方法，用于创建与已有{@link ContentType}关联的标识
   */
  private UncContentType(ContentType type){
    this.value = type;
    this.ordinal = type.ordinal();
    this.display = true;
  }

  /**
   * 创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name         该类型的名称
   * @param contentClass 这个contentType关联的内容类
   */
  public UncContentType(String name, Class<? extends Content> contentClass){
    this(name, ContentType.values().length, contentClass);
  }

  /**
   * 创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name         该类型的名称
   * @param ordinal      这个类型在数据库中显示的位置序数
   * @param contentClass 这个contentType关联的内容类
   */
  public UncContentType(String name, int ordinal, Class<? extends Content> contentClass){
    this(name, ordinal, contentClass, true);
  }

  /**
   * 创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name         该类型的名称
   * @param ordinal      这个类型在数据库中显示的位置序数
   * @param contentClass 这个contentType关联的内容类
   * @param display      是否在数据库内显示
   */
  public UncContentType(String name, int ordinal, Class<? extends Content> contentClass, boolean display){
    value = handler.appendEnumInstance(name, contentClass, false);
    this.ordinal = ordinal;
    this.display = display;

    allUncContentType.put(value, this);

    allField.set(ContentType.values());

    reloadDisplay();

    try{
      ObjectMap<String, MappableContent>[] contentNameMap = contentNameMapField.get(Vars.content);
      Seq<Content>[] contentMap = contentMapField.get(Vars.content);

      ArrayList<ObjectMap<String, MappableContent>> contentNameMapList = new ArrayList<>(Arrays.asList(contentNameMap));
      ArrayList<Seq<Content>> contentMapList = new ArrayList<>(Arrays.asList(contentMap));

      contentNameMapList.add(value.ordinal(), new ObjectMap<>());
      contentMapList.add(value.ordinal(), new Seq<>());

      contentNameMapField.set(Vars.content, contentNameMapList.toArray(new ObjectMap[0]));
      contentMapField.set(Vars.content, contentMapList.toArray(new Seq[0]));
    }catch(Throwable e){
      Log.err(e);
    }
  }

  protected static void reloadDisplay(){
    Seq<ContentType> list = new Seq<>(ContentType.values().length);

    for(ContentType type : ContentType.values()){
      UncContentType t = allUncContentType.get(type);
      if(!t.display) continue;
      list.add(t.value);
    }

    list.sort((a, b) -> a.ordinal() - b.ordinal());

    displayContentList = list.toArray(ContentType.class);
  }
}
