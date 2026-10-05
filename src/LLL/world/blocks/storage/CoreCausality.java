package LLL.world.blocks.storage;

import LLL.*;
import LLL.content.*;
import LLL.graphics.*;
import LLL.world.blocks.module.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

import static mindustry.Vars.*;

@Annotations.ImplEntries
public class CoreCausality extends CoreBlock implements ResourceBlockModule{
  public float resourceCapacity = 10_000_000;

  public float launchScl = 0.5f;
  public TextureRegion[] launchingRegions;

  public CoreCausality(String name){
    super(name);
    unitType = OvulamUnitTypes.daw;
    size = 15;

    landZoomFrom = 0.03f;
    landZoomTo = 4f;

    landSound = Sounds.none;

    landZoomInterp = new Interp.PowIn(10);

    landDuration = 160f;

    requirements(Category.effect, BuildVisibility.shown, ItemStack.empty);
  }

  @Override
  public void load(){
    super.load();

    Seq<TextureRegion> regions = new Seq<>();
    regions.add(region);
    int i = 0;
    while(Core.atlas.has(name + "-" + i)){
      regions.add(Core.atlas.find(name + "-" + i));
      i++;
    }
    launchingRegions = regions.toArray(TextureRegion.class);
  }

  @Annotations.ImplEntries
  public class CoreCausalityBuild extends CoreBuild implements ResourceBuildModule{
    private static final Rand rand = new Rand();

    @Override
    public void drawLaunch(){
      if(renderer.isLaunching()) drawLaunching();
      else drawLanding();
    }

    public void drawLaunching(){

    }

    public void drawLanding(){
      float fin = renderer.getLandTimeIn();
      float cameraScl = renderer.getDisplayScale();

      float fout = 1f - fin;
      float scl = Scl.scl(4f) / cameraScl;
      float pfin = Interp.pow3Out.apply(fin);

      Draw.color(Pal.lightTrail);

      rand.setSeed(1);
      for(int i = 0; i < 1000; i++){
        float dst = scl / Math.max(rand.nextFloat() - fin * 1.05f, 0.000001f);
        Tmp.v1.trns(rand.nextFloat() * 360f, dst);

        Lines.stroke(scl * pfin);
        Lines.lineAngle(x + Tmp.v1.x, y + Tmp.v1.y, Tmp.v1.angle(), dst);
      }
      Draw.color();

      TextureRegion region = launchingRegions[Mathf.floor(fin * launchingRegions.length)];

      float sscl = Scl.scl(landZoomTo) / renderer.getDisplayScale() * launchScl;
      float rotation = Interp.pow2OutInverse.apply(fin) * 1200f;

      Draw.scl(sscl);

      Drawf.spinSprite(region, x, y, rotation);

      Draw.scl();
      Draw.reset();
    }

    @Override
    public void endLaunch(){
      control.input.panCamera(Tmp.v1.set(this));

      if(!renderer.isLaunching()){
        OvulamEffect.shake(16f, 120f, x, y);
        OvulamFx.LulusSeparatorLanding.at(x, y);
      }
      landParticleTimer = 0f;
    }

    @Override
    public void updateLaunch(){
      float in = renderer.getLandTimeIn() * launchDuration();
      float tsize = Mathf.sample(thrusterSizes, (in + 35f) / launchDuration());

      landParticleTimer += tsize * Time.delta;
    }

    @Override
    public float zoomLaunch(){
      Core.camera.position.set(this);
      return landZoomInterp.apply(Scl.scl(landZoomFrom), Scl.scl(landZoomTo), renderer.getLandTimeIn()) * launchScl;
    }

    @Override
    public void buildConfiguration(Table table){
      table.table(Styles.black3, t -> {
        t.table(buttons -> {
          buttons.defaults().size(32).pad(4);

          buttons.button(Icon.upOpen, Styles.clearNoneTogglei, () -> LuluMod.tantrosDialog.show());
        });

        buildConfigurationResourceBuild(t);
      });
    }

    @Override
    public void updateTile(){
      super.updateTile();

      if(timer.get(60)){
        OvulamFx.bezierTrail.at(x, y, 0, Pal.accent,
          new OvulamTrail(120, 0){{
            trailWidth = 2f;
          }});
      }
    }

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);
      createResourceBuild();
      return this;
    }
  }
}
