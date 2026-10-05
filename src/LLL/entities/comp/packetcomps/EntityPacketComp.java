package LLL.entities.comp.packetcomps;

import LLL.*;
import LLL.ctype.packet.*;
import LLL.entities.*;
import LLL.entities.gen.*;
import LLL.graphics.*;
import LLL.type.resourceStacks.*;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.async.*;
import mindustry.entities.*;
import mindustry.gen.*;

@Annotations.EntityComponent
abstract class EntityPacketComp implements Packetc, Drawc, Physicsc, MetaBallManager.MetaBall{
  @Annotations.Import
  PacketType packetType;
  @Annotations.Import
  ObjectFloatMap<Object> objects = new ObjectFloatMap<>();
  @Annotations.Import
  Teamc owner;
  @Annotations.Import
  float drag, hitSize, rotation;
  @Annotations.Import
  PhysicsProcess.PhysicRef physref;

  float treadEffectTime;
  float despawnTime;

  @Override
  public void update(){
    drag = packetType.drag * floorOn().dragMultiplier;

    if(!vel().isZero(0.01f)){
      rotation = vel().angle();
    }

    treadEffectTime += Time.delta;
    if(packetType.treadEffect != null
      && !vel().isZero()
      //todo 体积越大, 间隔越少?
      && treadEffectTime > 2
      && Mathf.chance(vel().len() / packetType.treadEffectChangeSpeed)){
      Effect.floorDustAngle(packetType.treadEffect, x(), y(), vel().angle());
      treadEffectTime = 0;
    }

    if(owner == null){
      despawnTime += Time.delta;
    }else{
      despawnTime = 0;
    }

    if(despawnTime >= packetType.lifetime){
      despawn();
    }
  }

  @Override
  public void draw(){
    packetType.draw(self());

//        Draw.color(Color.acid);
//        if(owner != null){
//            Lines.line(x(), y(), owner.x(), y());
//            Lines.line(owner.x(), y(), owner.x(), owner.y());
//        }
//        Draw.reset();
  }

  @Override
  @Annotations.Replace
  public float clipSize(){
    return packetType.clipSize;
  }

  @Override
  @Annotations.Replace
  public EntityCollisions.SolidPred solidity(){
    return !(owner instanceof PacketTransporterBuilding) ? EntityCollisions::solid : (x, y) -> false;
  }

  public void setPhysref(PhysicsProcess.PhysicRef physref){
    this.physref = physref;
  }

  public int collisionLayer(){
    if(owner instanceof PacketTransporterc pc && pc.overrideCollisionLayer() <= OvulamPhysicsProcess.layers){
      return pc.overrideCollisionLayer();
    }

    return packetType.collisionLayer;
  }

  @Override
  public void add(){
    LuluMod.process.packets.add(self());
    if(physref() != null){
      LuluMod.process.physics.add(physref().body);
    }
  }

  @Override
  public void remove(){
    LuluMod.process.packets.remove(self());
  }

  @Override
  public float getRadius(){
    return packetType.size * 8 / 2f;
  }

  @Override
  public Color getColor(){
    return resources().isEmpty() ? Color.white : ((LiquidResourceStack)resources().first()).item.color;
  }
}
