package LLL.util;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;

/**
 * 基于外部噪声图的 2D Curl Noise 生成器。
 * 从灰度噪声纹理中采样作为势函数 ψ，通过有限差分计算梯度，生成无散向量场 (u, v) = (∂ψ/∂y, -∂ψ/∂x)。
 */
public class CurlNoise{
  public float scale = 1 / 8f;
  public float epsilon = 8;
  public Pixmap pixmap;
  public int width, height;

  public CurlNoise(Pixmap pixmap){
    set(pixmap);
  }

  public CurlNoise(){
    set(Core.assets.get("sprites/noiseAlpha.png", Texture.class).getTextureData().getPixmap());
  }

  public void set(Pixmap pixmap){
    this.pixmap = pixmap;
    this.width = pixmap.width;
    this.height = pixmap.height;
  }

  private float getGray(int x, int y){
    return pixmap.getA(x, y);
  }

  @SuppressWarnings("all")
  public Vec2 curlAt(float x, float y, Vec2 out){
    float psiX = (sample(x + epsilon, y) - sample(x - epsilon, y)) / (2 * epsilon);
    float psiY = (sample(x, y + epsilon) - sample(x, y - epsilon)) / (2 * epsilon);

    return out.set(psiY, -psiX);
  }

  /**
   * 采样势函数 ψ 在 (x, y) 处的值（双线性插值），返回范围 [0, 1]。
   */
  private float sample(float x, float y){
    float texX = Mathf.mod(x * scale, width);
    float texY = Mathf.mod(y * scale, height);

    int x0 = (int)texX;
    int y0 = (int)texY;
    float fx = texX - x0;
    float fy = texY - y0;

    int x1 = (x0 + 1) % width;
    int y1 = (y0 + 1) % height;

    float v00 = getGray(x0, y0);
    float v10 = getGray(x1, y0);
    float v01 = getGray(x0, y1);
    float v11 = getGray(x1, y1);

    return Mathf.lerp(Mathf.lerp(v00, v10, fx), Mathf.lerp(v01, v11, fx), fy);
  }
}