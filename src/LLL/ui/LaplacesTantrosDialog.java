package LLL.ui;

import LLL.content.tantros.*;
import LLL.util.*;
import LLL.world.blocks.module.*;
import LLL.world.modules.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.storage.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.graphics.g3d.PlanetRenderer.*;

public class LaplacesTantrosDialog extends Dialog implements PlanetRenderer.PlanetInterfaceRenderer{
  private static Planet tantros;
  private static Sector startSector;
  private static Vec3 startSectorCenter;

  public final PlanetRenderer planets = renderer.planets;
  public final float defaultZoom = 0.5f, magnifyZoom = 0.2f;

  public PlanetParams state = new PlanetParams();
  public @Nullable Sector hovered, launchSector;
  public @Nullable Sector currentSector, selectSector;

  public Vec3 cameraPos = new Vec3();

  private float universeSeconds;

  public @Nullable ResourceModule module;

  public int currentGameStage = OvulamSettings.registerInt("current-game-stage", o -> currentGameStage = o, 0);

//  static {
//    Events.on(EventType.ClientLoadEvent.class, e -> {
//      Reflect.set(renderer.planets, "batch", new VertexBatch3D(20000, false, true, 1));
//    });
//  }

  public LaplacesTantrosDialog(){
    setFillParent(true);

    state.planet = tantros = LaplacesTantros.tantros;
    state.zoom = defaultZoom;
    state.drawUi = true;
    state.renderer = this;
    state.alwaysDrawAtmosphere = true;
    state.drawSkybox = false;

    startSector = tantros.sectors.get(tantros.startSector);
    startSectorCenter = startSector.rect.center;

    shown(() -> {
      currentSector = Vars.state.getSector();
      selectSector = null;

      if(currentSector != null){
        getSectorCamera(currentSector, cameraPos);
        state.camPos.set(cameraPos);
      }

      universeSeconds = (universe.seconds() - 1) % (Mathf.pi * 300);

      setup();
    });

    Events.on(EventType.WorldLoadEvent.class, e -> {
      if(Vars.state.rules.sector != null && Vars.state.rules.sector.id == tantros.startSector && module != null){
        ResourceModule to = findCore().resources();

        to.add(module);
        startSector.info.items.add(module.itemModule);

        module = null;

        abandon();
      }
    });
  }

  public static Vec3 getSectorCamera(Sector sector, Vec3 out){
    return out.set(sector.rect.center).rotate(Vec3.Y, -tantros.getRotation() + 180).scl(-1, 1, -1);
  }

  public void setup(){
    clearChildren();
    stack(
      new Element(){
        {
          setSize(Core.graphics.getWidth(), Core.graphics.getHeight());
          addListener(new ElementGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, KeyCode button){
              selectSector = Core.scene.getDialog() == LaplacesTantrosDialog.this ? state.planet.getSector(planets.cam.getMouseRay(), PlanetRenderer.outlineRad * state.planet.radius) : null;
            }
          });

          dragged((cx, cy) -> {
            if(Core.input.getTouches() > 1) return;

            Vec3 pos = cameraPos;

            float upV = pos.angle(Vec3.Y);
            float xscale = 9f, yscale = 10f;
            float margin = 1;

            float speed = 1f - Math.abs(upV - 90) / 90f;

            pos.rotate(state.camUp, cx / xscale * speed);

            float amount = cy / yscale;
            amount = Mathf.clamp(upV + amount, margin, 180f - margin) - upV;

            pos.rotate(Tmp.v31.set(state.camUp).rotate(state.camDir, 90), amount);
          });
        }

        @Override
        public void draw(){
          planets.render(state);//todo render tantros only
        }
      },
      new Table(view -> {
        view.setFillParent(true);
        view.table(t -> {
          t.table(Styles.black3, ta -> {
            ta.defaults().size(210, 64);

            Boolp b = () -> selectSector != null && selectSector.id != tantros.startSector && launchable(selectSector);

            ta.button("开始", Icon.play, () -> {
              if(b.get()) playSector(currentSector, selectSector, () -> {
              });
            }).update(tb -> tb.setColor(b.get() ? Color.white : Color.gray)).row();
            ta.button("@sector.abandon", Icon.refresh, () -> Vars.ui.showConfirm("重置", "是否放弃并重置星球？", () -> {
              playSector(null, startSector, this::abandon);
            })).row();
            ta.button("@back", Icon.left, this::hide).row();
          }).left().bottom().expand().pad(10);
        }).grow();
      })
    ).grow();
  }

  public boolean accessible(Sector sector){
    return accessible(sector.rect.center);
  }

  public boolean accessible(Vec3 sectorCenter){
    return sectorCenter.angle(startSectorCenter) < currentGameStage * 9 + 18;
  }

  public boolean launchable(Sector sector){
    return currentSector.near().contains(sector);
  }

  public @Nullable ResourceBuildModule findCore(){
    return (ResourceBuildModule)Vars.state.teams.get(Vars.state.rules.defaultTeam).cores.find(c -> c instanceof ResourceBuildModule);
  }

  public void abandon(){
    tantros.sectors.each(s -> {
      if(s.hasSave() && s.id != tantros.startSector){
        s.info.items.clear();
        s.info.hasCore = false;
        s.info.production.clear();
        s.saveInfo();
      }
    });

    int seed = Mathf.random(1000000);
    OvulamSettings.putInt("laplace-tantros-seed", seed);
    tantros.generator.seed = seed;
  }

  @Override
  public void act(float delta){
    super.act(delta);

    state.camPos.lerp(selectSector != null ? getSectorCamera(selectSector, Tmp.v31) : cameraPos, 0.05f);
    state.zoom = Mathf.lerp(state.zoom, selectSector == null ? defaultZoom : magnifyZoom, 0.03f);

    universeSeconds = Mathf.slerpRad(universeSeconds, universe.secondsf() % (Mathf.pi * 300), 0.01f);

    if(state.planet.hasGrid()){
      hovered = Core.scene.getDialog() == this ? state.planet.getSector(planets.cam.getMouseRay(), PlanetRenderer.outlineRad * state.planet.radius) : null;
    }
  }

  @Override
  public void renderSectors(Planet planet){
    if(hovered != null) planets.fill(hovered, hoverColor.write(Tmp.c1).mulA(state.uiAlpha), -0.003f);
    if(currentSector != null) planets.drawBorders(currentSector, Pal.accent, state.uiAlpha);
    if(selectSector != null) planets.drawSelection(selectSector, state.uiAlpha);
    planets.drawBorders(startSector, Pal.accent, state.uiAlpha);

    for(Sector sec : tantros.sectors){
      //if(cameraPos.angle(sec.rect.center) > 100)return;

      float alpha = Mathf.sin(universeSeconds + sec.rect.center.y + sec.rect.center.x * 0.2f) * 4 - 3;

      if(alpha > 0){
        Vec3 vec3 = sec.rect.center;
        float scale = Simplex.noise3d(0, 1, 1, 1, vec3.x, vec3.y, vec3.z);
        fillScaled(planets.batch,
          sec,
          Tmp.c1.set(accessible(vec3) ? Pal.accent : Pal.remove).a(Mathf.lerp(0.7f, 1, alpha)),
          0.3f * Interp.pow2In.apply(alpha),
          Interp.pow2Out.apply(alpha * scale));
      }
    }

    planets.batch.flush(Gl.triangles);
  }

  public void fillScaled(VertexBatch3D batch, Sector sector, Color color, float offset, float scale){
    float rr = outlineRad * tantros.radius + offset;

    Vec3 center = sector.tile.v;

    Tmp.v33.set(center).lerp(sector.tile.corners[0].v, scale).setLength(rr);

    for(int i = 1; i < sector.tile.corners.length - 1; i++){
      PlanetGrid.Corner c = sector.tile.corners[i], next = sector.tile.corners[(i + 1) % sector.tile.corners.length];
      batch.tri(Tmp.v31.set(center).lerp(c.v, scale).setLength(rr), Tmp.v32.set(center).lerp(next.v, scale).setLength(rr), Tmp.v33, color);
    }
  }

  @Override
  public void renderProjections(Planet planet){
  }

  public void playSector(Sector from, Sector sector, Runnable back){
    if(from != null){
      Events.fire(new EventType.SectorLaunchLoadoutEvent(sector, from, universe.getLastLoadout()));

      CoreBlock.CoreBuild core = player.team().core();
      if(core == null || settings.getBool("skipcoreanimation")){
        control.playSector(from, sector);
        Time.runTask(8f, () -> {
          hide();
          back.run();
        });
      }else{
        Time.runTask(5f, () -> {
          renderer.showLaunch(core);
          Time.runTask(core.launchDuration() - 8f, () -> control.playSector(from, sector));
          hide();
          back.run();
        });
      }
    }else{
      control.playSector(sector);
      Time.runTask(8f, () -> {
        hide();
        back.run();
      });
    }
  }
}
