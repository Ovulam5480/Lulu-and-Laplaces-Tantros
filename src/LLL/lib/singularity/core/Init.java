package LLL.lib.singularity.core;

import LLL.lib.singularity.world.meta.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.liquid.*;

import static mindustry.Vars.*;

/**
 * 改动游戏原内容重初始化，用于对游戏已定义的实例进行操作
 */
public class Init{

  /**
   * 内容重载，对已加载的内容做出变更(或者覆盖)
   */
  public static void reloadContent(){
    //设置方块及地板属性
    Blocks.stone.attributes.set(SglAttribute.bitumen, 0.12f);

    for(Block target : Vars.content.blocks()){
      //为液体装卸器保证不从(常规)导管中提取液体
      if(target instanceof Conduit) target.unloadable = false;

      //禁用所有超速器
      if(target instanceof OverdriveProjector over){
        over.placeablePlayer = false;
        over.update = false;
        over.destructible = true;
      }
    }

    //超速禁用提示
    Events.run(EventType.Trigger.draw, () -> {
      if(Vars.ui.hudfrag.blockfrag.hover() instanceof OverdriveProjector.OverdriveBuild b){
        GlyphLayout layout = GlyphLayout.obtain();
        layout.setText(Fonts.outline, Core.bundle.get("infos.blockDisabled"));

        float w = layout.width * 0.185f;
        float h = layout.height * 0.185f;

        layout.free();
        Draw.color(Color.darkGray, 0.6f);
        Fill.quad(
          b.x - w / 2 - 2, b.y + b.block.size * tilesize / 2f + h + 2,
          b.x - w / 2 - 2, b.y + b.block.size * tilesize / 2f - 2,
          b.x + w / 2 + 2, b.y + b.block.size * tilesize / 2f - 2,
          b.x + w / 2 + 2, b.y + b.block.size * tilesize / 2f + h + 2
        );

        Fonts.outline.draw(Core.bundle.get("infos.blockDisabled"), b.x, b.y + b.block.size * tilesize / 2f + h, Color.white, 0.185f, false, Align.center);
      }
    });
  }
}
