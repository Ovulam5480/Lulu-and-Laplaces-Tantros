package LLL.entities.multiSegment;

import LLL.entities.gen.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import universecore.annotations.*;

//多体节单位, 自身就是体节
public interface TreeTypeModule{
  //该部位类型的子部位
  @Annotations.BindField("node")
  default IntMap<Seq<TreeTypePart>> node(){
    return null;
  }

  default void initModule(){
    node().values().toArray().each(a -> a.each(part -> {
      if(part.type == null) part.type = this;
      if(part.mirrorX){
        TreeTypePart p = part.copy();
        p.x = -p.x;
        p.x2 = -p.x2;
        p.mirrorX = false;
        a.add(p);
      }

      if(part.mirrorY){
        TreeTypePart p = part.copy();
        p.y = -p.y;
        p.y2 = -p.y2;
        p.mirrorY = false;
        a.add(p);
      }
    }));
  }

  default void loadModule(){
    node().values().toArray().each(a -> a.each(part -> part.load()));
  }

  default void addTreeRoot(Posc parent){
    node().values().toArray().each(a -> a.each(part -> {
      part.spawn(parent, this);
    }));
  }

  static void setRotation(Treec node, TreeTypePart part, Unit root){
    float partRot = Mathf.lerp(part.rotation, part.rotation2, part.partMove ? part.progress.get(node) : 0);
    float angle = Mathf.angle(node.x() - root.x, node.y() - root.y);

    node.rotation(angle + partRot);
  }

  static Vec2 getTargetPos(Treec node, TreeTypePart part, Posc root, float rootRot, Vec2 out){
    return getPartPos(part.partMove ? part.progress.get(node) : 0, part, out).rotate(rootRot - 90).add(root);
  }

  static float getInferRotation(TreeTypePart part, Unit root){
    float partRot = Mathf.lerp(part.rotation, part.rotation2, part.partMove ? 0.5f : 0);
    float angle = getPartPos(part.partMove ? 0.5f : 0, part, Tmp.v1).angle();

    return angle + partRot + root.rotation + 180f;
  }

  static Vec2 getPartPos(float progress, TreeTypePart part, Vec2 out){
    out.set(Mathf.lerp(part.x, part.x2, progress), Mathf.lerp(part.y, part.y2, progress));
    return out;
  }
}
