package lulu.world.blocks;

import arc.Core;
import arc.scene.ui.layout.*;
import lulu.*;
import lulu.content.*;
import lulu.lib.singularity.game.researchs.*;
import lulu.lib.singularity.world.blocks.research.*;
import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import lulu.entities.gen.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import universecore.annotations.*;

@Annotations.ImplEntries
public class ResearchPlatform extends Block implements ProducerBlockComp<ProducerBuildBuilding>, ResourceBlockModule{
  public ResearchPlatform(String name){
    super(name);
    resourceCapacity(400f);
    configurable = true;
    hasItems = true;
    hasLiquids = true;
    hasPower = true;
    itemCapacity = 400;
    liquidCapacity = 400;

    requirements(Category.effect, ItemStack.with(Items.copper, 25));

    newConsume(c -> {
      c.time(60f);
      c.add(new ConsumeResearch<>());
    });

    newProduce(p -> p.add(new ProduceValid<>()));

    /*选中研究以字符串形式作为建筑配置，随存档持久化*/
    config(String.class, (e, s) -> {
      ResearchPlatformBuild b = (ResearchPlatformBuild)e;
      b.selected = s == null ? null : LuluMod.researches.current().getResearch(s);
    });
  }

  @Annotations.ImplEntries
  public class ResearchPlatformBuild extends ProducerBuildBuilding implements ResourceBuildComp{
    /** 当前选中的待研究项目，由配置菜单写入，随存档恢复。 */
    public ResearchProject selected;

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);
      createResourceBuild();
      return this;
    }

    /** 合成触发：资源已被消耗，此时推进/完成选中的研究。 */
    @Override
    public void craftTrigger(){
      super.craftTrigger();

      if(selected != null && !selected.isCompleted()){
        selected.researchProcess(selected.getRealRequireTechs());
      }
    }

    @Override
    public Object config(){
      return selected == null ? null : selected.name;
    }

    @Override
    public void buildConfiguration(Table table){
      table.table(Tex.buttonTrans, t -> {
        t.add("选择研究").padLeft(5).padTop(5).padBottom(5);
        t.row();
        t.pane(buttons -> {
          for(ResearchProject project : LuluMod.researches.currentListResearches()){
            if(project.isCompleted()) continue;
            if(!project.dependenciesCompleted()) continue;
            if(!project.isRevealed()) continue;

            buttons.button(project.localizedName, Styles.defaultt, () -> configure(project.name))
              .checked(tb -> project == selected).growX().left().margin(5).row();
          }
        }).fill().maxHeight(280);
      });
      table.row();

      buildConfigurationResourceBuild(table);
    }
  }
}
