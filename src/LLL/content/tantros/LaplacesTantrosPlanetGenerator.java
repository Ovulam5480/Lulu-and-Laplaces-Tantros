package LLL.content.tantros;

import LLL.content.*;
import LLL.content.extensions.*;
import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.biomeplanet.*;
import LLL.type.resourceStacks.*;
import LLL.util.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class LaplacesTantrosPlanetGenerator extends PlanetGenerator{
  private static final int startSector = 205;
  Seq<LodeConfig> lodes = Seq.with(
    new LodeConfig(OvulamPacketTypes.lodePacket, 0.2f, Item.class, OvulamItems.manganeseNodule, 40f, Item.class, OvulamItems.cobaltNodule, 20f)
  );
  private Vec3 tmpPosition;
  private Color tmpOut;

  public QuadTree<BiomeBox> biomeTree = new QuadTree<>(new Rect(0, 0, 1, 1));
  public Vec3 lowTempDir;

  {
    baseSeed = OvulamSettings.registerInt("laplace-tantros-seed", o -> {
      baseSeed = o;
      lowTempDir = new Vec3().setToRandomDirection(rand);
    }, 0);

    lowTempDir = new Vec3().setToRandomDirection(rand);
    defaultLoadout = OvulamLoadouts.coreBackFlow;
  }

  public LaplacesTantrosPlanetGenerator add(BiomeBox biome){
    biomeTree.insert(biome);
    return this;
  }

  float rawDepth(Vec3 position){
    return Simplex.noise3d(seed, 1, 1, 1.2, position.x, position.y, position.z);
  }

  float rawTemp(Vec3 position){
    return Mathf.lerp(
      position.angle(lowTempDir) / 180,
      Simplex.noise3d(seed, 8, 0.6, 1, position.x, position.y, position.z),
      0.3f);
  }

  @Override
  public float getHeight(Vec3 position){
    return 0.5f - rawDepth(position);
  }

  //星球外观
  @Override
  public void getColor(Vec3 position, Color out){
    tmpPosition = position;
    tmpOut = out;

    if(setSectorColor(startSector, Pal.accent, 1f)
      || setSectorColor(startSector, Color.gray, 4)
      || setSectorColor(startSector, Color.grays(0.4f), 5)
      || setSectorColor(startSector, Color.grays(0.6f))){
      return;
    }

    out.set(Color.blue).lerp(Color.red, rawTemp(position));
  }


  public Biome select(Vec3 position){
    Tmp.r1.set(rawTemp(position), rawDepth(position), 0.01f, 0.01f);

    BiomeBox[] biome = new BiomeBox[1];
    while(biome[0] == null){
      biomeTree.intersect(Tmp.r1, b -> biome[0] = b);
      Tmp.r1.grow(0.05f);

      if(Tmp.r1.width > 2){
        throw new RuntimeException("Biome not found");
      }
    }

    return biome[0].biome;
  }

  @Override
  public void getEmissiveColor(Vec3 position, Color out){
    setSectorColor(startSector, Pal.accent, 1f);
    setSectorColor(startSector, Tmp.c1.set(Color.grays(0.5f)).a(0.4f), 4);
    setSectorColor(startSector, Tmp.c1.set(Color.grays(0.4f)).a(0.4f), 5);
    setSectorColor(startSector, Tmp.c1.set(Color.grays(0.6f)).a(0.4f));
  }

  public boolean setSectorColor(int sectorId, Color target){
    return setSectorColor(sectorId, target, 6f);
  }

  public boolean setSectorColor(int sectorId, Color target, float angle){
    if(tmpPosition.angle(LaplacesTantros.tantros.sectors.get(sectorId).rect.center) < angle){
      tmpOut.set(target);
      return true;
    }
    return false;
  }

  @Override
  public boolean isEmissive(){
    return true;
  }

  @Override
  public int getSectorSize(Sector sector){
    return 600;
  }

  @Override
  public void addWeather(Sector sector, Rules rules){
  }

  @Override
  public void generate(Tiles tiles, Sector sec, WorldParams params){
    this.tiles = tiles;
    this.seed = params.seedOffset + baseSeed;
    this.sector = sec;
    this.width = tiles.width;
    this.height = tiles.height;
    this.rand.setSeed(sec.id + params.seedOffset + baseSeed);

    //一个区块仅存在一种生物群系, 避免不同环境出现的冲突(例如水上沙滩和水下环境)
    Biome biome = select(sec.rect.center);
    TileGen gen = new TileGen();

    biome.begin();

    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        gen.reset();
        Vec3 position = sector.rect.project(x / (float)tiles.width, y / (float)tiles.height);

        float noise = blockNoise(biome, position);

        if(biome.walls != null) gen.block = biome.walls.get(noise);
        if(biome.floors != null) gen.floor = biome.floors.get(noise);
        if(biome.overlays != null) gen.overlay = biome.overlays.get(noise);

        biome.pass(x, y, seed, gen, rand, noise);

        tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
      }
    }

    biome.end();

    generate(tiles, params);
  }

  public void applyBiomeRule(Sector sector, Rules rules){
    select(sector.rect.center).applyRules(rules);
  }

  float blockNoise(Biome biome, Vec3 position){
    return Simplex.noise3d(seed, biome.octaves, biome.persistence, biome.scale, position.x, position.y, position.z);
  }

  @Override
  protected void generate(){
    Biome biome = select(sector.rect.center);

    //pass((x, y) -> {
    //可燃冰
//      float noise = noise(x, y, 20f, 1f);
//      if(noise > 0.96){
//        floor = OvulamBlocks.gasHydrateFloor;
//      }else if(noise > 0.95){
//        floor = Blocks.snow;
//      }
//
//      //晶体
//      if(rand.chance(0.00012)){
//        block = OvulamBlocks.amethystCrystalCore;
//        int radius = 5;
//        for(int rx = -radius; rx <= radius; rx++){
//          for(int ry = -radius; ry <= radius; ry++){
//            Tile t = tiles.get(x + rx, y + ry);
//
//            if(t != null && rand.chance(1 - (Mathf.len2(rx, ry) / Mathf.sqr(radius)) + 0.1f)){
//              t.setFloor(Blocks.crystalFloor.asFloor());
//            }
//          }
//        }
//      }

    //});

    Schematics.placeLaunchLoadout(width / 2, height / 2);
    //addAfterLoad(this::lodes);
    //vent();
  }

  public void lodes(){
    tiles.each((x, y) -> {
      if(!floor.asFloor().hasSurface()) return;

      int offsetX = x - 4, offsetY = y + 23;
      for(int i = lodes.size - 1; i >= 0; i--){
        LodeConfig config = lodes.get(i);

        if(Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 3, 0.7, (40 + i * 2))) > config.threshold
          && Math.abs(0.5f - noise(offsetX - i * 999, offsetY, 4, 1, (30 + i * 4))) > config.threshold
        ){

          Packetc packet = config.type.create(null, x * 8, y * 8);

          if(packet != null) packet.copyResourceStacks(config.resourceStacks);

          break;
        }
      }
    });
  }

  public void vent(){
    outer:
    for(Tile tile : tiles){
      var floor = tile.floor();
      if((floor == Blocks.bluemat) && rand.chance(0.0001)){
        int radius = 2;
        for(int x = -radius; x <= radius; x++){
          for(int y = -radius; y <= radius; y++){
            Tile other = tiles.get(x + tile.x, y + tile.y);
            if(other == null || (other.floor() != Blocks.bluemat) || other.block().solid){
              continue outer;
            }
          }
        }

        for(var pos : SteamVent.offsets){
          Tile other = tiles.get(pos.x + tile.x + 1, pos.y + tile.y + 1);
          other.setFloor(OvulamBlocks.hydrothermalVent.asFloor());
        }
      }
    }
  }

  public static class LodeConfig{
    public PacketType type;
    public float threshold;
    public Seq<ResourceStack<?>> resourceStacks;

    public LodeConfig(PacketType type, float threshold, Seq<ResourceStack<?>> resourceStacks){
      this.type = type;
      this.threshold = threshold;
      this.resourceStacks = resourceStacks;
    }

    public LodeConfig(PacketType type, float threshold, Object... objects){
      this(type, threshold, ResourceStack.list(objects));
    }
  }
}
