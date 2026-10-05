package LLL;

import LLL.content.*;
import LLL.content.extensions.*;
import LLL.entities.*;
import LLL.graphics.*;
import LLL.io.*;
import LLL.lib.seam.*;
import LLL.lib.singularity.graphic.*;
import LLL.type.neoplasm.*;
import LLL.type.resourceStacks.*;
import LLL.ui.*;
import LLL.world.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.scene.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.mod.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class LuluMod extends Mod{
  public static String modName = "lulu-";
  public static OvulamIndexer oIndexer;
  public static OvulamPhysicsProcess process;
  public static OvulamGraphics graphics;
  public static NeoplasmRules neoplasmRules;
  public static LaplacesTantrosDialog tantrosDialog;
  public static Seam seam;

  @Override
  public void init(){
    if(!Core.settings.getBool("linear")){
      Texture.TextureFilter filter = Texture.TextureFilter.nearest;

      Core.atlas.getTextures().each(texture -> texture.setFilter(filter, filter));
    }

//    {
//      FrameBuffer buffer = new FrameBuffer();
//      Events.run(EventType.Trigger.drawOver, () -> {
//        Draw.z(Layer.fogOfWar + 1);
//        Draw.blend(Blending.additive);
//        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
//        buffer.begin(Color.clear);
//        Fill.circle(2500,2500,4);
//        buffer.end();
//
//        Draw.blend();
//        OvulamShaders.test.apply();
//        Draw.blit(buffer, OvulamShaders.test);
//      });
//    }

    Events.on(EventType.ClientLoadEvent.class, e -> {
      Vars.ui.load.runLoadSave(control.saves.getSaveSlots().first());

      Vars.ui.hudGroup.addChild(new Element(){
        public FrameBuffer buffer = new FrameBuffer();

        @Override
        public void draw(){
          int w = Core.graphics.getWidth();
          int h = Core.graphics.getHeight();

          buffer.resize(w / 4, h);
          buffer.begin(Color.clear);
          Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w, h / 2f, 9 * 32 * 2, 9 * 32 * 2);
          Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w / 2f, h, 9 * 32 * 2, 9 * 32 * 2);
          Draw.rect(Blocks.tetrativeReconstructor.fullIcon, w / 2f, h / 2f, 9 * 32 * 2, 9 * 32 * 2);
          buffer.end();

          Tmp.tr1.set(buffer.getTexture());
          Draw.scl(4);
          Draw.rect(Tmp.tr1, w / 2f, h / 2f);
          Draw.scl();
          Tmp.r1.setCentered(w / 2f, h / 2f, Tmp.tr1.width, Tmp.tr1.height);
          Lines.rect(Tmp.r1);


//          OvulamShaders.test.apply();
//          Draw.blit(buffer, OvulamShaders.test);
//-----------------------------
//          buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
//
//          ScreenSampler.toBuffer(buffer);
//          Tmp.tr1.set(buffer.getTexture());
//
//          float w = Core.graphics.getWidth();
//          float h = Core.graphics.getHeight();
//
//          Draw.color(Color.black);
//          Fill.circle(w / 2f, h / 2f, w);
//          Draw.color();
//
//          Draw.rect(Tmp.tr1, w / 2f, h /2f, w, h);
          //-----------------------------
//          buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
//
//          ScreenSampler.toBuffer(buffer);
//
//          OvulamShaders.test.apply();
//          Draw.blit(buffer, OvulamShaders.test);
          //-----------------------------

        }
      });

//      Log.info(sectorSeq.size);
//      for(Sector sector: sectorSeq){
//        Log.info(sector.id + " " + sector.rect.center.angle(start));
//      }
//            Planets.serpulo.sectors.each(s -> {
//                s.info.waves = false;
//                s.info.attack = false;
//            });
      //PlanetDialog.debugSelect = true;
      //Vars.control.playNewSector(null, LaplacesTantros.tantros.sectors.find(s -> s.id == 207), new WorldReloader());
//      ItemModule item = Vars.state.teams.get(Vars.player.team()).core().items;
//      Vars.content.items().each((awdawd) -> item.set(awdawd, 1000));
//      Vars.state.rules.infiniteResources = true;
      //tantrosDialog.show();
    });

    oIndexer = new OvulamIndexer();
    graphics = new OvulamGraphics();
    process = new OvulamPhysicsProcess();
    Vars.asyncCore.processes.set(0, process);
    neoplasmRules = new NeoplasmRules();

    tantrosDialog = new LaplacesTantrosDialog();

    seam.init();

    OvulamBlockFlags.init();
    OvulamShaders.init();
    OvulamPacketTypes.initAndLoad();
    initResources();
    PrefrontalCortex.load();

    //ScreenSampler.setup();
    SglShaders.load();
    MathRenderer.load();
    OvulamMathRenderers.load();
    Shapes.load();
    Momentum.init();

    Vars.world = new OvulamWorld();

    if(Vars.mobile){
    }else{
      control.input = new OvulamDesktopControl();
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

  public static Planet biomePlanet;

  @Override
  public void loadContent(){

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
