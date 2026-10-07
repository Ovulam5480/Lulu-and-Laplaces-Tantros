package LLL.util.uncUtil.handler;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

/**
 * 用于操作content的静态工具集
 *
 * @author EBwilson
 * @since 1.0
 */
public class ContentHandler{
  private static final FieldAccessor<ContentLoader, Seq[]> contentMapField = Reflection.accessField(ContentLoader.class, Seq[].class, "contentMap");
  private static final FieldAccessor<ContentLoader, ObjectMap[]> contentNameMapField = Reflection.accessField(ContentLoader.class, ObjectMap[].class, "contentNameMap");
  private static final FieldAccessor<MappableContent, String> cont = Reflection.accessField(MappableContent.class, String.class, "name");
  private static final FieldAccessor<UnlockableContent, Boolean> unlockedField = Reflection.accessField(UnlockableContent.class, Boolean.class, "unlocked");

  private static Seq<Content>[] contentMap;
  private static ObjectMap<String, MappableContent>[] contentNameMap;

  private static void updateContainer(){
    contentMap = contentMapField.get(Vars.content);
    contentNameMap = contentNameMapField.get(Vars.content);
  }

  public static void removeContent(Content content){
    updateContainer();
    if(content.getContentType().ordinal() < contentMap.length){
      Seq<Content> conts = contentMap[content.getContentType().ordinal()];
      if(conts != null){
        conts.remove(content);
      }
    }

    if(content instanceof MappableContent){
      if(content.getContentType().ordinal() < contentNameMap.length){
        ObjectMap<String, MappableContent> map = contentNameMap[content.getContentType().ordinal()];
        if(map != null){
          map.remove(((MappableContent)content).name);
        }
      }
    }
  }

  /**
   * 用新的content去覆盖一个原有的content，并设置{@link UnlockableContent}的信息
   *
   * @param oldContent 将要被取代的content
   * @param newContent 重写后的content
   * @throws RuntimeException 当oldContent与newContent的类型（ContentType）不相同时抛出
   */
  public static void overrideContent(MappableContent oldContent, MappableContent newContent){
    updateContainer();
    ContentType type = oldContent.getContentType();

    String oldName = oldContent.name;
    String newName = newContent.name;

    cont.set(newContent, oldName);

    if(oldContent.getContentType() != newContent.getContentType())
      throw new RuntimeException("The old content cannot override by new content, because the content type are different");

    if(newContent instanceof UnlockableContent newC){
      newC.localizedName = Core.bundle.get(type + "." + oldName + ".name", oldName);
      newC.description = Core.bundle.getOrNull(type + "." + oldName + ".description");
      newC.details = Core.bundle.getOrNull(type + "." + oldName + ".details");
      unlockedField.set(newC, Core.settings != null && Core.settings.getBool(oldName + "-unlocked", false));
    }

    if(contentNameMap != null){
      contentNameMap[type.ordinal()].put(oldName, newContent);
      contentNameMap[type.ordinal()].remove(newName);
    }

    overrideContent(oldContent, (Content)newContent);
  }

  /**
   * 用新的content去覆盖一个原有的content
   *
   * @param oldContent 将要被取代的content
   * @param newContent 重写后的content
   * @throws RuntimeException 当oldContent与newContent的类型（ContentType）不相同时抛出
   */
  public static void overrideContent(Content oldContent, Content newContent){
    updateContainer();
    short oldId = oldContent.id;
    short newId = newContent.id;

    if(oldContent.getContentType() != newContent.getContentType())
      throw new RuntimeException("The old content cannot override by new content, because the content type are different");

    ContentType type = oldContent.getContentType();
    try{
      newContent.id = oldId;

      if(contentMap != null){
        contentMap[type.ordinal()].set(oldId, newContent);
        contentMap[type.ordinal()].remove(newId);
      }
    }catch(Throwable e){
      Log.err(e);
    }
  }
}
