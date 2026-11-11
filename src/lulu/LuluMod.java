package lulu;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.util.*;
import arc.util.io.*;
import lulu.content.*;
import lulu.ctype.*;
import lulu.entities.*;
import lulu.graphics.draw.*;
import lulu.graphics.renderer.*;
import lulu.graphics.shader.*;
import lulu.io.*;
import lulu.lib.seam.*;
import lulu.research.*;
import lulu.type.neoplasm.*;
import lulu.type.resourceStacks.*;
import lulu.ui.*;
import lulu.util.uncUtil.handler.*;
import lulu.world.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.mod.*;

public class LuluMod extends Mod{
  public static String modName = "lulu-";
  public static OvulamIndexer oIndexer;
  public static OvulamPhysicsProcess process;
  public static OvulamGraphics graphics;
  public static NeoplasmRules neoplasmRules;
  public static LaplacesTantrosDialog tantrosDialog;
  public static Seam seam;
  public static CategoryHandler categories;
  public static StaticMeshRenderer staticMeshRenderer;
  public static Settings globals;
  public static ResearchManager researches;
  public static SglUI ui;

  @Override
  public void init(){
    if(!Core.settings.getBool("linear")){
      Texture.TextureFilter filter = Texture.TextureFilter.nearest;

      Core.atlas.getTextures().each(texture -> texture.setFilter(filter, filter));
    }

    ScreenSampler.setup();
    SglShaders.load();
    MathRenderer.load();
    SglDrawConst.load();
    SglStyles.load();
    ui = new SglUI();
    ui.init();
    loadNotificationHistory();

    oIndexer = new OvulamIndexer();
    graphics = new OvulamGraphics();
    process = new OvulamPhysicsProcess();
    Vars.asyncCore.processes.set(0, process);
    neoplasmRules = new NeoplasmRules();

    tantrosDialog = new LaplacesTantrosDialog();

    seam.init();

    OvulamBlockFlags.init();
    OvulamShaders.init();
    initResources();
    PrefrontalCortex.load();

    OvulamMathRenderers.load();
    Shapes.load();
    Momentum.init();

    Vars.world = new OvulamWorld();
    staticMeshRenderer = new StaticMeshRenderer();

    if(Vars.mobile){
    }else{
      Vars.control.input = new OvulamDesktopControl();
    }

//        new BaseDialog("Lulu-and-Laplace-Tantros"){{
//            cont.add("""
//                    警告：此模组仍在开发阶段，[red]存档相关功能尚未完善[]。
//                    [yellow]请勿在需要保存的重要地图中使用本模组[]，
//                    以免造成[red]存档损坏。[]
//
//                    模组群: 1091216674""").padBottom(10f);
//            addCloseButton();
//        }}.show();

    globals = new Settings(){
      {
        setAutosave(true);
        setDataDirectory(ModFiles.dataDirectory);
      }

      @Override
      public Fi getSettingsFile(){
        return ModFiles.globalVars;
      }

      @Override
      public Fi getBackupFolder(){
        return ModFiles.dataDirectory.child("global_backups");
      }

      @Override
      public Fi getBackupSettingsFile(){
        return ModFiles.globalVarsBackup;
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

    researches = new ResearchManager();
    OvulamResearchs.init();

    OvulamSaveChunks.addSaveChunks();
  }

  @Override
  public void loadContent(){
    categories = new CategoryHandler();
    registerResources();
    OvulamContents.load();
//        new RunnableBlock("daw"){{
//            runnable = () -> {
//                Vars.fogControl.resetFog();
//                Vars.state.rules.staticFog = true;
//                Vars.ui.editor.save();
//            };
//        }};
    seam = new Seam();
    seam.loadContent();
  }

  public static boolean isMDTX(){
    return Version.combined().contains("MindustryX");
  }

  public static void registerResources(){
    for(ResourceStack<?> instance : ResourceStackManager.resourceStackInstances){
      instance.register();
    }
  }

  public static void initResources(){
    for(ResourceStack<?> instance : ResourceStackManager.resourceStackInstances){
      instance.init();
    }
  }

  private static void loadNotificationHistory(){
    if(ModFiles.notificationHistory.exists() || ModFiles.notificationHistoryBackup.exists()){
      try(Reads reads = ModFiles.notificationHistory.reads()){
        ui.notificationFrag.loadHistory(reads);
      }catch(RuntimeException ignored){
        if(ModFiles.notificationHistory.exists() && ModFiles.notificationHistoryBackup.exists())
          Log.err("[Lulu] history notification load failed, trying load backup");

        try(Reads reads = ModFiles.notificationHistoryBackup.reads()){
          ui.notificationFrag.loadHistory(reads);
        }catch(RuntimeException e){
          Log.err("[Lulu] history notification load failed!", e);
        }
      }
    }
  }
}
