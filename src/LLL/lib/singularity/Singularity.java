package LLL.lib.singularity;

import LLL.lib.singularity.contents.*;
import LLL.lib.singularity.core.*;
import LLL.lib.singularity.type.*;
import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.mod.*;
import universecore.*;

import java.util.*;

import static mindustry.game.EventType.*;

//@RecipeEntryPoint(Recipes.class)
//@Annotations.ImportUNC(requireVersion = "2.1.0")
public class Singularity extends Mod{
  private static final ContentList[] modContents = new ContentList[]{
    new OtherContents(),//其他内容

    new SglItems(),//物品
    new SglLiquids(),//液体
    new NuclearBlocks(),//核能方块
    new LiquidBlocks(),//物流方块
    new ProductBlocks(),//采集方块
    new DistributeBlocks(),//物流运输方块
    new SglTurrets(),//炮台
    new SglUnits(),//单位相关内容（单位、工厂）
    new DefenceBlocks(),//防御方块
  };

  public boolean initialized = false;

  public Singularity(){
    //加载模组配置数据
    Sgl.config.load();
    Sgl.classes = UncCore.classes.newInstance(Singularity.class);

    Log.info(
      """
        [Singularity] Singularity mod is loading!
        Thanks for your play.
        
        Visit the GitHub project about this mod:
        >\040""" + Sgl.githubProject + " <"
    );

    if(Sgl.config.modReciprocalContent){
      Events.on(ContentInitEvent.class, e -> {
        Init.reloadContent();
      });
    }

    if(Sgl.config.debugMode) Events.on(WorldLoadEvent.class, e -> Vars.state.rules.infiniteResources = true);
  }

  @Override
  public void init(){
    //加载全局变量
    Sgl.init();

    initialized = true;

    Sgl.classes.finishGenerate();
    if(Sgl.config.loadInfo) Log.info("[Singularity] mod initialize finished");
  }

  @Override
  public void loadContent(){
    //加载属性类型
    SglAttribute.load();
    //加载方块类型
    SglCategory.load();
    //载入所有新内容类型
    SglContentType.load();

    for(ContentList list : Singularity.modContents){
      list.load();
    }

    if(Sgl.config.debugMode){

      for(ContentType type : ContentType.all){
        for(Content content : Vars.content.getBy(type)){
          if(content instanceof UnlockableContent uc){
            uc.alwaysUnlocked = true;
          }
        }
      }
    }

    if(Sgl.config.loadInfo) Log.info("[Singularity] mod content load finished");
  }

  public static TextureRegion getModAtlas(String name){
    return Core.atlas.find(Sgl.modName + "-" + name);
  }

  public static TextureRegion getModAtlas(String name, TextureRegion def){
    return Core.atlas.find(Sgl.modName + "-" + name, def);
  }

  public static <T extends Drawable> T getModDrawable(String name){
    return Core.atlas.getDrawable(Sgl.modName + "-" + name);
  }

  public static Fi getInternalFile(String path){
    return Sgl.modFile.child(path);
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