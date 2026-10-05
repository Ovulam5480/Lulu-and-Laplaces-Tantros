package LLL.content.tantros;

import LLL.content.*;
import LLL.type.biomeplanet.*;
import arc.graphics.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class LaplacesTantrosPlanet extends Planet{
  public LaplacesTantrosPlanetGenerator laplaceGenerator;

  public LaplacesTantrosPlanet(){
    super("laplace's tantros", Planets.sun, 2f, 4);

    alwaysUnlocked = true;

    defaultCore = OvulamBlocks.coreCausality;

    //x为温度, y为深度
    generator = laplaceGenerator = new LaplacesTantrosPlanetGenerator()
      //.add(new BiomeBox(OvulamBiomes.beach, 0, 0, 1, 0.2f))
      .add(new BiomeBox(OvulamBiomes.mats, 0, 0.2f, 1, 0.3f))
      .add(new BiomeBox(OvulamBiomes.deepArea, 0, 0.5f, 1, 0.5f))
    ;
    meshLoader = () -> new HexMesh(this, 6);

    iconColor = Color.valueOf("597be3");

    startSector = 205;

    defaultEnv = Env.underwater | Env.terrestrial | Env.oxygen | Env.groundWater;
    ruleSetter = r -> {
      r.loadout.clear();
      r.ghostBlocks = false;
      //r.lighting = true;
      //r.ambientLight = Color.valueOf("101016ee");
      r.infiniteResources = true;
    };

    updateLighting = false;

    clearSectorOnLose = true;
    allowLaunchLoadout = false;
    allowLaunchToNumbered = false;
    unlockedOnLand.add(OvulamBlocks.coreBackflow);
  }

  @Override
  public void applyRules(Rules rules, boolean customGame){
    ruleSetter.get(rules);
    if(state.rules.sector != null){
      Sector sector = state.rules.sector;
      laplaceGenerator.applyBiomeRule(sector, rules);

      if(sector.preset != null){
        sector.preset.rules.get(rules);
      }
    }

//    Sector sector = state.getSector();
//    if(sector.preset == null && sector instanceof OvulamSector){
//      Log.info(LuluMod.oIndexer.lodePacketTree.totalObjects);
//      //todo packetOre
//    }
  }
}
