package lulu.type.biomeplanet;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import lulu.ctype.*;
import lulu.ctype.packet.*;
import lulu.type.resourceStacks.*;
import lulu.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.game.*;
import mindustry.world.*;

/**
 * 数据驱动的生物群系定义。
 * <p>
 * 对应需求：
 * - 温度/深度分段：minTemp~maxTemp、minDepth~maxDepth 表示该群系占据的温度与深度区间（0~1）。
 * - 噪声值方块对应数组：floors/walls/overlays 是"某个噪声值 -> 方块"的映射表，
 * 运行时用归一化噪声值 n∈[0,1) 取下标 idx = min((int)(n * arr.length), arr.length-1)。
 * - 变种：variants 为可选变种群系列表，由"额外的噪声值"选定（见 BiomePlanetGenerator）。
 */
public class Biome extends UnlockableContent{
  public int env = 0;
  /**
   * 噪声值 -> floor 方块数组。
   */
  public BiomeBlockData floors;
  /**
   * 可选：噪声值 -> wall 方块数组；为空时使用 floor.asFloor().wall。
   */
  public BiomeBlockData walls;
  /**
   * 可选：噪声值 -> overlay 方块数组（矿/植被等）。
   */
  public BiomeBlockData overlays;
  /**
   * 可选变种：由额外噪声值从数组中选定一个。
   */
  public Biome[] variants;
  /**
   * 倍频层数, 越大 边缘的小型碎裂细节越多
   */
  public double octaves = 4;
  /**
   * 频率衰减, 越大 后叠加的层倍率越低
   */
  public double persistence = 0.6f;
  /**
   * 噪声尺度缩放, 越大 代表着xyz的变化越剧烈
   */
  public double scale = 4f;

  public Biome(String name){
    super(name);
  }

  public void begin(){
  }

  public void end(){
  }


  public Biome walls(Object... walls){
    this.walls = new BiomeBlockData().setData(walls);
    return this;
  }

  public Biome floors(Object... floors){
    this.floors = new BiomeBlockData().setData(floors);
    return this;
  }

  public Biome overlays(Object... overlays){
    this.overlays = new BiomeBlockData().setData(overlays);
    return this;
  }

  public Biome env(int env){
    this.env = env;
    return this;
  }

  public Biome variants(Biome... variants){
    this.variants = variants;
    return this;
  }

  public Biome octaves(double octaves){
    this.octaves = octaves;
    return this;
  }

  public Biome persistence(double persistence){
    this.persistence = persistence;
    return this;
  }

  public Biome scale(double scale){
    this.scale = scale;
    return this;
  }

  public Biome noise(double octaves, double persistence, double scale){
    this.octaves = octaves;
    this.persistence = persistence;
    this.scale = scale;
    return this;
  }

  public void lodes(PacketType lodeType, Seq<ResourceStack<?>> lodeResources, int tileX, int tileY, float multi){
    lodeType.create(null, tileX * 8, tileY * 8).copyResourceStacks(lodeResources, multi);
  }

  public void pass(int x, int y, int seed, TileGen tile, Rand rand, float noise){

  }

  public static float curve(float f, float offset) {
    return (f - offset) / (1.0F - offset);
  }

  public static float curve(float f, float from, float to) {
    return (f - from) / (to - from);
  }

  public static float curve(float f, float from, float to, Interp interp) {
    return interp.apply((f - from) / (to - from));
  }

  public static boolean hasTileNear(int x, int y, int radius, Boolf<Tile> cons){
    for(int i = 0; i < radius; i++){
      for(Point2 point2 : MathUtil.getPixelCircle(i)){
        Tile tile = Vars.world.tile(x + point2.x, y + point2.y);
        if(tile != null && cons.get(tile)){
          return true;
        }
      }
    }
    return false;
  }

  public void applyRules(Rules rules){

  }

  @Override
  public ContentType getContentType(){
    return OvulamContentType.biome.value;
  }
}
