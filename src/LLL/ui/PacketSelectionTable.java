package LLL.ui;

import LLL.content.*;
import LLL.ctype.packet.*;
import LLL.type.resourceStacks.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class PacketSelectionTable{
  private int currentSelection = 0, prevSelection = 0;

  public void build(Table parents, PacketType[] packetType, Seq<ResourceStack<?>> selectedResources){
    parents.table(Styles.black5, all -> {
      all.table(visibleSetter -> {
        visibleSetter.button(Icon.menu, Styles.clearNoneTogglei, () -> currentSelection = 0).checked(ib -> currentSelection == 0).height(60).growX();
        visibleSetter.button(Icon.filter, Styles.clearNoneTogglei, () -> currentSelection = 1).checked(ib -> currentSelection == 1).disabled(ib -> packetType[0] == null).height(60).growX();
      }).growX().row();

      Table packetSelectionTable = new Table(ps -> {
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);
        ps.defaults().size(40);

        int i = 0;

        for(PacketType type : OvulamPacketTypes.packetTypes){
          ImageButton button = ps.button(Tex.whiteui, Styles.clearNoneTogglei, 40f, () -> {
            currentSelection = 1;
            selectedResources.clear();
          }).checked(ib -> packetType[0] == type).tooltip(type.name).group(group).get();
          button.changed(() -> packetType[0] = button.isChecked() ? type : null);
          button.getStyle().imageUp = new TextureRegionDrawable(type.region);

          if(i++ % 8 == (8 - 1)){
            ps.row();
          }
        }
      });

      Table objectTable = new Table();
      Table objectSelectionTable = new Table();
      Table objectSetterTable = new Table();
      objectSelectionTable.defaults().size(40);

      Runnable rebuildSetter = () -> {
        objectSetterTable.clearChildren();
        if(selectedResources.isEmpty()) return;

        float sum = selectedResources.sumf(r -> r.amount);
        for(ResourceStack<?> stack : selectedResources){
          float amount = stack.amount;
          TextField field = new TextField(amount + "");

          objectSetterTable.image(stack.getIcon()).size(32f);
          Slider slider = new Slider(1, packetType[0].capacity - sum + stack.amount, 1, false);
          slider.setValue(amount);
          slider.moved(f -> {
            stack.amount = f;
            field.setText(f + "");
          });

          field.changed(() -> {
            if(!field.getText().isEmpty()){
              stack.amount = Float.parseFloat(field.getText());
              slider.setValue(stack.amount);
            }

            field.setCursorPosition(field.getText().length() - 2);
          });

          objectSetterTable.add(slider).growX();
          objectSetterTable.add(field).width(80);
          objectSetterTable.row();
        }
      };

      Runnable rebuildObjects = () -> {
        objectSelectionTable.clearChildren();
        if(packetType[0] == null) return;

        Seq<?> seq = ResourceStackManager.resourceInstances.get(packetType[0].resourceClass);
        Class<? extends ResourceStack<?>> stackClass = ResourceStackManager.classMap.get(packetType[0].resourceClass);

        if(seq == null) return;

        int j = 0;
        for(Object o : seq){
          //todo .tooltip(name)
          ImageButton button = objectSelectionTable.button(Tex.whiteui, Styles.clearNoneTogglei, 40f, () -> {
            rebuildSetter.run();
          }).checked(ib -> selectedResources.contains(r -> r.item == o)).get();
          button.changed(() -> {
            if(selectedResources.contains(r -> r.item == o)){
              selectedResources.remove(r -> r.item == o);
            }else if(selectedResources.sumf(r -> r.amount) + 1 <= packetType[0].capacity){
              selectedResources.add(ResourceStackManager.getResourceInstanceByClass(o, packetType[0].resourceClass, 1f));
            }
          });

          ResourceStack<?> stack = ResourceStackManager.resourceStackInstances.find(r -> r.getClass() == stackClass);
          TextureRegion icon = stack.getIcon();

          button.getStyle().imageUp = new TextureRegionDrawable(icon);

          if(j++ % 8 == (8 - 1)){
            objectSelectionTable.row();
          }
        }
      };

      rebuildObjects.run();
      rebuildSetter.run();

      objectTable.add(objectSelectionTable);
      objectTable.row();
      objectTable.add(objectSetterTable).marginBottom(10f).growX();

      Stack selections = new Stack();

      selections.update(() -> {
        if(prevSelection != currentSelection){
          selections.clearChildren();
          switch(currentSelection){
            case 0:{
              selections.add(packetSelectionTable);
              break;
            }
            case 1:{
              selections.add(objectTable);
              rebuildObjects.run();
              rebuildSetter.run();
              break;
            }
          }
          prevSelection = currentSelection;
        }
      });

      selections.add(currentSelection == 0 ? packetSelectionTable : objectTable);
      all.add(selections);
    });
  }
}