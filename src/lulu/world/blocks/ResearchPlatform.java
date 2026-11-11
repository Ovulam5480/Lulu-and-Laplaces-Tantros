package lulu.world.blocks;

import arc.scene.ui.layout.*;
import lulu.*;
import lulu.entities.gen.*;
import lulu.research.*;
import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
import lulu.world.recipe.consumers.*;
import lulu.world.recipe.producers.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class ResearchPlatform extends Block implements ProducerBlockComp<ProducerBuildBuilding>, ResourceBlockComp{
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

    config(String.class, (e, s) -> {
      ResearchPlatformBuild b = (ResearchPlatformBuild)e;
      b.selected = s == null ? null : LuluMod.researches.current().getResearch(s);
    });
  }

  @Annotations.ImplEntries
  public class ResearchPlatformBuild extends ProducerBuildBuilding implements ResourceBuildComp{
    public ResearchProject selected;

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);
      createResourceBuild();
      return this;
    }

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
