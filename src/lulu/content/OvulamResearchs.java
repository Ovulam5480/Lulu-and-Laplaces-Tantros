package lulu.content;

import lulu.*;
import lulu.research.*;
import lulu.type.resourceStacks.*;
import mindustry.content.*;

public class OvulamResearchs extends ResearchManager.ResearchSDL{
  public static void init(){
    makeContext("anchor", () -> {
      research("111", 100, () -> {
        contents(Blocks.duo, Blocks.arc);
        //hideTechs();
        // 示例：完成本研究需消耗 物品/液体/电力/载荷 四种资源（仅用于测试消耗链路）
        cost(
          new ItemResourceStack().set(Items.copper, 100f),
          new LiquidResourceStack().set(Liquids.water, 100f),
          new PowerChannelStack().set(OvulamResource.power, 100f),
          new PayloadResourceStack().set(Blocks.duo, 1f)
        );
      });
      research("222", 100, () -> {
        contents(Blocks.duo, Blocks.arc);
        //hideTechs();
        dependencies("111");
      });
      research("333", 100, () -> {
        contents(Blocks.duo, Blocks.arc);
        hideTechs();
        dependencies("111");
      });

      reveal(new RevealGroup("111"), () -> {
        research("444", 100, () -> {
          contents(Blocks.duo, Blocks.arc);
          hideTechs();
          dependencies("111");
        });
        research("555", 100, () -> {
          contents(Blocks.duo, Blocks.arc);
          //hideTechs();
          dependencies("111");
        });
      });
      // 示例（取消注释并按你的内容替换）：
      // research("my_research", 100, () -> {
      //   contents(someBlock);
      //   dependencies("other_research");
      //   hideTechs();
      // });
    });

    // ===== BOSS 战区块：按区域类型编号，例如 "boss:1"、"boss:2" =====
    // 注意：BOSS 区块的研究请设置 sectorScoped = true，其解锁内容仅在该 BOSS 区块（对应 zoneKey 的组）内可用。
    // makeContext("boss:1", () -> {
    //   research("boss_research", 200, () -> {
    //     contents(bossOnlyBuilding);
    //     hideTechs();
    //   });
    //   // 标记该 BOSS 项目为区块限定（makeContext 结束后）：
    //   // LuluMod.researches.getGroup("boss:1").getResearch("boss_research").sectorScoped = true;
    // });

    LuluMod.researches.init();
  }
}
