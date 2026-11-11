package lulu.io;

import arc.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.struct.*;
import lulu.util.uncUtil.mods.*;

import java.util.*;

import static arc.Core.*;

public class ModFiles{
  /**
   * 此mod内部名称
   */
  public static final String modName = "lulu";
  /**
   * 模组文件夹位置
   */
  public static final Fi modDirectory = settings.getDataDirectory().child("mods");
  /**
   * 本模组的文件位置信息
   */
  public static final ModInfo mod = ModGetter.getModWithName(modName);
  /**
   * 此模组的压缩包对象
   */
  public static final ZipFi modFile = new ZipFi(mod.file);
  /**
   * 本模组版本号
   */
  public static final String modVersion = mod.version;
  /**
   * 模组内配置文件存放位置
   */
  public static final Fi internalConfigDir = modFile.child("config");
  /**
   * 模组数据文件夹
   */
  public static final Fi dataDirectory = modDirectory.child("data").child(modName);
  /**
   * 行星上下文目录
   */
  public static final Fi planetDataDirectory = dataDirectory.child("planet_contexts");
  /**
   * 模组配置文件夹
   */
  public static final Fi configDirectory = modDirectory.child("config").child(modName);
  /**
   * 模组的mod_config.hjson配置文件
   */
  public static final Fi configFile = configDirectory.child("mod_config.hjson");
  /**
   * 模组持久全局变量存储文件
   */
  public static final Fi globalVars = dataDirectory.child("global_vars.bin");
  /**
   * 模组持久全局变量备份文件
   */
  public static final Fi globalVarsBackup = dataDirectory.child("global_vars.bin.bak");
  /**
   * 通知标签历史
   */
  public static final Fi notificationHistory = dataDirectory.child("notifyHistory.bin");
  /**
   * 通知标签历史备份
   */
  public static final Fi notificationHistoryBackup = dataDirectory.child("notifyHistory.bin");

  public static TextureRegion getModAtlas(String name){
    return Core.atlas.find(modName + "-" + name);
  }

  public static TextureRegion getModAtlas(String name, TextureRegion def){
    return Core.atlas.find(modName + "-" + name, def);
  }

  public static <T extends Drawable> T getModDrawable(String name){
    return Core.atlas.getDrawable(modName + "-" + name);
  }

  public static Fi getInternalFile(String path){
    return modFile.child(path);
  }

  public static Fi getDocumentFile(String name){
    return getInternalFile("documents").child(Core.bundle.getLocale().toString()).child(name);
  }

  public static Fi getDocumentFile(Locale locale, String name){
    Fi docs = getInternalFile("documents").child(locale.toString());
    return docs.exists() ? docs.child(name) : getInternalFile("documents").child("zh_CN");
  }

  private static final ObjectMap<Fi, String> docCache = new ObjectMap<>();

  public static String getDocument(String name){
    return getDocument(name, true);
  }

  public static String getDocument(String name, boolean cache){
    Fi fi = getDocumentFile(name);
    return cache ? docCache.get(fi, fi::readString) : fi.readString();
  }

  public static String getDocument(Locale locale, String name){
    return getDocumentFile(locale, name).readString();
  }
}
