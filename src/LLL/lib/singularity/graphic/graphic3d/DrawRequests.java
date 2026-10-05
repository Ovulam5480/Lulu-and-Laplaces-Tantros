package LLL.lib.singularity.graphic.graphic3d;

import arc.graphics.*;

public class DrawRequests{
  public static class DrawRequest{
    public Texture texture, normalTexture, specTexture;
    public Mesh mesh;
    public int verticesOffset, verticesSize;
  }

  public static class SortedDrawRequest extends DrawRequest{
    public boolean isAlpha;
    public float dst2;
    public Runnable runTask;
  }

}
