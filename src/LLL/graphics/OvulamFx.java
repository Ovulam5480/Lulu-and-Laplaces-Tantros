package LLL.graphics;

import LLL.lib.singularity.graphic.*;
import LLL.util.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;

public class OvulamFx{
  private static final Bezier<Vec2> bezier = new Bezier<>();
  private static final Rand rand = new Rand();
  public static Bloom bloom = new Bloom(true);
  public static Interp pow20Out = new Interp.PowOut(20);
  public static Vec2[] vec2s = {Tmp.v1, Tmp.v2, Tmp.v3, Tmp.v4};
  public static Vec2 tmp = new Vec2();

  static{
    Events.run(EventType.Trigger.preDraw, () -> {
      bloom.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
      bloom.blurPasses = settings.getInt("bloomblur", 1);
      bloom.resize(Core.graphics.getWidth(), Core.graphics.getHeight(), (int)Scl.scl(4));
//      bloom.capture();
//      bloom.capturePause();
    });

    Events.run(EventType.Trigger.drawOver, () -> {
//      bloom.render();
    });
  }

  public static Effect coreBackflowCharge = new OvulamEffect(40f, e -> {
    if(e.data instanceof OvulamTrail t){
      if(Time.delta > 0 && !Vars.state.isPaused()){
        rand.setSeed(e.id);

        float angle = rand.nextFloat() * 360;
        for(int i = 0; i < vec2s.length; i++){
          vec2s[i].trns(angle + rand.range(20f), (3 - i) * 400);
        }

        bezier.set(vec2s);

        bezier.valueAt(tmp, Math.min(1, e.fin() * (1 + t.length * 2 / e.lifetime)));
        t.update(tmp.x + e.x, tmp.y + e.y);
      }

      bloom.capture();
      t.draw(e.color);
      bloom.render();
    }
  }){{
    layer = Layer.space + 1;
  }};

  public static Effect coreBackflowChargeFinished = new OvulamEffect(15f, e -> {
    bloom.capture();
    Draw.color(Pal.accent, e.fout());
    Lines.stroke(40 * e.fout());
    Lines.circle(e.x, e.y, 300 * Interp.pow2Out.apply(e.fin()));
    bloom.render();
  });

  public static Effect coreBackflowStartMove = new OvulamEffect(50f, e -> {
    bloom.capture();
    Draw.color(Pal.accent, e.fout());
    Lines.stroke(70 * e.fout() * e.fin());

    float rad = 1000 * e.fin();

    Tmp.v1.trns(e.rotation, 1).inv();

    for(int i = 1; i <= 3; i++){
      Tmp.v1.setLength(e.fin() * 800);
      Lines.ellipse(40, e.x + Tmp.v1.x * (1 + i), e.y + Tmp.v1.y * (1 + i), rad * i, rad * i * 0.7f, e.rotation + 90);
    }
    bloom.render();
  });

  public static Effect coreBackflowMove = new OvulamEffect(50f, e -> {
    bloom.capture();
    color(Pal.accent);
    Lines.stroke((1 + e.fout()) * 8);
    float spread = 49f;

    rand.setSeed(e.id);
    for(int i = 0; i < 20; i++){
      float ang = e.rotation + rand.range(17f);
      tmp.trns(ang, rand.random(e.fin() * 1000f));
      Lines.lineAngle(e.x + tmp.x + rand.range(spread), e.y + tmp.y + rand.range(spread), ang, e.fout() * 5f * rand.random(1f) + 1f);
    }
    bloom.render();
  });

  public static Effect launchTantros = new OvulamEffect(20f, e -> {
    if((Vars.state.rules.env & Env.underwater) == 0){
      color(Pal.command);
      stroke(e.fout() * 2f);
      Lines.circle(e.x, e.y, 4f + e.finpow() * 120f);
    }else{
      color(Liquids.water.color);
      stroke(e.fout() * 2.5f);
      Lines.circle(e.x, e.y, 4f + e.finpow() * 40f);
    }
  });

  public static Effect bezierTrail = new OvulamEffect(240f, 180f, e -> {
    if(e.data instanceof OvulamTrail t){
      if(Time.delta > 0 && !Vars.state.isPaused()){
        rand.setSeed(e.id);
        Tmp.v1.setToRandomDirection(rand).setLength(160);
        Tmp.v2.setToRandomDirection(rand).setLength(80);
        Tmp.v3.setZero();
        bezier.set(Tmp.v1, Tmp.v2, Tmp.v3);

        bezier.valueAt(Tmp.v4, Math.min(1, e.fin() * (1 + t.length * 2 / e.lifetime)));
        t.update(Tmp.v4.x + e.x, Tmp.v4.y + e.y);
      }
      t.draw(e.color);
    }
  });

  public static Effect absorbPayloadNearby = new Effect(180, e -> {
    Draw.color(Pal.accent);
    Fill.square(e.x, e.y, e.rotation * 8 / 2 * Interp.pow2In.apply(e.fout()));
  });

  public static Effect chargeUpSparkle = new Effect(240, e -> {
    Draw.draw(Layer.flyingUnit, () -> {
      Draw.mixcol(Color.yellow, Color.red, Interp.pow2Out.apply(e.fin()));
      Draw.alpha(0.6f + e.fin() * 0.4f);

      Draw.blend(Blending.additive);

      float t = 0.4f;
      float t2 = 0.6f;
      float interpY =
        e.fin() < t ? Interp.pow2Out.apply(e.fin() / t) :
          e.fin() < t2 ? 1 : Interp.pow2Out.apply((1 - e.fin()) / (1 - t2));

      //OvulamMathRenderers.drawSparkle(e.x, e.y, e.fin() * 80, interpY * 8, 0);
    });
  });

  public static Effect LulusSeparatorLanding = new OvulamEffect(240f, 100f, e -> {
    float fin = e.fin();
    float fout = 1 - fin;
    float x = e.x, y = e.y;

    Draw.color(Pal.accent);

    Draw.alpha(0.6f);
    float roundRadius = 400 * Interp.pow10Out.apply(fin);
    SglDraw.gradientCircle(x, y, roundRadius, -roundRadius * (1 - Interp.pow5In.apply(fin)) * 2 / 3f, 0);

    Draw.alpha(1);
    Lines.stroke(16 * fout);
    Lines.circle(x, y, roundRadius);

    float strokeScl = 1 - Interp.pow2In.apply(fin);
    Lines.stroke(4 * strokeScl);
    Angles.randLenVectors(e.id, 80, 160, 480, (ax, ay) -> {
      Lines.lineAngleCenter(x + ax * Interp.pow10Out.apply(fin), y + ay * Interp.pow10Out.apply(fin), Mathf.angle(ax, ay), 16 * strokeScl);
    });

    MathUtil.stratifiedRandomSampling(e.id, 18, roundRadius, 17f, (ax, ay) -> {
      Drawf.tri(x + ax, y + ay, 40, 400 * (1 - Interp.pow2Out.apply(fin)) * Interp.pow3Out.apply(fin), Mathf.angle(-ax, -ay));
    });

    Draw.reset();
  }).setTimeDelta(e -> e.fin() < 0.02f ? 0.06f * (1.4f - (e.fin() / 0.02f)) : 1)
    .setZoom(e -> e.fin() > 0.02f ? 1.55f : (6 + (Interp.pow2In.apply(e.fin()) * 6000)));

  public static Effect neoplasmPulse = new OvulamEffect(10f, e -> {
    float fin = e.fin();

    mixcol(Pal.neoplasmMid, Color.white, fin);
    alpha(1 - Interp.pow4In.apply(fin));
    Fill.square(e.x, e.y, 4);
  });

//    destroyTitanBlock = new Effect(200f, e -> {
//        if (!(e.data instanceof Block block)) return;
//
//        int index = Mathf.floor(e.time / 2f);
//        float progress = e.time / 2f - index;
//
//        Angles.randLenVectors(e.id + index, 1, 12, ((x1, y1) ->
//                Angles.randLenVectors(e.id + index + 1, 1, 12, (x2, y2) -> {
//                    float rx = e.x + Mathf.lerp(x1, x2, progress) * OvulamMath.fparabola(e.fin());
//                    float ry = e.y + Mathf.lerp(y1, y2, progress) * OvulamMath.fparabola(e.fin());
//
//                    Drawf.squareShadow(rx, ry, block.size * 8 * 1.85f, e.fout());
//
//                    Draw.alpha(e.foutpow());
//                    Draw.rect(block.fullIcon, rx, ry);
//                    Draw.reset();
//
//                    Draw.mixcol(Color.white, e.foutpow());
//                    Draw.alpha(e.foutpow());
//                    Draw.rect(block.fullIcon, e.x, e.y);
//
//                    Draw.reset();
//                })));
//    }),

  //    public int 数量 = 6;
//    public float 特效粒子的范围 = 120f;
//    public float 特效粒子外径 = 120;
//    //虽然规定内外径，但是并没有内径必须比外径小的必要
//    public float 特效粒子内径 = 12;
//    //这里填X角星,五角星就填5，四角星就填4
//    public int X角星 = 16;
//
//    public FloatSeq 星星图形(float x, float y, float out, float in, int side, float rotation){
//        FloatSeq floatSeq = new FloatSeq(side * 4 + 4);
//        floatSeq.add(x, y);
//
//        for (int i = 0; i < side; i++){
//            float pointRotation = rotation + 360f / side * i;
//            floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * pointRotation)),
//                    y + (float)(out * Math.sin(Mathf.degreesToRadians * pointRotation)));
//            floatSeq.add((float) (x + in * Math.cos(Mathf.degreesToRadians * (pointRotation + 180f / side))),
//                    y + (float)(in * Math.sin(Mathf.degreesToRadians * (pointRotation + 180f / side))));
//        }
//
//        floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * rotation)),
//                y + (float)(out * Math.sin(Mathf.degreesToRadians * rotation)));
//
//        return floatSeq;
//    }
//
//    public Effect 四角星 = new Effect(120, e -> {
//        Draw.color(Color.pink);
//        randLenVectors(e.id, 数量, 特效粒子的范围, (x, y) -> {
//            float angle = Mathf.angle(x, y);
//            Fill.poly(星星图形(e.finpow() * x + e.x, e.finpow() * y + e.y,
//                    特效粒子外径 * (1 - Mathf.sqr(1 - e.fslope())),
//                    特效粒子内径 * (1 - Mathf.sqr(1 - e.fslope())), X角星, angle));
//        });
//    });
}
