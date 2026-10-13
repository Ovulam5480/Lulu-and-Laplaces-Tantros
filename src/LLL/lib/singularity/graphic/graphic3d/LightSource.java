package LLL.lib.singularity.graphic.graphic3d;

import LLL.lib.singularity.world.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import universecore.annotations.*;

public interface LightSource extends GameObject{
  @Annotations.BindField(value = "lightColor", initialize = "arc.graphics.Color.white")
  default Color getLightColor(){
    return null;
  }

  @Annotations.BindField("lightColor")
  default void lightColor(Color color){
  }

  @Annotations.BindField(value = "lightRadius", initialize = "400f")
  default float getLightRadius(){
    return 0f;
  }

  @Annotations.BindField("lightRadius")
  default void lightRadius(float radius){
  }

  @Annotations.BindField(value = "lightAttenuation", initialize = "3.8f")
  default float getLightAttenuation(){
    return 0f;
  }

  @Annotations.BindField("lightAttenuation")
  default void lightAttenuation(float attenuation){
  }

  default void apply(Shader shader, int off){
    shader.setUniformf("u_lightSources[" + off + "].position", getX(), getY(), getZ());
    shader.setUniformf("u_lightSources[" + off + "].color", getLightColor());
    shader.setUniformf("u_lightSources[" + off + "].radius", getLightRadius());
    shader.setUniformf("u_lightSources[" + off + "].attenuation", getLightAttenuation());
  }
}
