package LLL.graphics;

import LLL.lib.singularity.graphic.MathRenderer.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
//import universe.graphic.MathShaderBuilder;
//import universe.graphic.expressions.Abs;
//import universe.graphic.expressions.Constant;
//import universe.graphic.expressions.Expression;
//import universe.graphic.expressions.Variable;

//import static universe.graphic.expressions.ExpressionKt.*;

public class OvulamMathRenderers{
  private static MathShader lineShader, rippleShader, parabolaShader, stripedRing, echinoideaShader;
  //private static universe.graphic.MathShader sparkleShader;
  private static float sparkleScale = 4f;

//    private static final Constant one = new Constant(1);
//    private static final Constant two = new Constant(2);
//    private static final Constant epsilon = new Constant(0.01f);

  static TextureRegion blank;

  public static void load(){
//        MathShaderBuilder builder = new MathShaderBuilder();
//
//        Expression x = vari("x");
//        Expression y = vari("y");
//
//        float n = 0.7f;
//        Constant exponent = new Constant(n);
//
//        Expression componentX = pow(new Abs(x), exponent);
//        Expression componentY = pow(new Abs(y), exponent);
//
//        builder.setFunction(plus(plus(componentX, componentY), new Constant(-1)));
//
//        sparkleShader = builder.build();
//        sparkleShader.setSclX(sparkleScale);
//        sparkleShader.setSclY(sparkleScale);
//
//        sparkleShader.setLowerBound(0.08f);
//        sparkleShader.setUpperBound(0.2f);
//        sparkleShader.setDispersion(0.04f);

    lineShader = new MathShader(1, 1,
      "float(step(arg0, arg0-y))",
      "1.0",
      "float");

    rippleShader = new MathShader(1, 1,
      "step(arg1, abs(x*x + y*y - arg0)) * 100.0",
      "sqrt(4.0*x*x + 4.0*y*y)",
      "float", "float");

    parabolaShader = new MathShader(
      "float tx = v_texCoords.x*sclX*arg1;\nfloat ty = (v_texCoords.y-0.5)*sclY*arg1;", 2, 2,
      "pow(tx*sin(arg2)-ty*cos(arg2), 2.0)-pow(sin(arg2), 2.0)*arg1*(tx-arg0*ty)",
      "sqrt("
        + "pow( 2.0*sin(arg2)*(tx*sin(arg2)-ty*cos(arg2))-arg1*pow(sin(arg2),2.0),2.0)+"
        + "pow(-2.0*cos(arg2)*(tx*sin(arg2)-ty*cos(arg2))+arg1*arg0*pow(sin(arg2),2.0),2.0)"
        + ")",
      "float", "float", "float"// 1/height len rad
    );

    echinoideaShader = new MathShader(
      "arg0+arg1*cos(arg2/tan(y/x))-arg3*pow(x*x+y*y,0.5)",
      "sqrt("
        + "pow( arg1*arg2*y*sin(arg2/tan(y/x))/(x*x+y*y)-arg3*x/pow(x*x+y*y,0.5),2.0)+"
        + "pow(-arg1*arg2*x*sin(arg2/tan(y/x))/(x*x+y*y)-arg3*y/pow(x*x+y*y,0.5),2.0)"
        + ")",
      "float", "float", "float", "float");
  }

//    public static void drawSparkle(float x, float y, float width, float height, float rotation){
//        Draw.shader(sparkleShader);
//        Draw.rect(getBlank(), x, y, width * sparkleScale, height * sparkleScale, rotation);
//        Draw.shader();
//    }

  public static void drawEchinoidea(float x, float y, float radius, float mag, float amount, float scl){
    echinoideaShader.setArg(0, radius);
    echinoideaShader.setArg(1, mag);
    echinoideaShader.setArg(2, amount);
    echinoideaShader.setArg(3, scl);

    Draw.shader(echinoideaShader);
    Draw.rect(getBlank(), x, y, (radius + mag) * 2, (radius + mag) * 2);
    Draw.shader();
  }

  public static void drawParabola(float x1, float y1, float x2, float y2, float height, float angle){
    drawParabola(x1, y1, x2, y2, height, angle, 1);
  }

  public static void drawParabola(float x1, float y1, float x2, float y2, float height, float angle, float sclY){
    float len = Mathf.len(x2 - x1, y2 - y1);

    parabolaShader.setScl(1, sclY);
    parabolaShader.setArg(0, 1 / height);
    parabolaShader.setArg(1, len);
    parabolaShader.setArg(2, Mathf.degreesToRadians * (angle + 90));//注意到90°+θ与90°-θ的图像并不对称

    Draw.shader(parabolaShader);
    Draw.rect(getBlank(), (x1 + x2) / 2f, (y1 + y2) / 2f, len, len, Angles.angle(x2, y2, x1, y1));
    Draw.shader();
  }

  public static void drawRipple(float x, float y, float radius, float width){
    rippleShader.setArg(0, radius * radius);
    rippleShader.setArg(1, width * width);

    Draw.shader(rippleShader);
    float r = radius * 2;
    Draw.rect(getBlank(), x, y, r, r);
    Draw.shader();
  }

  public static void drawLine(float x1, float y1, float x2, float y2, float width){
    lineShader.setArg(0, width / 4);
    Draw.shader(lineShader);
    Lines.stroke(width * 144);
    Lines.line(getBlank(), x1, y1, x2, y2, false);
    Draw.shader();
  }

  private static TextureRegion getBlank(){
    if(blank == null){
      Pixmap pix = new Pixmap(128, 128);
      pix.fill(Color.white);
      blank = new TextureRegion(new Texture(pix));
    }

    return blank;
  }
}
