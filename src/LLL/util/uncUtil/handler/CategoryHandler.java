package LLL.util.uncUtil.handler;

import arc.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.fragments.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;

import java.util.*;

/**
 * 用于增加右下角方块选择栏分类条目的工具
 *
 * @author EBwilson
 * @since 1.0
 */
public class CategoryHandler{
  protected final ObjectMap<Category, UncCategory> newCats = new ObjectMap<>();
  protected boolean hasNew = false;

  protected static final KeyBind empBind;

  static{
    empBind = KeyBind.add("unBind", KeyCode.unset, "universecore");
  }

  public void handleBlockFrag(){
    if(!hasNew) return;
    Table catTable = Reflection.accessField(PlacementFragment.class, Table.class, "blockCatTable").get(Vars.ui.hudfrag.blockfrag);

    // frame.update(() -> {});
    Table blockSelect = (Table)catTable.getChildren().get(0);
    Table categories = (Table)catTable.getChildren().get(1);

    Cell<?> pane = blockSelect.getCells().get(0);
    pane.height(240f);

    Seq<Element> catButtons = new Seq<>(categories.getChildren());
    catButtons.remove(0);

    //for(UncCategory cat: newCats.values()){
    //  ImageButton button = ((ImageButton)catButtons.find(e -> ("category-" + cat.cat.name()).equals(e.name)));
    //  if(button == null) continue;
    //  button.getStyle().imageUp = new TextureRegionDrawable(Core.atlas.find(cat.icon));
    //  button.resizeImage(32);
    //}

    categories.clearChildren();
    categories.pane(t -> {
      t.defaults().size(50);
      int count = 0;
      for(Element element : catButtons){
        if(count++ % 2 == 0 && count != 0) t.row();
        t.add(element);
      }

      if(catButtons.size % 2 != 0) t.image(Styles.black6);
    }).size(catButtons.size > 12 ? 125 : 100, 300);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   *
   * @param name     类别的内部名称
   * @param ordinal  这个类别在选择栏的显示位置序数
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, int ordinal, String iconName){
    return add(name, ordinal, null, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   *
   * @param name     类别的内部名称
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, String iconName){
    return add(name, null, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   *
   * @param name     类别的内部名称
   * @param bind     这个类别绑定到的目标键位
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, KeyBind bind, String iconName){
    return add(name, Category.values().length, bind, iconName);
  }

  /**
   * 新增一个建筑类型到列表中，这会在游戏中的方块选择栏呈现
   *
   * @param name     类别的内部名称
   * @param ordinal  这个类别在选择栏的显示位置序数
   * @param bind     这个类别绑定到的目标键位
   * @param iconName 这个类别的图标的资源文件名称
   */
  public Category add(String name, int ordinal, KeyBind bind, String iconName){
    hasNew = true;
    UncCategory category = new UncCategory(name, ordinal, bind, iconName);
    newCats.put(category.cat, category);

    return category.cat;
  }

  public void init(){
    KeyBind[] arr = Reflection.accessField(PlacementFragment.class, KeyBind[].class, "blockSelect").get(Vars.ui.hudfrag.blockfrag);
    if(arr.length < Category.all.length){
      arr = Arrays.copyOf(arr, Category.all.length);
      for(int i = 0; i < arr.length; i++){
        UncCategory cat = newCats.get(Category.all[i]);
        if(arr[i] == null){
          arr[i] = cat != null ? cat.bind : empBind;
        }
      }
    }

    Reflection.accessField(PlacementFragment.class, KeyBind[].class, "blockSelect").set(Vars.ui.hudfrag.blockfrag, arr);

    for(ObjectMap.Entry<Category, UncCategory> cat : newCats){
      TextureRegion r = Core.atlas.find(cat.value.icon);
      Core.atlas.addRegion(cat.key.name(), r);
      Icon.icons.put(cat.key.name(), new TextureRegionDrawable(r){
        @Override
        public float imageSize(){
          return 32f;
        }
      });
    }
  }

  protected static class UncCategory{
    private static final EnumAccessor0<Category> handler = Enums.accessEnum(Category.class);

    final Category cat;
    @Nullable
    final KeyBind bind;
    int ordinal;
    final String icon;

    UncCategory(Category cat, KeyBind bind, String icon){
      this.cat = cat;
      this.icon = icon;
      ordinal = cat.ordinal();
      this.bind = bind;
    }

    UncCategory(String name, int ordinal, KeyBind bind, String icon){
      this(handler.newEnumInstance(name, ordinal, false), bind, icon);
      Reflection.accessStaticField(Category.class, Category[].class, "all").set(Category.values());
      this.ordinal = ordinal;
    }
  }
}
