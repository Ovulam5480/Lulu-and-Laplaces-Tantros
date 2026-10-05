package LLL.content;

import LLL.type.biomeplanet.*;
import LLL.type.resourceStacks.*;
import LLL.util.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.noise.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class OvulamBiomes{
  public static Biome beach, mats, deepArea;

  public static void load(){
    beach = new Biome("beach"){
      @Override
      public void applyRules(Rules rules){
        rules.env &= ~Env.underwater;
      }
    }.floors(Blocks.water, 5, Blocks.sandWater, 1, Blocks.sand, 2);

    mats = new Biome("mats"){
      private static final Seq<ResourceStack<?>> lodes = Seq.with(
        new ItemResourceStack().set(OvulamItems.cobaltNodule, 10f),
        new ItemResourceStack().set(OvulamItems.manganeseNodule, 40f));

      @Override
      public void pass(int x, int y, int seed, TileGen tile, Rand rand, float noise){
        if(within(noise, 0.4f, 0.41f) && rand.chance(1 - Mathf.curve(noise, 0.4f, 0.41f))){
          tile.floor = Blocks.bluemat;
        }

        if(noise < 0.4 && rand.chance(0.3f * (1 - Mathf.curve(noise, 0, 0.4f)))){
          tile.block = Blocks.purbush;
          return;
        }

        if(within(noise, 0.59f, 0.6f) && rand.chance(Mathf.curve(noise, 0.59f, 0.6f))){
          tile.floor = Blocks.redmat;
        }

        if(noise > 0.6 && rand.chance(0.3f * Mathf.curve(noise, 0.6f))){
          tile.block = Blocks.redweed;
          return;
        }

        float n1 = Simplex.noise2d(seed, 1, 1, 0.01f, x, y);
        float c = Interps.A.apply(Mathf.curve(noise, 0.4f, 0.6f));

        float cn1 = c * n1;

        if(cn1 > 0.85f && (cn1 > 0.9f || rand.chance(Mathf.curve(cn1, 0.85f, 0.9f)))){
          float grain = c * Math.abs(Simplex.noise2d(seed, 1, 1, 0.1f, x, y) - 0.5f);
          tile.floor = within(grain, 0.2f, 0.35f) ? Blocks.yellowStone : Blocks.rhyolite;

          if(cn1 > 0.95f){
            OvulamPacketTypes.lodePacket.create(null, x * 8, y * 8).copyResourceStacks(lodes, Mathf.curve(cn1, 0.94f));
            return;
          }

          if(cn1 > 0.9 && rand.chance(0.05f)){
            tile.block = OvulamBlocks.manganeseNoduleProp;
          }

          return;
        }

        float n2 = Simplex.noise2d(seed, 1, 1, 0.01f, x + rand.random(10000), y);
        float cn2 = c * n2 * (1 - Mathf.curve(cn1, 0.75f, 0.9f));

        if(cn2 > 0.75f && rand.chance(Mathf.curve(cn2, 0.8f))){
          tile.floor = Blocks.yellowStone;
        }else if(cn2 > 0.65 && rand.chance(Mathf.curve(cn2, 0.7f))){
          tile.floor = Blocks.rhyolite;
        }

      }
    }.floors(Blocks.bluemat, 2, Blocks.darksand, 1, Blocks.redmat, 2);

    deepArea = new Biome("deepArea"){
      final int maxCrystal = 8;
      final Seq<Point2> crystalTiles = new Seq<>();

      @Override
      public void begin(){
        crystalTiles.clear();
      }

      @Override
      public void pass(int x, int y, int seed, TileGen tile, Rand rand, float noise){
        float n1 = Simplex.noise2d(seed, 6, 0.8f, 0.01f, x, y);
        float c = Interps.A.apply(Mathf.curve(noise, 0.35f, 0.65f));

        if(n1 * c > 0.55f){
          tile.floor = Blocks.crystalFloor;

          if(n1 * c > 0.77f){
            crystalTiles.add(new Point2(x, y));
          }
          return;
        }

        float n2 = Simplex.noise2d(seed, 6, 0.8f, 0.01f, x + 10000, y);
        float nn2 = n2 * Interps.A.apply(Mathf.curve(noise, 0.1f, 0.3f));

        if(nn2 > 0.55f){
          tile.floor = OvulamBlocks.gasHydrateFloor;
        }else if(nn2 > 0.45f){
          tile.floor = Blocks.snow;
        }
      }

      @Override
      public void end(){
        crystalTiles.sort(p -> p.dst(Vars.world.width() / 2, Vars.world.height() / 2));

        int i = 0;
        for(Point2 tile : crystalTiles){
          if(!hasOtherCrystalNear(tile.x, tile.y)){
            Vars.world.tile(tile.x, tile.y).setBlock(OvulamBlocks.amethystCrystalCore);
            if(i++ >= maxCrystal){
              break;
            }
          }
        }
      }

      public static boolean hasOtherCrystalNear(int x, int y){
        for(int i = 0; i < 12; i++){
          for(Point2 point2 : MathUtil.getPixelCircle(i)){
            Tile tile = Vars.world.tile(x + point2.x, y + point2.y);
            if(tile != null && tile.block() == OvulamBlocks.amethystCrystalCore){
              return true;
            }
          }
        }
        return false;
      }
    }.floors(Blocks.beryllicStone, 1, Blocks.carbonStone, 1).noise(6, 0.7, 4);
  }

  public static boolean within(float a, float min, float max){
    return a >= min && a <= max;
  }
}
