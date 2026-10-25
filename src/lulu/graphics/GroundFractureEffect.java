package lulu.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.graphics.*;

/**
 * 在地面上绘制一次性的 Voronoi 裂纹效果。
 *
 * <p>这个效果分为两个阶段：
 * <ol>
 *   <li>生成阶段：随机生成 Voronoi 板块，并把每个板块加工成裂缝几何。</li>
 *   <li>绘制阶段：从中心向外显示裂缝，然后冷却变暗，最后整体淡出。</li>
 * </ol>
 *
 * <p>所有顶点都以效果中心为原点保存，真正绘制时再加上 {@code e.x/e.y}。
 */
public class GroundFractureEffect extends Effect{
  /** 效果的最大半径，单位是世界坐标。 */
  public final float radius;
  /** Voronoi 种子点数量，通常也决定板块数量。 */
  public final int cells;
  /** 发光裂缝的宽度。 */
  public float crackWidth = 5f;
  /** 发光裂缝外侧的深色轮廓宽度。 */
  public float outlineWidth = 2f;
  /** 裂纹从中心扩散到完整半径所需的 tick 数。 */
  public float revealDuration = 24f;
  /** 经过生命周期的多少比例后完成冷却。 */
  public float coolingEnd = 0.65f;
  /** 经过生命周期的多少比例后开始淡出。 */
  public float fadeStart = 0.75f;
  /** 复用的随机数对象；生成网格前会重新设置 seed。 */
  private static final Rand rand = new Rand();

  public static Bloom bloom = new Bloom(true);
  public static Color outlineColorFrom = Color.valueOf("08080b");
  public static Color outerColor = Color.valueOf("18181b");
  //public static Color outerColor = Pal.accent;
  public static Color outlineColorTo = Color.valueOf("202030");

  public static Vec2 tmp = new Vec2(), tmp2 = new Vec2();

  public GroundFractureEffect(float radius, float lifetime, int cells){
    super(lifetime, radius * 2f + 16f, e -> {});

    this.radius = radius;
    this.cells = cells;

    if(cells <= 1){
      throw new IllegalArgumentException("cells应当大于1");
    }

    layer = Layer.debris;
    followParent = false;
    renderer = e -> {
      if(!(e.data instanceof Mesh mesh)) return;
      //OvulamGraphics.applyBloom(bloom);

      // cooling: 0~1，表示冷却进度。
      // fade: 0~1，表示当前透明度。
      // heat: 使用 smoothstep 让颜色先亮、再平滑变暗。
      float cooling = Math.min(e.fin() / coolingEnd, 1);
      float fade = 1 - Mathf.curve(e.fin(), fadeStart, 1);
      float heat = 1f - Interp.pow2In.apply(cooling);

//      Draw.color(outlineColorFrom, fade * 0.2f);
//      float rad = Interp.pow5Out.apply(Mathf.clamp(e.fin() * 4)) * radius * 1.5f;
//      Angles.randLenVectors(e.id, 12, radius * 1.3f, (x, y) -> {
//        Draw.rect("circle-shadow", x + e.x, y + e.y, rad, rad);
//      });

      // revealRadius 使用 ease-out 曲线，从中心快速向外展开。
      float progress = Mathf.clamp(e.time / revealDuration + 0.1f);
      float revealRadius = radius * Interp.pow2Out.apply(progress);

      float baseAlpha = outerColor.a * fade;
      Tmp.c1.set(outerColor).mul(heat);
      for(Cell cell : mesh.cells){
        int size = cell.outer.size;
        for(int i = 0; i < size; i++){
          if(cell.boundaryEdges[i]) continue;
          Vec2 a = cell.outer.get(i), b = cell.outer.get((i + 1) % size);
          Vec2 c = cell.inner.get((i + 1) % size), d = cell.inner.get(i);
          Vec2 ee = cell.outline.get((i + 1) % size), f = cell.outline.get(i);

          float lerpC = getLerp(c, revealRadius);
          float lerpD = getLerp(d, revealRadius);
          float lerpE = getLerp(ee, revealRadius);
          float lerpF = getLerp(f, revealRadius);

          if(lerpC > 1 && lerpD > 1){
            continue;
          }

          Tmp.v1.set(c).add(d).add(ee).add(f).scl(0.25f).angle(cell.center);
          float angle = Angles.angle(Tmp.v1.x, Tmp.v1.y, cell.center.x, cell.center.y);

          Draw.color(Tmp.c2.set(outlineColorFrom).lerp(outlineColorTo, Angles.angleDist(angle, 45)/180f), outerColor.a * fade);
          drawCellSize(e.x, e.y, a, b, ee, f, lerpE - 0.4f, lerpF - 0.4f);

          Draw.color(Tmp.c1, baseAlpha);
          drawCellSize(e.x, e.y, a, b, c, d, lerpC, lerpD);
        }
      }

      Draw.reset();
    };
  }

  private float getLerp(Vec2 vec2, float revealRadius){
    return vec2.len() / revealRadius;
  }

  private static void drawCellSize(float x, float y, Vec2 a, Vec2 b, Vec2 c, Vec2 d, float lerpC, float lerpD){
    tmp.set(c).lerp(b, lerpC);
    tmp2.set(d).lerp(a, lerpD);

    float x1 = a.x, y1 = a.y;
    float x2 = b.x, y2 = b.y;
    float x3 = tmp.x, y3 = tmp.y;
    float x4 = tmp2.x, y4 = tmp2.y;

    if(lerpC > 1 || lerpD > 1){
      Intersector.intersectLines(a, b, tmp, tmp2, Tmp.v1);
      if(lerpC > 1){
        x2 = x3 = Tmp.v1.x;
        y2 = y3 = Tmp.v1.y;
      }else{
        x1 = x4 = Tmp.v1.x;
        y1 = y4 = Tmp.v1.y;
      }
    }

    Fill.quad(x1 + x, y1 + y, x2 + x, y2 + y, x3 + x, y3 + y, x4 + x, y4 + y);
  }

  /** 使用随机 seed 生成并播放一次裂纹效果。 */
  public void spawn(float x, float y){
    // 服务端没有渲染器，关闭特效时也不应生成绘制数据。
    if(Vars.headless || !Vars.renderer.enableEffects) return;
    spawn(x, y, Mathf.random(100000));
  }

  /** 使用指定 seed 播放效果；相同参数可以得到完全相同的裂纹布局。 */
  public void spawn(float x, float y, long seed){
    if(Vars.headless || !Vars.renderer.enableEffects) return;
    // Mesh 放入 EffectContainer.data，随后由 renderer 每帧读取。
    at(x, y, 0f, generate(seed));
  }

  /**
   * 生成一次裂纹网格。这里的计算只在效果创建时执行，不在每帧重复执行。
   */
  private Mesh generate(long seed){
    rand.setSeed(seed);
    Seq<Vec2> boundary = new Seq<>();

    // 用48边形近似最大圆形范围；随机 phase 避免每次网格方向完全一致。
    float phase = rand.nextFloat(Mathf.PI2);
    for(int i = 0; i < 48; i++){
      float angle = phase + i * Mathf.PI2 / 48f;
      boundary.add(new Vec2().trns(angle * Mathf.radiansToDegrees, radius));
    }

    // 生成 Voronoi 种子点。spacing 用来避免点彼此过近，减少极细碎的板块。
    Seq<Vec2> sites = new Seq<>();
    float spacing = radius * 0.7f / Mathf.sqrt(cells);
    for(int i = 0; i < cells; i++){
      Vec2 site = null;
      for(int attempt = 0; attempt < 40; attempt++){
        float angle = rand.nextFloat(Mathf.PI2);
        float distance = Mathf.sqrt(rand.nextFloat()) * radius;
        site = new Vec2().trns(angle * Mathf.radiansToDegrees, distance);

        boolean clear = true;
        for(Vec2 other : sites){
          if(site.within(other, spacing)){
            clear = false;
            break;
          }
        }
        if(clear) break;
      }
      sites.add(site);
    }

    // 每个 seed 对应一个 Voronoi 单元，并进一步加工成三层数据：
    // outer 是原始板块边界，inner 是向中心收缩后的裂缝内边界，
    // outline 是深色轮廓边界。
    Seq<Cell> result = new Seq<>(cells);
    for(int i = 0; i < sites.size; i++){
      Seq<Vec2> polygon = createVoronoiCell(sites.get(i), sites, boundary);
      if(polygon.size < 3) continue;

      // 用多边形顶点平均值作为局部中心，用于把边界向内收缩。
      Vec2 center = vertexAverage(polygon);
      float width = Math.max(0f, crackWidth) * (0.65f + rand.nextFloat() * 0.7f);
      Seq<Vec2> inner = createCrackInset(polygon, center, width);
      Seq<Vec2> outline = createOutlineInset(inner, center);
      boolean[] boundaryEdges = findBoundaryEdges(polygon, boundary);
      result.add(new Cell(polygon, inner, outline, center, boundaryEdges));
    }

    return new Mesh(result);
  }

  /**
   * 求出一个 seed 的 Voronoi 单元。
   *
   * <p>两个 seed 的等距线满足：
   * {@code point · (other - site) = (|other|² - |site|²) / 2}。
   * 保留 site 所在的一侧，依次裁剪后就得到该 seed 的多边形区域。
   */
  private static Seq<Vec2> createVoronoiCell(Vec2 site, Seq<Vec2> sites, Seq<Vec2> boundary){
    Seq<Vec2> polygon = new Seq<>(boundary);
    for(int i = 0; i < sites.size; i++){
      Vec2 other = sites.get(i);
      if(other == site) continue;

      // normal 指向另一 seed；offset 是两 seed 等距线在该法线方向上的位置。
      Vec2 normal = other.cpy().sub(site);
      float offset = (other.len2() - site.len2()) * 0.5f;
      polygon = clipHalfPlane(polygon, normal, offset);
      if(polygon.size < 3) break;
    }
    return polygon;
  }

  /** 返回多边形所有顶点的平均位置，作为近似几何中心。 */
  private static Vec2 vertexAverage(Seq<Vec2> polygon){
    Vec2 center = new Vec2();
    for(Vec2 point : polygon){
      center.add(point);
    }
    return center.scl(1f / polygon.size);
  }

  /** 将板块顶点向中心轻微收缩，留出的环带就是板块之间的发光裂缝。 */
  private Seq<Vec2> createCrackInset(Seq<Vec2> polygon, Vec2 center, float width){
    Seq<Vec2> inner = new Seq<>(polygon.size);
    for(Vec2 point : polygon){
      // 越靠近效果外沿，裂缝越细；内部裂缝保持接近设定宽度。
      float edge = Mathf.clamp((radius - point.len()) / (radius * 0.18f));
      // lerp 的第二个参数是插值比例；上限避免尖角或小板块被过度收缩。
      float inset = Math.min(0.28f, width * (0.15f + 0.85f * edge) / Math.max(0.001f, point.dst(center)));
      inner.add(point.cpy().lerp(center, inset));
    }
    return inner;
  }

  /** 再从裂缝边缘向板块内部收缩一层，形成紧贴发光裂缝的深色轮廓带。 */
  private Seq<Vec2> createOutlineInset(Seq<Vec2> inner, Vec2 center){
    Seq<Vec2> outline = new Seq<>(inner.size);
    for(int i = 0; i < inner.size; i++){
      Vec2 previous = inner.get((i + inner.size - 1) % inner.size);
      Vec2 point = inner.get(i), next = inner.get((i + 1) % inner.size);
      float distance = Math.min(distanceToLine(center, previous, point), distanceToLine(center, point, next));
      float inset = Math.min(0.35f, Math.max(0f, outlineWidth) / Math.max(0.001f, distance));
      outline.add(point.cpy().lerp(center, inset));
    }
    return outline;
  }

  /** 标记属于最外层圆形边界的边，避免把裁剪边误画成一圈圆形裂缝。 */
  private boolean[] findBoundaryEdges(Seq<Vec2> polygon, Seq<Vec2> boundary){
    boolean[] edges = new boolean[polygon.size];
    float tolerance = radius * 0.00001f;
    for(int i = 0; i < polygon.size; i++){
      Vec2 a = polygon.get(i), b = polygon.get((i + 1) % polygon.size);
      for(int j = 0; j < boundary.size; j++){
        Vec2 start = boundary.get(j), end = boundary.get((j + 1) % boundary.size);
        if(distanceToLine(a, start, end) <= tolerance && distanceToLine(b, start, end) <= tolerance){
          edges[i] = true;
          break;
        }
      }
    }
    return edges;
  }

  /** 点到线段所在直线的垂直距离，用于判断边是否落在外部圆边界上。 */
  private static float distanceToLine(Vec2 point, Vec2 start, Vec2 end){
    float length = start.dst(end);
    if(length == 0f) return 0f;
    return Intersector.distanceLinePoint(start, end, point) * Math.min(1f, length / 0.001f);
  }
  /**
   * 用一条直线裁剪多边形，只保留 {@code point.dot(normal) <= offset} 的一侧。
   *
   * <p>算法逐条检查多边形的边：
   * <ul>
   *   <li>两端在同一侧：只保留位于内侧的端点。</li>
   *   <li>两端分处两侧：先计算边与裁剪线的交点，再保留内侧端点。</li>
   * </ul>
   * 这就是 Voronoi 单元生成中最核心的一步。
   */
  private static Seq<Vec2> clipHalfPlane(Seq<Vec2> polygon, Vec2 normal, float offset){
    Seq<Vec2> result = new Seq<>(polygon.size + 1);
    if(polygon.isEmpty()) return result;

    // 从最后一个顶点开始，让“最后一条边到第一条边”也被检查。
    Vec2 previous = polygon.peek();
    float previousSide = previous.dot(normal) - offset;
    boolean previousInside = previousSide <= 0f;

    for(Vec2 current : polygon){
      float currentSide = current.dot(normal) - offset;
      boolean currentInside = currentSide <= 0f;

      // 一进一出或一出一进，说明这条边穿过了裁剪线。
      if(previousInside != currentInside){
        // 线性插值比例，使 previous + (current - previous) * fraction 落在裁剪线上。
        float fraction = previousSide / (previousSide - currentSide);
        result.add(previous.cpy().lerp(current, fraction));
      }

      // 当前顶点在保留侧时加入结果；原顶点不修改，交点使用 cpy() 创建副本。
      if(currentInside) result.add(current);

      previous = current;
      previousSide = currentSide;
      previousInside = currentInside;
    }
    return result;
  }

  /** 一个 Voronoi 板块的全部绘制数据。 */
  private record Cell(Seq<Vec2> outer, Seq<Vec2> inner, Seq<Vec2> outline, Vec2 center, boolean[] boundaryEdges){}

  /** 一次效果生成的完整网格。 */
  private record Mesh(Seq<Cell> cells){}
}
