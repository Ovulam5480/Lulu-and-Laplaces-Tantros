package LLL.entities.comp.entitycomps;

import LLL.entities.gen.*;
import LLL.graphics.*;
import LLL.world.blocks.entity.*;
import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.*;

import static mindustry.Vars.*;

@Annotations.EntityDef(LulusSeparatorEntityc.class)
@Annotations.EntityComponent
public abstract class LulusSeparatorEntityComp implements TileEntityc, LaunchAnimator{
  @Annotations.Import
  float x, y;

  public static final float cloudScaling = 1700f, cfinScl = -2f, cfinOffset = 0.3f, calphaFinOffset = 0.25f, cloudAlpha = 0.81f;
  public static final float[] cloudAlphas = {0, 0.5f, 1f, 0.1f, 0, 0f};
  public static final float[] thrusterSizes = {0f, 0f, 0f, 0f, 0.3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f};

  public Music landMusic = Musics.land;
  public float launchSoundVolume = 1f, landSoundVolume = 1f;
  public Sound launchSound = Sounds.coreLaunch;
  public Sound landSound = Sounds.coreLand;
  public Effect launchEffect = Fx.launch;
  public final Interp landZoomInterp = new Interp.PowIn(15);
  public final float landZoomFrom = 0.005f, landZoomTo = 4f;

  public float launchScl = 0.5f;

  protected float cloudSeed;
  public float landParticleTimer;
  ;

  public boolean hasLanding = false;

  @Annotations.Replace
  @Override
  public LulusSeparator blockAs(){
    return (LulusSeparator)block();
  }

  @Override
  public void update(){
  }

  @Override
  public void draw(){
    if(!hasLanding) return;

    Draw.rect(blockAs().region, x, y);

    Draw.color(Pal.accent);
    //SglDraw.gradientCircle(x, y, blockAs().entitySize * 8f / 2, 0);

    Draw.reset();
  }

  @Override
  public void add(){
    renderer.showLanding(this);
  }

  @Override
  public void drawLaunch(){
    var clouds = Core.assets.get("sprites/clouds.png", Texture.class);

    float fin = renderer.getLandTimeIn();
    float cameraScl = renderer.getDisplayScale();

    float fout = 1f - fin;

    //todo particles

    drawLanding(x, y);

    Draw.color();
    Draw.mixcol(Color.white, Interp.pow5In.apply(fout));

    //accent tint indicating that the core was just constructed
    if(renderer.isLaunching()){
      float f = Mathf.clamp(1f - fout * 12f);
      if(f > 0.001f){
        Draw.mixcol(Pal.accent, f);
      }
    }

    //draw clouds
    if(state.rules.cloudColor.a > 0.0001f){
      float scaling = cloudScaling;
      float sscl = Math.max(1f + Mathf.clamp(fin + cfinOffset) * cfinScl, 0f) * cameraScl;

      Tmp.tr1.set(clouds);
      Tmp.tr1.set(
        (Core.camera.position.x - Core.camera.width / 2f * sscl) / scaling,
        (Core.camera.position.y - Core.camera.height / 2f * sscl) / scaling,
        (Core.camera.position.x + Core.camera.width / 2f * sscl) / scaling,
        (Core.camera.position.y + Core.camera.height / 2f * sscl) / scaling);

      Tmp.tr1.scroll(10f * cloudSeed, 10f * cloudSeed);

      Draw.alpha(Mathf.sample(cloudAlphas, fin + calphaFinOffset) * cloudAlpha);
      Draw.mixcol(state.rules.cloudColor, state.rules.cloudColor.a);
      Draw.rect(Tmp.tr1, Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height);
      Draw.reset();
    }
  }

  @Override
  public void beginLaunch(boolean launching){
    cloudSeed = Mathf.random(1f);
    if(launching){
      Fx.coreLaunchConstruct.at(x, y, blockAs().size);
    }

    if(!headless){
      (launching ? launchSound : landSound).play(launchSoundVolume);
      // Add fade-in and fade-out foreground when landing or launching.
      if(renderer.isLaunching()){
        float margin = 30f;

        Image image = new Image();
        image.color.a = 0f;
        image.touchable = Touchable.disabled;
        image.setFillParent(true);
        image.actions(Actions.delay((launchDuration() - margin) / 60f), Actions.fadeIn(margin / 60f, Interp.pow2In), Actions.delay(6f / 60f), Actions.remove());
        image.update(() -> {
          image.toFront();
          ui.loadfrag.toFront();
          if(state.isMenu()){
            image.remove();
          }
        });
        Core.scene.add(image);
      }else{
        Image image = new Image();
        image.color.a = 1f;
        image.touchable = Touchable.disabled;
        image.setFillParent(true);
        image.actions(Actions.fadeOut(35f / 60f), Actions.remove());
        image.update(() -> {
          image.toFront();
          ui.loadfrag.toFront();
          if(state.isMenu()){
            image.remove();
          }
        });
        Core.scene.add(image);

        Time.run(launchDuration(), () -> {
          launchEffect.at(this);
          Effect.shake(5f, 5f, this);

          if(state.isCampaign() && Vars.showSectorLandInfo && (state.rules.sector.preset == null || state.rules.sector.preset.showSectorLandInfo)){
            ui.announce("[accent]" + state.rules.sector.name() + "\n" +
              (state.rules.sector.info.resources.any() ? "[lightgray]" + Core.bundle.get("sectors.resources") + "[white] " +
                state.rules.sector.info.resources.toString(" ", UnlockableContent::emoji) : ""), 5);
          }
        });
      }
    }
  }

  public void drawLanding(float x, float y){
    float fin = renderer.getLandTimeIn();
    float fout = 1f - fin;

    //TextureRegion region = blockAs().region;
    TextureRegion region = blockAs().launchingRegions[Mathf.floor(fin * blockAs().launchingRegions.length)];

    float scl = Scl.scl(landZoomTo) / renderer.getDisplayScale() * launchScl;
    float rotation = Interp.pow2Out.apply(fout) * 1800f;

    Draw.scl(scl);

    Drawf.spinSprite(region, x, y, rotation);

    Draw.scl();
    Draw.reset();
  }

  @Override
  public void endLaunch(){
    hasLanding = true;

    control.input.panCamera(Tmp.v1.set(this));

    OvulamFx.LulusSeparatorLanding.at(x, y);
    landParticleTimer = 0f;
    //Effect.shake(5f, 120f, this);
  }

  @Override
  public void updateLaunch(){
    float in = renderer.getLandTimeIn() * launchDuration();
    float tsize = Mathf.sample(thrusterSizes, (in + 35f) / launchDuration());

    landParticleTimer += tsize * Time.delta;
  }

  @Override
  public float launchDuration(){
    return blockAs().landDuration;
  }

  //todo ?
  @Override
  public Music landMusic(){
    return null;
  }

  //todo
  @Override
  public float zoomLaunch(){
    Core.camera.position.set(this);
    return landZoomInterp.apply(Scl.scl(landZoomFrom), Scl.scl(landZoomTo), renderer.getLandTimeIn()) * launchScl;
  }
}
