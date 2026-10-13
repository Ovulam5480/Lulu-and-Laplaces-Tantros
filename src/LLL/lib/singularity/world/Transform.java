package LLL.lib.singularity.world;

import arc.math.geom.*;
import arc.struct.*;
import universecore.annotations.*;

import static universecore.annotations.Annotations.*;

public interface Transform{
  Seq<Transform> tmpStack = new Seq<>();

  Transform parent();

  @Annotations.BindField(value = "tempTrans", initialize = "new arc.math.geom.Mat3D()")
  default Mat3D parentTrans(){
    return null;
  }

  @Annotations.BindField("tempTrans")
  default void parentTrans(Mat3D value){
  }

  @Annotations.BindField("parTransformed")
  default boolean parTransformed(){
    return false;
  }

  @Annotations.BindField("parTransformed")
  default void parTransformed(boolean transformed){
  }

  default void updateParentTransform(){
    if(!parTransformed()){
      Seq<Transform> stack = tmpStack.clear();

      Transform curr = this;
      while(curr != null){
        stack.add(curr);

        curr = curr.parent();
      }

      for(int i = stack.size - 1; i >= 0; i--){
        Transform obj = stack.get(i);
        Transform par = obj.parent();
        if(par == null) obj.parentTrans().idt();
        else{
          Mat3D objTrn = obj.parentTrans();
          Mat3D parTrn = par.parentTrans();

          par.getTransform(objTrn)
            .translate(obj.getX(), obj.getY(), obj.getZ())
            .mulLeft(parTrn);
        }
        obj.parTransformed(true);
      }
    }
  }

  @BindField(value = "tmpQuat", initialize = "new arc.math.geom.Quat()")
  default Quat tmpQuat(){
    return null;
  }

  @BindField("tmpQuat")
  default void tmpQuat(Quat value){
  }

  @BindField("x")
  default float getX(){
    return 0;
  }

  @BindField("y")
  default float getY(){
    return 0;
  }

  @BindField("z")
  default float getZ(){
    return 0;
  }

  default Vec3 getPos(Vec3 result){
    return result.set(getX(), getY(), getZ());
  }

  @BindField("x")
  default void x(float x){
  }

  @BindField("y")
  default void y(float y){
  }

  @BindField("z")
  default void z(float z){
  }

  default void setPosition(float x, float y, float z){
    x(x);
    y(y);
    z(z);
  }

  default void setPosition(Vec3 vec3){
    setPosition(vec3.x, vec3.y, vec3.z);
  }

  default void transform(float x, float y, float z){
    setPosition(getX() + x, getY() + y, getZ() + z);
  }

  default void transform(Vec3 vec3){
    transform(vec3.x, vec3.y, vec3.z);
  }

  @BindField("eulerX")
  default float getEulerX(){
    return 0;
  }

  @BindField("eulerY")
  default float getEulerY(){
    return 0;
  }

  @BindField("eulerZ")
  default float getEulerZ(){
    return 0;
  }

  default Quat getEuler(Quat result){
    return getRotation(result);
  }

  @BindField("eulerX")
  default void eulerX(float x){
  }

  @BindField("eulerY")
  default void eulerY(float y){
  }

  @BindField("eulerZ")
  default void eulerZ(float z){
  }

  default void setEuler(float x, float y, float z){
    eulerX(x);
    eulerY(y);
    eulerZ(z);
  }

  default void setEuler(Vec3 vec3){
    setEuler(vec3.x, vec3.y, vec3.z);
  }

  default void rotate(float x, float y, float z){
    setEuler(getEulerX() + x, getEulerY() + y, getEulerZ() + z);
  }

  default void rotate(Vec3 vec3){
    rotate(vec3.x, vec3.y, vec3.z);
  }

  @BindField(value = "scaleX", initialize = "1.0f")
  default float getScaleX(){
    return 0;
  }

  @BindField(value = "scaleY", initialize = "1.0f")
  default float getScaleY(){
    return 0;
  }

  @BindField(value = "scaleZ", initialize = "1.0f")
  default float getScaleZ(){
    return 0;
  }

  default Vec3 getScale(Vec3 result){
    return result.set(getScaleX(), getScaleY(), getScaleZ());
  }

  @BindField("scaleX")
  default void scaleX(float x){
  }

  @BindField("scaleY")
  default void scaleY(float y){
  }

  @BindField("scaleZ")
  default void scaleZ(float z){
  }

  default void setScale(float x, float y, float z){
    scaleX(x);
    scaleY(y);
    scaleZ(z);
  }

  default void setScale(Vec3 vec3){
    setScale(vec3.x, vec3.y, vec3.z);
  }

  default Quat getRotation(Quat result){
    return result.setEulerAngles(getEulerY(), getEulerX(), getEulerZ());
  }

  default void setRotation(Quat quat){
    setEuler(quat.getPitch(), quat.getYaw(), quat.getRoll());
  }

  default Mat3D getTransform(Mat3D result){
    Quat q = getRotation(tmpQuat());

    return result.set(
      getX(), getY(), getZ(),
      q.x, q.y, q.z, q.w,
      getScaleX(), getScaleY(), getScaleZ()
    );
  }

  default Mat3D getAbsTransform(Mat3D result){
    if(parent() == null) return getTransform(result);

    Quat q = getRotation(tmpQuat());

    return result.set(
      getX(), getY(), getZ(),
      q.x, q.y, q.z, q.w,
      getScaleX(), getScaleY(), getScaleZ()
    ).mulLeft(parentTrans());
  }
}
