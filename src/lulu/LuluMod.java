package lulu;

import lulu.content.*;
import lulu.content.extensions.*;
import lulu.entities.*;
import lulu.graphics.*;
import lulu.io.*;
import lulu.lib.seam.*;
import lulu.lib.singularity.graphic.*;
import lulu.type.neoplasm.*;
import lulu.type.resourceStacks.*;
import lulu.ui.*;
import lulu.util.uncUtil.handler.*;
import lulu.world.*;
import arc.*;
import arc.graphics.*;
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

  @Override
  public void init(){
    if(!Core.settings.getBool("linear")){
      Texture.TextureFilter filter = Texture.TextureFilter.nearest;

      Core.atlas.getTextures().each(texture -> texture.setFilter(filter, filter));
    }

    oIndexer = new OvulamIndexer();
    graphics = new OvulamGraphics();
    process = new OvulamPhysicsProcess();
    Vars.asyncCore.processes.set(0, process);
    neoplasmRules = new NeoplasmRules();

    tantrosDialog = new LaplacesTantrosDialog();

    seam.init();

    OvulamBlockFlags.init();
    //OvulamShaders.init();
    initResources();
    PrefrontalCortex.load();

    //ScreenSampler.setup();
    //SglShaders.load();
    MathRenderer.load();
    OvulamMathRenderers.load();
    Shapes.load();
    Momentum.init();

    Vars.world = new OvulamWorld();

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
}
