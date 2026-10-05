package LLL.lib.singularity.graphic.graphic3d;

import LLL.lib.singularity.world.*;
import arc.graphics.*;
import arc.math.geom.*;
import universecore.annotations.*;

public interface RendererObject extends GameObject{
  Mesh mesh();

  Material material();

  @Annotations.BindField(value = "boundBox", initialize = "new arc.math.geom.BoundingBox()")
  default BoundingBox bounds(){
    return null;
  }

  default BoundingBox getTransformedBounds(BoundingBox result, Mat3D tmp){
    result.set(bounds());
    Mat3D trn = getAbsTransform(tmp);
    Mat3D.prj(result.min, trn);
    Mat3D.prj(result.max, trn);
    return result;
  }

  @Annotations.BindField("renderValid")
  default boolean renderValid(){
    return false;
  }

  @Annotations.BindField("renderValid")
  default void renderValid(boolean valid){
  }

  default void renderer(){
    ShaderProgram shaderProgram = material().shader;
    material().setupData();
    shaderProgram.drawObject(this);
  }

  default int meshOffset(){
    return 0;
  }

  default int meshCount(){
    Mesh mesh = mesh();
    return mesh.indices.max() > 0 ? mesh.getNumIndices() : mesh.getNumVertices();
  }

  default int verticesPrimitive(){
    return Gl.triangles;
  }
}
