package LLL.util.uncUtil.mods;

import LLL.util.uncUtil.*;
import arc.files.*;
import arc.util.serialization.*;

public class ModInfo{
  public final String name;
  public final String version;
  public final Fi file;

  public ModInfo(Fi modFile){
    if(modFile instanceof ZipFi)
      throw new IllegalArgumentException("given file is a zip file object, please use file object");
    Fi modMeta;
    try{
      modMeta = ModGetter.checkModFormat(modFile);
    }catch(IllegalModHandleException e){
      throw new RuntimeException(e);
    }
    Jval info = Jval.read(modMeta.reader());
    file = modFile;
    name = info.get("name").asString();
    version = info.get("version").asString();
  }

}
