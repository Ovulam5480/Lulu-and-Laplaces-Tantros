package LLL.entities;

import LLL.*;
import LLL.entities.gen.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.async.*;
import mindustry.entities.*;
import mindustry.gen.*;
import universecore.util.handler.*;

public class OvulamPhysicsProcess extends PhysicsProcess{
  public static final int
    layers = 6,
    layer = -1,
    layerGround = 0,
    layerLegs = 1,
    layerFlying = 2,
    layerUnderwater = 3,
    layerBuildPackets = 4;

  public PhysicsWorld physics;
  public Seq<PhysicRef> refs = new Seq<>(false);
  public EntityGroup<Unit> group = Groups.unit;

  public EntityGroup<EntityPacketc> packets = LuluMod.oIndexer.entityPackets;

  @Override
  public void begin(){
    if(physics == null) return;

    boolean local = !Vars.net.client();

    //remove stale entities
    refs.removeAll(ref -> {
      if(!ref.entity.isAdded()){
        physics.remove(ref.body);
        ref.entity.physref(null);
        return true;
      }
      return false;
    });

    //find Units without bodies and assign them
    for(Unit entity : group){
      if(entity == null || entity.type == null || !entity.type.physics) continue;

      if(entity.physref == null){
        PhysicsWorld.PhysicsBody body = new PhysicsWorld.PhysicsBody();
        body.x = entity.x;
        body.y = entity.y;
        body.mass = entity.mass();
        body.radius = entity.hitSize * Vars.unitCollisionRadiusScale;

        PhysicRef ref = new PhysicRef(entity, body);
        refs.add(ref);

        entity.physref = ref;

        physics.add(body);
      }

      //save last position
      PhysicRef ref = entity.physref;

      ref.body.layer = entity.collisionLayer();
      ref.x = entity.x;
      ref.y = entity.y;
      ref.body.local = local || entity.isLocal();
    }

    for(EntityPacketc entity : packets){
      if(entity == null) continue;

      if(entity.physref() == null){
        PhysicsProcess.PhysicsWorld.PhysicsBody body = new PhysicsProcess.PhysicsWorld.PhysicsBody();
        body.x = entity.x();
        body.y = entity.y();
        body.mass = entity.mass();
        body.radius = entity.hitSize() / 2f;

        PhysicsProcess.PhysicRef ref = new PhysicsProcess.PhysicRef(entity, body);
        refs.add(ref);

        entity.setPhysref(ref);
        physics.add(body);
      }

      //save last position
      PhysicsProcess.PhysicRef ref = entity.physref();

      ref.body.layer = entity.collisionLayer();
      ref.x = entity.x();
      ref.y = entity.y();
      ref.body.local = local || entity.isLocal();
    }
  }

  @Override
  public void process(){
    if(physics == null) return;

    //get last position vectors before step
    for(PhysicRef ref : refs){
      //force set target position
      ref.body.x = ref.x;
      ref.body.y = ref.y;
      if(ref.entity instanceof EntityPacketc p){
        ref.body.layer = p.collisionLayer();
      }
    }

    physics.update();
  }

  @Override
  public void end(){
    if(physics == null) return;

    //move entities
    for(PhysicRef ref : refs){
      Physicsc entity = ref.entity;

      //move by delta
      entity.move(ref.body.x - ref.x, ref.body.y - ref.y);
    }
  }

  @Override
  public void reset(){
    if(physics != null){
      refs.clear();
      physics = null;
    }
  }

  @Override
  public void init(){
    reset();

    physics = new OvulamPhysicsWorld(Vars.world.getQuadBounds(new Rect()));
  }

  public static class OvulamPhysicsWorld extends PhysicsWorld{

    public OvulamPhysicsWorld(Rect bounds){
      super(bounds);
      QuadTree[] trees = new QuadTree[layers];

      for(int i = 0; i < layers; i++){
        trees[i] = new QuadTree(bounds);
      }

      FieldHandler.setValueDefault(this, "trees", trees);
    }
  }
}
