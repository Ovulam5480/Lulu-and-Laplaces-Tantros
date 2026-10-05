package LLL.lib.multiblock.extend;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public interface MultiBlockEntity{
  default Building getMultiBlockEntity(){
    return (Building)this;
  }

  @Annotations.BindField(value = "linkCreated", initialize = "false")
  default boolean linkCreated(){
    return false;
  }

  @Annotations.BindField(value = "linkCreated")
  default void setLinkCreated(boolean created){
  }

  @Annotations.BindField(value = "linkValid", initialize = "true")
  default boolean linkValid(){
    return true;
  }

  @Annotations.BindField(value = "linkValid")
  default void setLinkValid(boolean valid){
  }

  @Annotations.BindField(value = "linkEntities", initialize = "new arc.struct.Seq<>()")
  default Seq<Building> linkEntities(){
    return null;
  }

  @Annotations.BindField(value = "linkEntities")
  default void setLinkEntities(Seq<Building> entities){
  }

  @Annotations.BindField(value = "linkProximityMap", initialize = "new arc.struct.Seq<>()")
  default Seq<Building[]> linkProximityMap(){
    return null;
  }

  @Annotations.BindField(value = "linkProximityMap")
  default void setLinkProximityMap(Seq<Building[]> map){
  }

  @Annotations.BindField(value = "dumpIndex", initialize = "0")
  default int dumpIndex(){
    return 0;
  }

  @Annotations.BindField(value = "dumpIndex")
  default void setDumpIndex(int index){
  }

  @Annotations.BindField(value = "teamPos")
  default Tile teamPos(){
    return null;
  }

  @Annotations.BindField(value = "teamPos")
  default void setTeamPos(Tile pos){
  }

  @Annotations.BindField(value = "statusPos")
  default Tile statusPos(){
    return null;
  }

  @Annotations.BindField(value = "statusPos")
  default void setStatusPos(Tile pos){
  }

  @Annotations.MethodEntry(entryMethod = "created")
  default void initMultiBlockEntity(){
    setLinkProximityMap(new Seq<>());
  }

  @Annotations.MethodEntry(entryMethod = "updateTile")
  default void updateMultiBlockTile(){
    Building build = getMultiBlockEntity();

    if(build.isPayload()) return;

    if(!linkCreated()){
      createdMultiBlock();
    }

    if(!linkValid()){
      linkEntities().each(Building::kill);
      build.kill();
    }
  }

  @Annotations.MethodEntry(entryMethod = "dump", paramTypes = "mindustry.type.Item -> todump", override = true)
  default boolean dumpMultiBlock(Item todump){
    Building build = getMultiBlockEntity();
    Block block = build.block;

    if(!block.hasItems || build.items.total() == 0 || linkProximityMap().size == 0 || (todump != null && !build.items.has(todump)))
      return false;

    int dump = dumpIndex();
    for(int i = 0; i < linkProximityMap().size; i++){
      int idx = (i + dump) % linkProximityMap().size;
      Building[] pair = linkProximityMap().get(idx);
      Building target = pair[0];
      Building source = pair[1];

      if(todump == null){
        for(int ii = 0; ii < content.items().size; ii++){
          if(!build.items.has(ii)) continue;
          Item item = content.items().get(ii);
          if(target.acceptItem(source, item) && build.canDump(target, item)){
            target.handleItem(source, item);
            build.items.remove(item, 1);
            incrementDumpMultiBlock(linkProximityMap().size);
            return true;
          }
        }
      }else{
        if(target.acceptItem(source, todump) && build.canDump(target, todump)){
          target.handleItem(source, todump);
          build.items.remove(todump, 1);
          incrementDumpMultiBlock(linkProximityMap().size);
          return true;
        }
      }
      incrementDumpMultiBlock(linkProximityMap().size);
    }
    return false;
  }

  @Annotations.MethodEntry(entryMethod = "dumpLiquid", paramTypes = {"mindustry.type.Liquid -> liquid", "float -> scaling", "int -> outputDir"}, override = true)
  default void dumpLiquidMultiBlock(Liquid liquid, float scaling, int outputDir){
    Building build = getMultiBlockEntity();
    Block block = build.block;
    int dump = build.cdump;

    if(build.liquids.get(liquid) <= 0.0001f) return;
    if(!net.client() && state.isCampaign() && build.team == state.rules.defaultTeam) liquid.unlock();

    for(int i = 0; i < linkProximityMap().size; i++){
      incrementDumpMultiBlock(linkProximityMap().size);
      int idx = (i + dump) % linkProximityMap().size;
      Building[] pair = linkProximityMap().get(idx);
      Building target = pair[0];
      Building source = pair[1];

      if(outputDir != -1 && (outputDir + build.rotation) % 4 != build.relativeTo(target)) continue;
      target = target.getLiquidDestination(build, liquid);

      if(target != null && target.block.hasLiquids && build.canDumpLiquid(target, liquid) && target.liquids != null){
        float ofract = target.liquids.get(liquid) / target.block.liquidCapacity;
        float fract = build.liquids.get(liquid) / block.liquidCapacity;
        if(ofract < fract)
          build.transferLiquid(target, (fract - ofract) * block.liquidCapacity / scaling, liquid);
      }
    }
  }

  @Annotations.MethodEntry(entryMethod = "offload", paramTypes = "mindustry.type.Item -> item", override = true)
  default void offloadMultiBlock(Item item){
    Building build = getMultiBlockEntity();

    getMultiBlockEntity().produced(item, 1);

    int dump = dumpIndex();
    for(int i = 0; i < linkProximityMap().size; i++){
      incrementDumpMultiBlock(linkProximityMap().size);
      int idx = (i + dump) % linkProximityMap().size;
      Building[] pair = linkProximityMap().get(idx);
      Building target = pair[0];
      Building source = pair[1];

      if(target.acceptItem(source, item) && build.canDump(target, item)){
        target.handleItem(source, item);
        return;
      }
    }
    build.handleItem(build, item);
  }

  @Annotations.MethodEntry(entryMethod = "incrementDump", paramTypes = "int -> prox", override = true)
  default void incrementDumpMultiBlock(int prox){
    setDumpIndex(((dumpIndex() + 1) % prox));
  }

  @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
  default void updateMultiBlockProximity(){
    updateLinkProximity();
  }

  default void createdMultiBlock(){
    Building build = getMultiBlockEntity();
    Block block = build.block;
    Tile tile = build.tile;
    mindustry.game.Team team = build.team;
    int size = block.size;
    int rotation = build.rotation;

    MultiBlock multiBlock = (MultiBlock)block;
    setLinkEntities(multiBlock.setLinkBuild(build, block, tile, team, size, rotation));
    setLinkCreated(true);
    updateLinkProximity();
  }

  default void updateLinkProximity(){
    Building build = getMultiBlockEntity();

    if(linkEntities() != null){
      linkProximityMap().clear();

      //add link entity's proximity
      for(Building link : linkEntities()){
        for(Building linkProx : link.proximity){
          if(linkProx != build && !linkEntities().contains(linkProx)){
            if(checkValidPair(linkProx, link)){
              linkProximityMap().add(new Building[]{linkProx, link});
            }
          }
        }
      }

      //add self entity's proximity
      for(Building prox : build.proximity){
        if(!linkEntities().contains(prox)){
          if(checkValidPair(prox, build)){
            linkProximityMap().add(new Building[]{prox, build});
          }
        }
      }
    }
  }

  default boolean checkValidPair(Building target, Building source){
    for(Building[] pair : linkProximityMap()){
      Building pairTarget = pair[0];
      Building pairSource = pair[1];

      if(target == pairTarget){
        if(target.relativeTo(pairSource) == target.relativeTo(source)){
          return false;
        }
      }
    }
    return true;
  }

  @Annotations.MethodEntry(entryMethod = "onRemoved")
  default void onMultiBlockRemoved(){
    Building build = getMultiBlockEntity();
    MultiBlock multiBlock = (MultiBlock)build.block;
    multiBlock.createPlaceholder(build.tile, build.block.size);
  }

  @Annotations.MethodEntry(entryMethod = "canPickup", override = true)
  default boolean canPickupMultiBlock(){
    return false;
  }

  @Annotations.MethodEntry(entryMethod = "drawTeam", override = true)
  default void drawMultiBlockTeam(){
    Building build = getMultiBlockEntity();
    MultiBlock multiBlock = (MultiBlock)build.block;
    int size = build.block.size;
    int rotation = build.rotation;

    Tile teamPos = world.tile(build.tileX() + multiBlock.teamOverlayPos(size, rotation).x, build.tileY() + multiBlock.teamOverlayPos(size, rotation).y);
    setTeamPos(teamPos);

    if(teamPos != null){
      Draw.color(build.team.color);
      Draw.rect("block-border", teamPos.worldx(), teamPos.worldy());
      Draw.color();
    }
  }

  @Annotations.MethodEntry(entryMethod = "drawStatus", override = true)
  default void drawMultiBlockStatus(){
    Building build = getMultiBlockEntity();
    Block block = build.block;
    MultiBlock multiBlock = (MultiBlock)block;
    int size = block.size;
    int rotation = build.rotation;

    Tile statusPos = world.tile(build.tileX() + multiBlock.statusOverlayPos(size, rotation).x, build.tileY() + multiBlock.statusOverlayPos(size, rotation).y);
    setStatusPos(statusPos);

    if(block.enableDrawStatus && block.consumers.length > 0){
      float multiplier = block.size > 1 ? 1 : 0.64F;
      Draw.z(Layer.power + 1);
      Draw.color(Pal.gray);
      Fill.square(statusPos.worldx(), statusPos.worldy(), 2.5F * multiplier, 45);
      Draw.color(build.status().color);
      Fill.square(statusPos.worldx(), statusPos.worldy(), 1.5F * multiplier, 45);
      Draw.color();
    }
  }
}
