package LLL.lib.singularity.graphic.graphic3d.shaders;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.graphic.graphic3d.*;
import arc.graphics.*;
import arc.graphics.gl.*;

public class SolarShaderProgram extends ForwardRenderShaderProgram{
  private static final VertexAttribute[] meshFormat = new VertexAttribute[]{
    VertexAttribute.position3,
    VertexAttribute.normal,
    VertexAttribute.color
  };

  @Override
  protected Shader setupShader(){
    return SglShaders.solar;
  }

  @Override
  protected void applyShader(Shader shader){
  }

  @Override
  protected VertexAttribute[] meshFormat(){
    return meshFormat;
  }
}
