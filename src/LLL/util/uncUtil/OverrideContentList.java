package LLL.util.uncUtil;

import mindustry.*;
import mindustry.ctype.*;

import static LLL.util.uncUtil.handler.ContentHandler.*;

/**
 * 内容覆盖器实现此接口，用于在执行load()方法中进行内容覆盖
 *
 * @author EBwilson
 * @since 1.0
 */
public interface OverrideContentList{
  void load();

  default void doOverrideContent(ContentType type, short id, MappableContent newContent){
    overrideContent(Vars.content.getByID(type, id), newContent);
  }

  default void doOverrideContent(ContentType type, String name, MappableContent newContent){
    overrideContent(Vars.content.getByName(type, name), newContent);
  }

  default void doOverrideContent(MappableContent oldContent, MappableContent newContent){
    overrideContent(oldContent, newContent);
  }
}
