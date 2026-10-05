package LLL.world.blocks.storage;

import LLL.*;
import LLL.content.*;
import LLL.entities.gen.*;
import LLL.graphics.*;
import LLL.world.blocks.module.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;
import universecore.annotations.*;
import universecore.components.blockcomp.*;
import universecore.world.consumers.*;
import universecore.world.producers.*;

import static LLL.content.tantros.LaplacesTantros.*;
import static mindustry.Vars.*;

@SuppressWarnings("unused")
@Annotations.ImplEntries
public class CoreBackflow extends CoreBlock implements MovableBlockModule, ResourceBlockModule{
  public UnitType movableBlockType = OvulamUnitTypes.boostBuildUnit;

  public ConsFilter consFilter = new ConsFilter();
  public Seq<BaseConsumers> consumers = FactoryRecipes.smeltConsumers;
  public Seq<BaseProducers> producers = FactoryRecipes.smeltProducers;
  public float warmupSpeed = 0.05f, stopSpeed = 0.1f;
  public TextureRegion iconRegion;

  public float resourceCapacity = 100_000;

  public static final float[] thrusterSizesThis = {0f, 0f, 0f, 0f, 0.3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f};

  private static @Nullable Sector fromSector, toSector;

  static{
    Events.on(EventType.SectorLaunchLoadoutEvent.class, e -> {
      if(e.sector.planet == tantros){
        fromSector = e.from;
        toSector = e.sector;
      }
    });

    Events.on(EventType.WorldLoadEvent.class, e -> {
      fromSector = toSector = null;
    });
  }

  public CoreBackflow(String name){
    super(name);
    unitType = OvulamUnitTypes.daw;
    thrusterLength = 10;
    launchEffect = OvulamFx.launchTantros;
    customShadow = true;

    commandable = true;
    update = true;

    size = 6;

    alwaysUnlocked = true;

    landZoomFrom = 1f;

    //todo 因为建筑移动需要shown
    requirements(Category.effect, BuildVisibility.shown, ItemStack.with());
  }

  @Override
  public void load(){
    super.load();
    iconRegion = Core.atlas.find(name + "-icon");
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{iconRegion};
  }

  @Override
  public boolean canReplace(Block other){
    if(other.alwaysReplace) return true;
    if(other.privileged) return false;
    return other.replaceable && (other != this || (rotate && quickRotate)) && ((this.group != BlockGroup.none && other.group == this.group) || other == this) &&
      (size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    return true;
  }

  @Override
  public void displayExtra(Table table){
    for(int i = 0; i < consumers.size; i++){
      FactoryBlockComp.buildRecipe(table, consumers.get(i), producers.get(i));
    }
  }

  //todo call
  public static void playerSpawnBackflow(boolean isUnit, int id, Player player){
    CoreBuild core = null;

    if(isUnit){
      Unit unit = Groups.unit.getByID(id);
      if(unit instanceof MovableBuildUnitc m && m.tile() instanceof CoreBuild cb){
        core = cb;
      }
    }else{
      if(Vars.world.build(id) instanceof CoreBuild cb){
        core = cb;
      }
    }

    if(player == null || core == null) return;

    UnitType spawnType = ((CoreBlock)core.block).unitType;
    if(core.wasVisible){
      Fx.spawn.at(core);
    }

    player.set(core);

    if(!net.client()){
      Unit unit = spawnType.create(core.team());
      //reset reload so that the player can't shoot immediately
      for(var mount : unit.mounts){
        mount.reload = mount.weapon.reload;
      }
      unit.set(core);
      unit.rotation(90f);
      unit.impulse(0f, 3f);
      unit.spawnedByCore(true);
      unit.controller(player);
      unit.add();
    }

    if(state.isCampaign() && player == Vars.player){
      spawnType.unlock();
    }
  }

  @Override
  public void drawShadow(Tile tile){
    Drawf.squareShadow(tile.drawx(), tile.drawy(), size * tilesize * 1.85f, 1);
  }

  @Annotations.ImplEntries
  public class CoreBackflowBuilding extends CoreBuild implements MovableBuildModule, ResourceBuildModule{
    public int tableId;
    protected Interval interval;

    protected Runnable resetTable;
    protected int row = 6;
    protected static final Interp swingR = Interp.sine;
    protected int effected;

    @Override
    public boolean inFogTo(Team viewer){
      boolean launching = renderer.landTime != 0
        && renderer.isLaunching()
        && Reflect.get(Renderer.class, renderer, "launchAnimator") == this;

      wasVisible = !launching;

      return super.inFogTo(viewer) || launching;
    }

    @Override
    public void buildConfiguration(Table table){
      table.table(Styles.black3, t -> {
        t.table(buttons -> {
          buttons.defaults().size(32).pad(4);

          buttons.button(Icon.upOpen, Styles.clearNoneTogglei, () -> LuluMod.tantrosDialog.show()).row();

          Boolp validSector = () -> {
            Sector currentSector = state.getSector();
            return currentSector != null && currentSector.planet == tantros && currentSector.id != tantros.startSector;
          };
          buttons.button(Icon.link, Styles.clearNoneTogglei, () -> {
            if(validSector.get()){
              ui.showConfirm("是否返回锚点", () -> {
                LuluMod.tantrosDialog.module = resources();
                LuluMod.tantrosDialog.playSector(state.getSector(), tantros.sectors.get(tantros.startSector), () -> {
                });
              });
            }
          }).checked(ib -> validSector.get()).update(ib -> ib.setColor(validSector.get() ? Color.white : Color.gray)).row();
        });

        buildConfigurationResourceBuild(t);
      });
    }

    @Override
    public void requestSpawn(Player player){
      if(!unitType.supportsEnv(state.rules.env) || !allowSpawn) return;

      boolean isUnit = unitSelf() != null;
      playerSpawnBackflow(isUnit, isUnit ? unitSelf().id : tile.pos(), player);
    }

    @Override
    public void drawLaunch(){
      if((state.rules.env & Env.underwater) == 0) super.drawLaunch();
      else{
        if(!renderer.isLaunching()){
          drawLandingUnderwater();
        }else if(fromSector != null && toSector != null){
          drawLaunchingUnderwater();
        }else{
          super.drawLaunch();
        }
      }
    }

    @Override
    public float launchDuration(){
      if(((state.rules.env & Env.underwater) != 0 && renderer.isLaunching() && fromSector != null && toSector != null)){
        return 300;
      }
      return super.launchDuration();
    }

    @Override
    public void updateLaunch(){
    }

    @Override
    public void endLaunch(){
    }

    @Override
    public float zoomLaunch(){
      Core.camera.position.set(this);

      if(renderer.isLaunching()){
        float fin = Math.min(1, (1 - renderer.getLandTimeIn()) / (0.6f + 0.05f));
        //fin = Interp.pow2In.apply(fin);
        return swingR.apply(Scl.scl(landZoomTo), Scl.scl(landZoomFrom * 0.5f), swingR.apply(fin));
      }

      return landZoomInterp.apply(Scl.scl(landZoomFrom), Scl.scl(landZoomTo), renderer.getLandTimeIn());
    }

    public void drawLaunchingUnderwater(){
      float totalFin = 1 - renderer.getLandTimeIn();
      float minz = 0.5f;

      if(totalFin < 0.6){
        float fin = totalFin / 0.6f;
        float fout = 1f - fin;

        float zoomScl = (Interp.pow4Out.apply(swingR.apply(fout)) + 1) / 2;
        float scl = Scl.scl(4f) / renderer.getDisplayScale() * zoomScl;
        float rotation = Interp.pow2In.apply(fin) * 160;
        float thrustOpen = 0.25f;
        float thrusterFrame = fin >= thrustOpen ? 1f : fin / thrustOpen;
        float thrusterSize = Mathf.sample(thrusterSizesThis, fin);

        Draw.mixcol(Liquids.water.color, (0.4f - fin) * 0.5f);

        float ff = Interp.pow2In.apply(fin);
        Tmp.v1.trns(200, 50 * tilesize * ff);
        Drawf.squareShadow(x + Tmp.v1.x, y + Tmp.v1.y, size * tilesize * 1.85f * (1 + ff * 2), 1 - ff);

        Draw.scl(scl);

        float strength = (1f + (size - 3) / 2.5f) * scl * thrusterSize * (0.95f + Mathf.absin(2f, 0.1f));
        float offset = (size - 3) * 3f * scl;

        for(int i = 0; i < 4; i++){
          Tmp.v1.trns(i * 90 + rotation, 1f);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 2f + offset);
          Draw.color(team.color);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 0.5f + offset);
          Draw.color(Color.white);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
        }

        drawLandingThrusters(x, y, rotation, thrusterFrame);
        Drawf.spinSprite(region, x, y, rotation);

        if(teamRegions[team.id] == teamRegion) Draw.color(team.color);

        Drawf.spinSprite(teamRegions[team.id], x, y, rotation);
      }else if(totalFin < 0.8){
        if(interval == null){
          interval = new Interval(3);
        }
        if(interval.get(5) && totalFin < 0.7f){
          OvulamFx.coreBackflowCharge.at(x, y, 0, Pal.accent, new OvulamTrail(20, 0){{
            trailWidth = 10f;
          }});
        }
        float fin = Mathf.curve(totalFin, 0.6f, 0.8f);
        float fout = 1f - fin;

        float rotation = Interp.pow2Out.apply(Math.min(fin * 3, 1)) * 20 + 160;

        float heading = Tmp.v31.set(toSector.rect.center).sub(fromSector.rect.center).angle(fromSector.rect.right);

        float zoomScl = minz;
        float scl = Scl.scl(4f) / renderer.getDisplayScale() * zoomScl;

        Draw.scl(scl);

        float strength = (1f + (size - 3) / 2.5f) * scl * (0.95f + Mathf.absin(2f, 0.1f));
        float offset = (size - 3) * 3f * scl;

        for(int i = 0; i < 4; i++){
          Tmp.v1.trns(i * 90 + rotation, 1f);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 2f + offset);
          Draw.color(team.color);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 0.5f + offset);
          Draw.color(Color.white);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
        }

        drawLandingThrusters(x, y, rotation, 1);
        Drawf.spinSprite(region, x, y, rotation);

        if(teamRegions[team.id] == teamRegion) Draw.color(team.color);

        Drawf.spinSprite(teamRegions[team.id], x, y, rotation);
      }else{
        if(effected == 0){
          OvulamFx.coreBackflowChargeFinished.at(x, y);
          effected++;
        }
        float fin = Mathf.curve(totalFin, 0.8f);
        float fout = 1f - fin;

        float heading = Tmp.v31.set(toSector.rect.center).sub(fromSector.rect.center).angle(fromSector.rect.right);

        float zoomScl = minz;
        float scl = Scl.scl(4f) / renderer.getDisplayScale() * zoomScl;

        Draw.scl(scl);

        Tmp.v1.trns(heading, 600f * tilesize * Interp.pow2In.apply(Math.max((fin - 0.4f) * 2, 0)));
        float x = this.x + Tmp.v1.x;
        float y = this.y + Tmp.v1.y;

        if(effected == 1 && fin > 0.45f){
          OvulamFx.coreBackflowStartMove.at(x, y, heading);
          effected++;
        }

        if(fin > 0.45f && interval.get(5f)){
          OvulamFx.coreBackflowMove.at(x, y, heading);
        }

        float strength = (1f + (size - 3) / 2.5f) * scl * (0.95f + Mathf.absin(2f, 0.1f));
        float offset = (size - 3) * 3f * scl;

        for(int i = 0; i < 4; i++){
          Tmp.v1.trns(i * 90, 1f);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 2f + offset);
          Draw.color(team.color);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

          Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 0.5f + offset);
          Draw.color(Color.white);
          Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
        }

        drawLandingThrusters(x, y, 0, 1);
        Draw.rect(region, x, y);

        if(teamRegions[team.id] == teamRegion) Draw.color(team.color);

        Draw.rect(teamRegions[team.id], x, y);
      }

      Draw.color();
      Draw.scl();
      Draw.reset();
    }

    public void drawLandingUnderwater(){
      float fin = renderer.getLandTimeIn();
      float fout = 1f - fin;

      float scl = Scl.scl(4f) / renderer.getDisplayScale();
      float rotation = 0;
      float thrustOpen = 0.25f;
      float thrusterFrame = fin >= thrustOpen ? 1f : fin / thrustOpen;
      float thrusterSize = Mathf.sample(thrusterSizes, fin);

      Draw.mixcol(Liquids.water.color, (fin - 0.4f) * 0.5f);

      Draw.scl(scl);

      //thruster
      float strength = (1f + (size - 3) / 2.5f) * scl * thrusterSize * (0.95f + Mathf.absin(2f, 0.1f));
      float offset = (size - 3) * 3f * scl;

      for(int i = 0; i < 4; i++){
        Tmp.v1.trns(i * 90, 1f);

        Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 2f + offset);
        Draw.color(team.color);
        Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

        Tmp.v1.setLength((size * tilesize / 2f + 1f) * scl + strength * 0.5f + offset);
        Draw.color(Color.white);
        Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
      }

      drawLandingThrusters(x, y, rotation, thrusterFrame);
      Drawf.spinSprite(region, x, y, rotation);

      if(teamRegions[team.id] == teamRegion) Draw.color(team.color);

      Drawf.spinSprite(teamRegions[team.id], x, y, rotation);

      Draw.color();
      Draw.scl();
      Draw.reset();
    }

    @Override
    public boolean isCommandable(){
      return true;
    }

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);
      state.teams.registerCore(this);
      createResourceBuild();
      return this;
    }

    @Override
    public boolean shouldConsume(){
      return items.get(OvulamItems.ferroManganese) < 100 && super.shouldConsume();
    }

    @Override
    public void onRemoved(){
      int totalCapacity = proximity.sum(e -> e.items != null && e.items == items ? e.block.itemCapacity : 0);

      proximity.each(e -> owns(e) && e.items == items && owns(e), t -> {
        StorageBuild ent = (StorageBuild)t;
        ent.linkedCore = null;
        ent.items = new ItemModule();
        for(Item item : content.items()){
          ent.items.set(item, (int)Math.min(ent.block.itemCapacity, items.get(item) * (float)ent.block.itemCapacity / totalCapacity));
        }
      });

      for(CoreBuild other : state.teams.cores(team)){
        other.onProximityUpdate();
      }
    }

    @Override
    public void killed(){
      state.teams.unregisterCore(this);
      super.killed();
    }
  }
}
