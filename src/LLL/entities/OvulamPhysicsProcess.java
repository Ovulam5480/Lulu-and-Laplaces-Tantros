package LLL.entities;

import LLL.*;
import LLL.entities.gen.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.async.*;
import mindustry.entities.*;
import mindustry.gen.*;

public class OvulamPhysicsProcess extends PhysicsProcess{
  public static final int
    layers = 6,
    layer = -1,
    layerGround = 0,
    layerLegs = 1,
    layerFlying = 2,
    layerUnderwater = 3,
    layerBuildPackets = 4;

  private PhysicsWorld[] physics;
  private final Seq<BodyRef> refs = new Seq<>(false);
  private final ObjectMap<Physicsc, BodyRef> references = new ObjectMap<>();
  public EntityGroup<Unit> group = Groups.unit;
  public EntityGroup<EntityPacketc> packets = LuluMod.oIndexer.entityPackets;

  @Override
  public void add(Unit unit){
    //Bodies are registered in begin(), after the previous asynchronous step finishes.
  }

  @Override
  public boolean shouldProcess(){
    return physics != null;
  }

  @Override
  public void begin(){
    if(physics == null) return;

    refs.removeAll(ref -> {
      if(!ref.entity.isAdded() || ref.entity instanceof Unit unit && (unit.type == null || !unit.type.physics)){
        if(ref.lastLayer >= 0) physics[ref.lastLayer].remove(ref.body);
        references.remove(ref.entity);
        ref.entity.hasPhysicsRef(false);
        return true;
      }
      return false;
    });

    for(Unit entity : group){
      if(entity.type != null && entity.type.physics){
        register(entity);
      }
    }
    for(EntityPacketc entity : packets){
      register(entity);
    }

    boolean local = !Vars.net.client();
    for(BodyRef ref : refs){
      Physicsc entity = ref.entity;
      int nextLayer = collisionLayer(entity);
      if(nextLayer != ref.lastLayer){
        if(ref.lastLayer >= 0) physics[ref.lastLayer].remove(ref.body);
        ref.lastLayer = nextLayer;
        if(nextLayer >= 0) physics[nextLayer].add(ref.body);
      }
      ref.startX = ref.body.x = entity.x();
      ref.startY = ref.body.y = entity.y();
      ref.body.mass = entity.mass();
      ref.body.radius = entity.hitSize() * (entity instanceof Unit ? Vars.unitCollisionRadiusScale : 0.5f);
      ref.body.local = local || entity.isLocal();
    }
  }

  private void register(Physicsc entity){
    if(references.containsKey(entity)) return;
    BodyRef ref = new BodyRef(entity);
    references.put(entity, ref);
    refs.add(ref);
    entity.hasPhysicsRef(true);
  }

  private int collisionLayer(Physicsc entity){
    int value = entity instanceof Unit unit ? unit.collisionLayer() : ((EntityPacketc)entity).collisionLayer();
    return value >= 0 && value < layers ? value : -1;
  }

  @Override
  public void process(){
    if(physics == null) return;
    for(PhysicsWorld world : physics){
      world.update();
    }
  }

  @Override
  public void end(){
    if(physics == null) return;
    for(BodyRef ref : refs){
      if(ref.entity.isAdded()){
        ref.entity.move(ref.body.x - ref.startX, ref.body.y - ref.startY);
      }
    }
  }

  @Override
  public void reset(){
    for(BodyRef ref : refs){
      ref.entity.hasPhysicsRef(false);
    }
    refs.clear();
    references.clear();
    physics = null;
  }

  @Override
  public void init(){
    reset();
    Rect bounds = Vars.world.getQuadBounds(new Rect());
    physics = new PhysicsWorld[layers];
    for(int i = 0; i < layers; i++){
      physics[i] = new PhysicsWorld(bounds);
    }
  }

  private static class BodyRef{
    final Physicsc entity;
    final PhysicsWorld.PhysicsBody body = new PhysicsWorld.PhysicsBody();
    float startX, startY;
    int lastLayer = -1;

    BodyRef(Physicsc entity){
      this.entity = entity;
    }
  }
}
