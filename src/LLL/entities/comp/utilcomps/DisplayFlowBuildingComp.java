package LLL.entities.comp.utilcomps;

import arc.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;

@Annotations.EntityComponent
abstract class DisplayFlowBuildingComp implements Buildingc{
  @Annotations.Import
  Block block;
  @Annotations.Import
  Tile tile;
  @Annotations.Import
  Team team;
  @Annotations.Import
  LiquidModule liquids;
  @Annotations.Import
  String lastAccessed;

  @Annotations.Replace
  @Override
  public void display(Table table){
    Building self = self();

    //display the block stuff
    //TODO duplicated code?
    table.table(t -> {
      t.left();
      t.add(new Image(block.getDisplayIcon(tile))).scaling(Scaling.fit).size(8 * 4);
      t.labelWrap(block.getDisplayName(tile)).left().width(190f).padLeft(5);
    }).growX().left();

    table.row();

    //only display everything else if the team is the same
    if(team == player.team()){
      table.table(bars -> {
        bars.defaults().growX().height(18f).pad(4);

        self.displayBars(bars);
      }).growX();
      table.row();
      table.table(self::displayConsumption).growX();

      if(block.displayFlow){
        String ps = " " + StatUnit.perSecond.localized();

        var flowItems = self.flowItems();

        if(flowItems != null){
          table.row();
          table.left();
          table.table(l -> {
            Bits current = new Bits();

            Runnable rebuild = () -> {
              l.clearChildren();
              l.left();
              for(Item item : content.items()){
                if(flowItems.hasFlowItem(item)){
                  l.image(item.uiIcon).scaling(Scaling.fit).padRight(3f);
                  l.label(() -> flowItems.getFlowRate(item) < 0 ? "..." : Strings.fixed(flowItems.getFlowRate(item), 1) + ps).color(Color.lightGray);
                  l.row();
                }
              }
            };

            rebuild.run();
            l.update(() -> {
              for(Item item : content.items()){
                if(flowItems.hasFlowItem(item) && !current.get(item.id)){
                  current.set(item.id);
                  rebuild.run();
                }
              }
            });
          }).left();
        }

        if(liquids != null){
          table.row();
          table.left();
          table.table(l -> {
            Bits current = new Bits();

            Runnable rebuild = () -> {
              l.clearChildren();
              l.left();
              for(var liquid : content.liquids()){
                if(liquids.hasFlowLiquid(liquid)){
                  l.image(liquid.uiIcon).scaling(Scaling.fit).size(32f).padRight(3f);
                  l.label(() -> liquids.getFlowRate(liquid) < 0 ? "..." : Strings.fixed(liquids.getFlowRate(liquid), 1) + ps).color(Color.lightGray);
                  l.row();
                }
              }
            };

            rebuild.run();
            l.update(() -> {
              for(var liquid : content.liquids()){
                if(liquids.hasFlowLiquid(liquid) && !current.get(liquid.id)){
                  current.set(liquid.id);
                  rebuild.run();
                }
              }
            });
          }).left();
        }
      }

      if(net.active() && lastAccessed != null){
        table.row();
        table.add(Core.bundle.format("lastaccessed", lastAccessed)).growX().wrap().left();
      }

      table.marginBottom(-5);
    }
  }
}
