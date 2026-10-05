package LLL.entities.multiSegment;

import LLL.entities.gen.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.*;

@Annotations.EntityDef(Treec.class)
@Annotations.EntityComponent
public abstract class TreeComp implements Posc, Rotc, Drawc{
  Posc parent;
  TreeTypePart partType;
  TreeTypeModule module;

  //TreeManager manager;

  int serialNumber;
  public ObjectMap<TreeTypePart, Float> constructingPart = new ObjectMap<>();
  public ObjectMap<TreeTypePart, Treec> childs = new ObjectMap<>();

  public void setType(TreeTypePart part, Posc parent, TreeTypeModule module){
    this.partType = part;
    this.parent = parent;
    this.module = module;

    initChilds();
  }

  public void initChilds(){
//        if(parent instanceof Treec t){
//            manager = t.manager();
//        }else {
//            manager = new TreeManager(module, parent);
//        }

    for(int item : module.node().keys().toArray().items){
      if(item > serialNumber){
        module.node().get(item).each(part -> {
          if(part.immediatelyAdd) spawnChild(part);
          else constructingPart.put(part, 0f);
        });
        break;
      }
    }
  }

  public Treec spawnChild(TreeTypePart part){
    Treec child = part.spawn(this, module);
    childs.put(part, child);

    return child;
  }

  @Override
  public void draw(){
    Drawf.circles(x(), y(), 3);
  }

  @Override
  public void update(){
    if(parent == null) return;

    Vec2 current = Tmp.v1.set(this).sub(parent);

    float parentRot = 0;
    if(partType.rotWithParent){
      //todo instanceof Treec?
      if(parent instanceof Rotc r){
        parentRot = r.rotation();
      }else if(parent instanceof RotBlock rot){
        parentRot = rot.buildRotation();
      }
    }

    Vec2 target = TreeTypeModule.getTargetPos(self(), partType, parent, parentRot, Tmp.v2).sub(parent);

    float deltaAngle = current.angle(target);
    float absAngle = Math.abs(deltaAngle);

    if(absAngle > partType.fractureAngle){
      fracture();
    }else if(absAngle > partType.minSettingAngle){
      deltaAngle = partType.minSettingAngle * Mathf.sign(deltaAngle);
    }

    target.rotate(Mathf.lerp(absAngle, partType.minHomingAngle, partType.homingLerp) * Mathf.sign(deltaAngle)).add(parent);

    set(target);
    rotation(angleTo(parent));

    constructingPart.each((part, time) -> {
      time += Time.delta;

      if(time > part.constructTime){
        Treec t = spawnChild(part);
        //Fx.spawn.at(t.x(), t.y());

        constructingPart.remove(part);
      }else constructingPart.put(part, time);
    });
  }

  public void fracture(){

  }
}
