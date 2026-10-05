package LLL.entities.ai;

import LLL.content.extensions.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class SeizePayloadAI extends AIController{
  public Building dumpTarget;
  public Teamc seizeTarget;
  public boolean seizing = true;

  @Override
  public void updateMovement(){
    Payloadc payloadc = (Payloadc)unit;

    if(seizing){
      float range = payloadc.payloads().isEmpty() ? 9999 : unit.range();
      Unit cu = Units.closestEnemy(unit.team, unit.x, unit.y, range, u -> u.isGrounded() && payloadc.canPickup(u));
      Building cb = Units.findEnemyTile(unit.team, unit.x, unit.y, range, payloadc::canPickup);

      Teamc closested;

      if(cu == null && cb == null){
        seizing = false;
        return;
      }else if(cu == null){
        closested = cb;
      }else if(cb == null){
        closested = cu;
      }else{
        closested = cu.dst2(unit) < cb.dst2(unit) ? cu : cb;
      }

      if(seizeTarget == null || ((Healthc)seizeTarget).dead()){
        seizeTarget = closested;
      }else{
        moveTo(seizeTarget, 0);

        if(unit.within(seizeTarget, 2)){
          if(seizeTarget instanceof Building b){
            payloadc.pickup(b);
          }else{
            payloadc.pickup((Unit)seizeTarget);
          }
          seizeTarget = null;
        }
      }
      return;
    }

    if(dumpTarget == null){
      if(payloadc.payloads().isEmpty()){
        seizing = true;
        return;
      }

      Seq<Building> targets = Vars.indexer.getFlagged(unit.team, OvulamBlockFlags.payloadConsumer);

      if(!targets.isEmpty()){
        dumpTarget = targets.find(b -> b.acceptPayload(null, payloadc.payloads().peek()));
      }
    }else{
      moveTo(dumpTarget, 0);

      if(unit.within(dumpTarget, 2)){
        if(payloadc.tryDropPayload(payloadc.payloads().peek())){
          payloadc.payloads().pop();
          dumpTarget = null;
        }
      }
    }
  }
}
