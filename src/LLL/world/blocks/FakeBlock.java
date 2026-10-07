package LLL.world.blocks;

import LLL.world.AAAtodo.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import universe.util.reflect.*;
import universe.util.reflect.accessor.*;
import universecore.annotations.*;

/**
 * 帮助需要替换方块放置的建筑，使用此方块放置虚建筑后立即转换为实建筑，实建筑不应当在放置选择栏位中可见
 *
 * @author EBwilson *
 * @since 1.5
 */
public class FakeBlock extends Block{
  public final Block maskedBlock;
  public Boolf3<Tile, Team, Integer> placeValid;

  public FakeBlock(Block maskedBlock, Boolf3<Tile, Team, Integer> placeValid){
    super(maskedBlock.name + "-mask");
    this.placeValid = placeValid;
    this.maskedBlock = maskedBlock;
    if(!maskedBlock.hasBuilding())
      throw new IllegalArgumentException("masked block must has building");
  }

  @Override
  public void setStats(){
    maskedBlock.setStats();
    stats = maskedBlock.stats;
  }

  @Override
  public void init(){
    super.init();
    health = maskedBlock.health;

    localizedName = maskedBlock.localizedName;
    description = maskedBlock.description;
    details = maskedBlock.details;

    size = maskedBlock.size;
    update = true;
    rotate = maskedBlock.rotate;
    rotateDraw = maskedBlock.rotateDraw;
    conveyorPlacement = maskedBlock.conveyorPlacement;
    group = maskedBlock.group;
    requirements = maskedBlock.requirements;
    category = maskedBlock.category;

    buildVisibility = BuildVisibility.shown;
    //UncDatabaseDialog.hide(maskedBlock);
    maskedBlock.category = null;
  }

  @Override
  public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list){
    maskedBlock.drawPlanConfigTop(plan, list);
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    maskedBlock.drawPlace(x, y, rotation, valid);
  }

  @Override
  public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid){
    maskedBlock.drawPlan(plan, list, valid);
  }

  @Override
  public TextureRegion getPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
    return maskedBlock.getPlanRegion(plan, list);
  }

  @Override
  public float drawPlaceText(String text, int x, int y, boolean valid){
    return maskedBlock.drawPlaceText(text, x, y, valid);
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return (tile.block() != maskedBlock && tile.block().size == size && (tile.build == null || !tile.block().rotate || tile.build.rotation == rotation)) && (placeValid == null || placeValid.get(tile, team, rotation));
  }

  @Override
  protected TextureRegion[] icons(){
    MethodInvoker0<Block, TextureRegion[]> handler = Reflection.accessMethod(Block.class, "icons", TextureRegion[].class);
    return handler.invoke(maskedBlock);
  }

  @Annotations.ImplEntries
  public class FakeBuild extends Building implements ReplaceBuildComp{
    Building realBuild;
    private static final FieldAccessor<Tile, Block> field = Reflection.accessField(Tile.class, Block.class, "block");

    @Override
    public Building init(Tile tile, Team team, boolean shouldAdd, int rotation){
      field.set(tile, maskedBlock);
      realBuild = maskedBlock.newBuilding();
      this.tile = tile;
      this.x = tile.drawx();
      this.y = tile.drawy();
      this.rotation = rotation;
      this.team = team;
      realBuild.init(tile, team, shouldAdd, rotation);

      return realBuild;
    }

    @Override
    public void onReplaced(ReplaceBuildComp old){
      if(realBuild instanceof ReplaceBuildComp re){
        re.onReplaced(old);
      }
    }
  }
}
