package LLL.graphics;

import LLL.*;
import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.*;
import mindustry.*;

import static mindustry.graphics.Shaders.*;

public class OvulamShaders{
  public static MetaBallShader metaBall;
  public static HGaussianShader hGaussian;
  public static TestShader test;

  public static void init(){
    metaBall = new MetaBallShader();
    hGaussian = new HGaussianShader();
    test = new TestShader();
  }

  public static class TestShader extends OvulamFragShader{
    public float threshold = 0.3f;
    public float softness = 0f;
    public float intensity = 16f;
    public float radius = 128f;
    public int sampler = 64;

    public boolean zoom = true;

    public TestShader(){
      super("test", false);
    }

    @Override
    public void apply(){
      int samplerZoom = Math.min(64, (int)(sampler * (zoom ? Vars.renderer.camerascale : 1)));

      setUniformf("u_sampler", samplerZoom);
      setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
      setUniformf("u_threshold", threshold);
      setUniformf("u_softness", softness);
      setUniformf("u_intensity", intensity);
      setUniformf("u_radius", radius * (zoom ? Vars.renderer.camerascale : 1));
    }
  }

  /// ////////////////////////////////////////////////////////

  public static class HGaussianShader extends OvulamFragShader{
    public float variance = 16;

    public HGaussianShader(){
      super("hGaussian", false);
    }

    @Override
    public void apply(){
      setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());

      float coe1 = 1 / (Mathf.sqrt(2 * Mathf.PI) * variance);
      float coe2 = -0.5f / Mathf.sqr(variance);
      setUniformf("u_coe1", coe1);
      setUniformf("u_coe2", coe2);

      int bound = (int)(variance * 3);
      setUniformi("u_bound", bound);

      float normalization = 0;
      for(int i = -bound; i <= bound; i++){
        normalization += coe1 * Mathf.pow(Mathf.E, (coe2 * i * i));
      }
      normalization = 1 / normalization;
      setUniformf("u_normalization", normalization);
    }
  }

  /// ////////////////////////////////////////////////////////
  public static class MetaBallShader extends OvulamFragShader{
    public float[] metaBallX = new float[1024];
    public float[] metaBallY = new float[1024];
    public float[] metaBallRadius = new float[1024];
    public float[] metaBallColor = new float[1024];

    public int metaBallCount;

    public MetaBallShader(){
      super("metaball", false);
    }

    public void apply(){
      setUniformi("metaBallCount", metaBallCount);

      setUniform1fv("metaBallX", metaBallX, 0, metaBallCount);
      setUniform1fv("metaBallY", metaBallY, 0, metaBallCount);
      setUniform1fv("metaBallRadius", metaBallRadius, 0, metaBallCount);
      setUniform4fv("metaBallColor", metaBallColor, 0, metaBallCount);

      setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
    }
  }

  /// ////////////////////////////////////////////////////////
  public static class OvulamFragShader extends Shader{
    public static Texture loadTexture(String name){
      Texture t = new Texture(Vars.mods.getMod(LuluMod.class).root.child("textures").child(name + ".png"));
      t.setFilter(Texture.TextureFilter.linear);
      t.setWrap(Texture.TextureWrap.repeat);

      return t;
    }

    public OvulamFragShader(String frag, boolean isDefault){
      super(getShaderFi(isDefault ? "default.vert" : "screenspace.vert"), getModShaderFi(frag));
    }

    public static Fi getModShaderFi(String file){
      return Vars.mods.getMod(LuluMod.class).root.child("shaders").child(file + ".frag");
    }
  }
}
