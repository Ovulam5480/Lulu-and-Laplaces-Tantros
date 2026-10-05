package LLL.world.blocks.module;

import LLL.type.resourceStacks.*;
import LLL.world.modules.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.modules.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;

public interface ResourceBuildModule extends BuildCompBase{
  default ResourceBlockModule getResourceBlock(){
    return (ResourceBlockModule)getBlock();
  }

  @Annotations.BindField("resources")
  default ResourceModule resources(){
    return null;
  }

  @Annotations.BindField("resources")
  default void setResources(ResourceModule resourceModule){
  }

  @Annotations.BindField("tableId")
  default int tableId(){
    return 0;
  }

  @Annotations.BindField("tableId")
  default void setTableId(int id){
  }

  @Annotations.BindField("resetTable")
  default Runnable resetTable(){
    return null;
  }

  @Annotations.BindField("resetTable")
  default void setResetTable(Runnable runnable){
  }

  default void createResourceBuild(){
    Building building = getBuilding();

    setResources(new ResourceModule(building));
  }

  int row = 6;

  /**
   * 液体运输的逻辑判断被写死在{@link Building#dumpLiquid(Liquid, float, int)}中
   */

//  default boolean acceptLiquidResourceBuild(Building source, Liquid liquid){
//    return getBlock().hasLiquids && getBlock().consumesLiquid(liquid);
//  }
//  default void handleLiquidResourceBuild(Building source, Liquid liquid, float amount) {
//    this.liquids.add(liquid, amount);
//  }
  @Annotations.MethodEntry(entryMethod = "getLiquidDestination", paramTypes = {"mindustry.gen.Building -> from", "mindustry.type.Liquid -> liquid"}, override = true)
  default Building getLiquidDestinationResourceBuild(Building from, Liquid liquid){
    getBlock().liquidCapacity = getMaxAccepted(liquid) + liquids().get(liquid);
    return getBuilding();
  }

  @Annotations.MethodEntry(entryMethod = "acceptItem", paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Item -> item"}, override = true)
  default boolean acceptItemResourceBuild(Building source, Item item){
    return getMaxAccepted(item) >= 1;
  }

  @Annotations.MethodEntry(entryMethod = "acceptStack", paramTypes = {"mindustry.type.Item -> item", "int -> amount", "mindustry.gen.Teamc -> source"}, override = true)
  default int acceptStackResourceBuild(Item item, int amount, Teamc source){
    return Math.min(Mathf.ceil(getMaxAccepted(item)), amount);
  }

  @Annotations.MethodEntry(entryMethod = "acceptLiquid", paramTypes = {"mindustry.gen.Building -> source", "mindustry.type.Liquid -> liquid"}, override = true)
  default boolean acceptLiquidResourceBuild(Building source, Liquid liquid){
    return true;
  }

  @Annotations.MethodEntry(entryMethod = "acceptPayload", paramTypes = {"mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"}, override = true)
  default boolean acceptPayloadResourceBuild(Building source, Payload payload){
    boolean stackable;
    if(payload instanceof UnitPayload u){
      stackable = checkPayloadStackable(u.unit);
    }else{
      stackable = checkPayloadStackable(((BuildPayload)payload).build);
    }

    return stackable && getMaxAccepted(payload.content()) >= 1;
  }

  default boolean acceptUnitPayload(Unit unit){
    return checkPayloadStackable(unit) && getMaxAccepted(unit.type) >= 1;
  }

  default boolean checkPayloadStackable(Teamc entity){
    if(entity instanceof Building b){
      return b.health == b.block.health
        && (b.items == null || b.items.total() == 0)
        && (b.liquids == null || b.liquids.sum((i, a) -> a) == 0)
        && (b.power == null || b.power.status == 0);
    }else if(entity instanceof Unit u){
      return u.health == u.type.health
        && u.stack.amount == 0
        && !(u instanceof Payloadc pc && !pc.payloads().isEmpty());
    }else{
      return false;
    }
  }

  default float getMaxAccepted(Object object){
    float had = resources().resourceStacks.get(object) == null ? 0 : resources().resourceStacks.get(object).amount;

    float remain;
    if(getResourceBlock().separateResource()){
      remain = getResourceBlock().resourceCapacity() - had;
    }else{
      remain = getResourceBlock().resourceCapacity() - resources().sum(this::getResourceOccupy);
    }

    return remain / getResourceOccupy(object, 1);
  }

  @Annotations.MethodEntry(entryMethod = "getMaximumAccepted", paramTypes = {"mindustry.type.Item -> item"})
  default int getMaximumAcceptedResourceBuild(Item item){
    return (int)getMaxAccepted(item);
  }

  default void handleResourceStack(Object object, float amount){
    resources().add(object, amount);
  }

  //todo takePayload
  @Annotations.MethodEntry(entryMethod = "handlePayload", paramTypes = {"mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"}, override = true)
  default void handlePayloadResourceBuild(Building source, Payload payload){
    resources().add(payload.content(), 1);
  }

  @Annotations.MethodEntry(entryMethod = "canControlSelect", paramTypes = {"mindustry.gen.Unit -> unit"}, override = true)
  default boolean canControlSelectResourceBuild(Unit unit){
    return !unit.spawnedByCore && unit.type.allowedInPayloads && acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build == this;
  }

  @Annotations.MethodEntry(entryMethod = "onControlSelect", paramTypes = {"mindustry.gen.Unit -> player"}, override = true)
  default void onControlSelectResourceBuild(Unit player){
    getBuilding().handleUnitPayload(player, p -> handlePayloadResourceBuild(null, p));
  }

  default float getResourceOccupy(ResourceStack<?> stack){
    return getResourceOccupyFunc(stack.item, stack.amount, stack.id());
  }

  default float getResourceOccupy(Object item, float amount){
    return getResourceOccupyFunc(item, amount, ResourceStackManager.getResourceInstanceID(item));
  }

  default float getResourceOccupyFunc(Object item, float amount, int id){
    return switch(id){
      case 0 -> amount;
      case 1 -> amount / 6;
      case 2 -> amount / 600;
      case 3 -> amount * getPayloadOccupy((UnlockableContent)item);
      default -> 99999999;
    };
  }

  default float getPayloadOccupy(UnlockableContent content){
    float health, hitSize;

    if(content instanceof Block b){
      health = b.health;
      hitSize = b.size;
    }else if(content instanceof UnitType u){
      health = u.health;
      hitSize = u.hitSize;
    }else{
      return 99999999;
    }

    return Mathf.sqrt(health * hitSize * hitSize);
  }

  //@Annotations.MethodEntry(entryMethod = "buildConfiguration", paramTypes = {"arc.scene.ui.layout.Table -> table"})
  default void buildConfigurationResourceBuild(Table table){
    Building building = getBuilding();
    ItemModule items = building.items;
    LiquidModule liquids = building.liquids;

    table.table(Styles.black3, t -> {
      t.table(resourceTable -> {
        Cons<Table> reset = list -> {
          list.clear();

          if(tableId() == 0){
            int[] i = {0};
            items.each((item, awdwadwa) -> {
              int itemId = item.id;
              addResourceImage(list, item.fullIcon, () -> items.get(itemId));
              if(++i[0] % row == 0) list.row();
            });
          }else if(tableId() == 1){
            int[] i = {0};
            liquids.each((liquid, dwadwad) -> {
              addResourceImage(list, liquid.fullIcon, () -> liquids.get(liquid));
              if(++i[0] % row == 0) list.row();
            });
          }else{
            Seq<Object> objects = resources().getResourceInstanceSeqID(tableId());

            int i = 0;
            for(Object object : objects){
              ResourceStack<?> stack = resources().resourceStacks.get(object);
              addResourceImage(list, stack.getIcon(), () -> stack.amount);

              if(++i % row == 0) list.row();
            }
          }
        };

        Table l = new Table(list -> {
          list.defaults().size(40).pad(4);
        });

        resourceTable.table(cat -> {
          cat.defaults().size(40);

          TextureRegion[] regions = {Items.copper.fullIcon, Liquids.water.fullIcon, Icon.power.getRegion(), UnitTypes.flare.fullIcon};

          for(int i = 0; i < regions.length; i++){
            int finalI = i;
            Cell<ImageButton> cell = cat.button(new TextureRegionDrawable(regions[i]){
              @Override
              public void draw(float x, float y, float width, float height){
                Draw.mixcol(Color.white.toFloatBits());
                Draw.rect(region, x + width / 2f, y + height / 2f, width, height);
                Draw.mixcol();
              }
            }, Styles.clearNoneTogglei, () -> {
              setTableId(finalI);
              reset.get(l);
            }).checked(ib -> tableId() == finalI);

            cell.get().getImageCell().size(32).scaling(Scaling.fit);
            cell.row();
          }

          cat.add().growY();
        }).growY().pad(4);

        resourceTable.image().color(Tmp.c1.set(Color.grays(0.8f)).a(0.8f)).growY().padTop(4).padBottom(4);
        resourceTable.add(l).pad(4);
        setResetTable(() -> reset.get(l));
      });
      resetTable().run();
    });
  }

  default void triggerResourceChanged(){
    if(resetTable() != null) resetTable().run();
  }

  default void addResourceImage(Table table, TextureRegion region, Floatp floatp){
    table.add(new Stack(){{
      add(new Image(region).setScaling(Scaling.fit));

      Table t = new Table().right().bottom();
      t.setWidth(32);
      t.setScale(0.7f);
      t.setTransform(true);
      t.setOrigin(Align.bottomRight);

      t.label(() -> UI.formatAmount((long)floatp.get())).style(Styles.outlineLabel);

      add(t);
    }}).size(32);
  }

  @Annotations.MethodEntry(entryMethod = "read", paramTypes = {"arc.util.io.Reads -> read", "byte -> revision"})
  default void readResourceBuild(Reads read, byte revision){
    resources().read(read);
  }

  @Annotations.MethodEntry(entryMethod = "write", paramTypes = {"arc.util.io.Writes -> write"})
  default void writeResourceBuild(Writes write){
    resources().write(write);
  }
}
