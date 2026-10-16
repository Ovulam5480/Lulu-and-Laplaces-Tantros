package lulu.lib.singularity;

import lulu.lib.singularity.core.*;
import lulu.lib.singularity.graphic.*;
import lulu.lib.singularity.ui.*;
import lulu.util.uncUtil.mods.*;
import arc.*;
import arc.files.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;

import java.util.concurrent.*;

import static arc.Core.*;

public class Sgl{
  public static final String NL = System.lineSeparator();

  /**
   * 此mod内部名称
   */
  //public static final String modName = "singularity";
  public static final String modName = "lulu";
  /**
   * 模组文件夹位置
   */
  public static final Fi modDirectory = settings.getDataDirectory().child("mods");
  /**
   * 本模组的文件位置
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

  //URIs
  public static final String telegram = "https://t.me/EB_wilson";
  public static final String facebook = "https://www.facebook.com/profile.php?id=100024490163405";
  public static final String qqGroup = "https://jq.qq.com/?_wv=1027&k=BTHaN7gd";
  public static final String telegramGroup = "https://t.me/+Dv_nTdi3e_k0ZDk9";
  public static final String modDevelopGroup = "https://jq.qq.com/?_wv=1027&k=vjybgqDG";
  public static final String githubUserAvatars = "https://avatars.githubusercontent.com/";
  public static final String githubProject = "https://github.com/EB-wilson/Singularity";
  public static final String afdian = "";
  public static final String patreon = "";
  public static final String libGithubProject = "https://github.com/EB-wilson/UniverseCore";
  public static final String discord = "";
  public static final String githubRawMaster = "https://raw.githubusercontent.com/EB-wilson/Singularity/master/";
  public static final String githubProjReleaseApi = "https://api.github.com/repos/EB-wilson/Singularity/releases/latest";
  public static final String publicInfo = githubRawMaster + "publicInfo/";

  public static final Team none = Team.get(255);
  /**
   * 模组配置存储器
   */
  public static ModConfig config = new ModConfig();

  /**
   * 持久保存的全局变量集
   */
  public static Settings globals;
  /**
   * ui类存放对象
   */
  public static SglUI ui;

  public static ModsInteropAPI interopAPI = new ModsInteropAPI();

  public static ExecutorService executor = Threads.unboundedExecutor("SGL_EXEC", 1);

  public static void init(){
    globals = new Settings(){
      {
        setAutosave(true);
        setDataDirectory(Sgl.dataDirectory);
      }

      @Override
      public Fi getSettingsFile(){
        return globalVars;
      }

      @Override
      public Fi getBackupFolder(){
        return Sgl.dataDirectory.child("global_backups");
      }

      @Override
      public Fi getBackupSettingsFile(){
        return globalVarsBackup;
      }

      @Override
      public synchronized void load(){
        try{
          loadValues();
        }catch(Throwable error){
          Log.err("Error in load: " + Strings.getStackTrace(error));
          if(errorHandler != null){
            if(!hasErrored) errorHandler.get(error);
          }else{
            throw error;
          }
          hasErrored = true;
        }
        loaded = true;
      }

      @Override
      public synchronized void forceSave(){
        if(!loaded) return;
        try{
          saveValues();
        }catch(Throwable error){
          Log.err("Error in forceSave to " + getSettingsFile() + ":\n" + Strings.getStackTrace(error));
          if(errorHandler != null){
            if(!hasErrored) errorHandler.get(error);
          }else{
            throw error;
          }
          hasErrored = true;
        }
        modified = false;
      }
    };

    globals.load();

    interopAPI.init();

    if(!Core.app.isHeadless()){
      generatePostAtlas();

      //设置屏幕采样器
      ScreenSampler.setup();
      //载入着色器
      SglShaders.load();
      //载入数学着色器
      MathRenderer.load();
      //加载绘制资源
      SglDrawConst.load();
      //载入风格
      SglStyles.load();

      ui = new SglUI();
      ui.init();

      configNotificationIO();
    }

    Events.on(EventType.ClientLoadEvent.class, e -> interopAPI.updateModels());
  }

  private static boolean saving;

  private static void configNotificationIO(){
    if(notificationHistory.exists() || notificationHistoryBackup.exists()){
      try(Reads reads = notificationHistory.reads()){
        ui.notificationFrag.loadHistory(reads);
      }catch(RuntimeException ignored){
        if(notificationHistory.exists() && notificationHistoryBackup.exists())
          Log.err("[Singularity] history notification load failed, trying load backup");

        try(Reads reads = notificationHistoryBackup.reads()){
          ui.notificationFrag.loadHistory(reads);
        }catch(RuntimeException e){
          Log.err("[Singularity] history notification load failed!", e);
        }
      }
    }
  }

  private static void generatePostAtlas(){
    Log.info("[Singularity] load post generated atlas");
    Vars.content.each(c -> {
      if(c instanceof PostAtlasGenerator gen){
        gen.postLoad();
      }
    });
  }
}
