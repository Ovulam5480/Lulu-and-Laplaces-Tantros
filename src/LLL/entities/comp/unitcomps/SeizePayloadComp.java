package LLL.entities.comp.unitcomps;

import LLL.content.*;
import LLL.entities.gen.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

@Annotations.EntityComponent
abstract class SeizePayloadComp implements Payloadc{
  @Annotations.Import
  float x, y;
  @Annotations.Import
  UnitType type;
  @Annotations.Import
  Seq<Payload> payloads;

  @Annotations.Replace
  @Override
  public boolean canPickup(Unit unit){
    return type.pickupUnits && payloadUsed() + unit.hitSize * unit.hitSize <= type.payloadCapacity + 0.001f && unit.isAI() && unit.type.allowedInPayloads;
  }

  @Annotations.Replace
  @Override
  public boolean canPickup(Building build){
    return payloadUsed() + build.block.size * build.block.size * Vars.tilesize * Vars.tilesize <= type.payloadCapacity + 0.001f && build.canPickup();
  }

  @Override
  public void update(){
    payloads.each(p -> {
      if(p instanceof BuildPayload b){
        b.build.updatePayload(self(), null);
      }else{
        Unit unit = ((UnitPayload)p).unit;
        unit.type.updatePayload(unit, self(), null);
      }
    });
  }

  @Annotations.Replace
  @Override
  public boolean tryDropPayload(Payload payload){
    Tile on = tileOn();

    //clear removed state of unit so it can be synced
    if(Vars.net.client() && payload instanceof UnitPayload u){
      Vars.netClient.clearRemovedEntity(u.unit.id);
    }

    //drop off payload on an acceptor if possible
    if(on != null && on.build != null && on.build.acceptPayload(on.build, payload)){
      Fx.unitDrop.at(on.build);
      on.build.handlePayload(on.build, payload);
      return true;
    }

    if(payload instanceof BuildPayload b){
      return dropBlock(b);
    }else if(payload instanceof UnitPayload p){
      return dropUnit(p);
    }
    return false;
  }

  @Annotations.Replace
  @Override
  public boolean canDropPayload(){
    if(payloads.isEmpty()) return false;

    Payload payload = payloads.peek();
    Tile on = tileOn();

    if(on != null && on.build != null && on.build.acceptPayload(on.build, payload)) return true;

    if(payload instanceof BuildPayload b){
      Building tile = b.build;
      int tx = World.toTile(x - tile.block.offset), ty = World.toTile(y - tile.block.offset);
      on = Vars.world.tile(tx, ty);
      return on != null && Build.validPlace(tile.block, tile.team, tx, ty, tile.rotation, false);
    }else if(payload instanceof UnitPayload p){
      var u = p.unit;
      return !(!u.canPass(World.toTile(x + Tmp.v1.x), World.toTile(y + Tmp.v1.y)) || Units.count(x, y, u.physicSize(), o -> o.isGrounded() && o.hitSize > 14f) > 1);
    }
    return false;
  }

  @Annotations.MethodPriority(-1)
  @Override
  public void remove(){
    payloads.each(payload -> {
      if(payload instanceof BuildPayload b){
        InertiaDamagec unit = (InertiaDamagec)OvulamUnitTypes.inertiaBuildUnit.create(b.build.team);
        unit.set(b.build);
        unit.tile(b.build);
        unit.add();
      }else{
        payload.dump();
      }
    });
  }
}
