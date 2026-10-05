package LLL;

import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.gen.*;
import mindustry.input.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class OvulamDesktopControl extends DesktopInput{
//    public final KeyBind bindPacket = KeyBind.add("bind-packet", KeyCode.leftBracket, "LLL");
//    public final KeyBind unbindPacket = KeyBind.add("unbind-packet", KeyCode.rightBracket, "LLL");

  public long lastPacketKeyTapMillis = 0;
  public long lastPacketKeyHoldMillis = 0;

  //todo Call
  public void tryBindPacketRound(){
    Unit unit = player.unit();
    if(!(unit instanceof PacketUnitc pack) || pack.speed() / pack.type().speed < 0.01f) return;

    float radius = pack.typeAs().packetBindRange;
    boolean[] hasBind = {false};
    LuluMod.oIndexer.entityPackets.intersect(unit.x - radius / 2, unit.y - radius / 2, radius, radius, p -> {
      if(!hasBind[0] && p.owner() == null && p.dst(pack) <= radius){
        PacketEntry entry = PacketEntry.create(p);
        if(pack.acceptPacket(entry, null)){
          pack.handlePacket(entry, null);
          p.owner(pack);

          hasBind[0] = true;
        }else{
          Pools.free(entry);
        }
      }

    });
  }

  public void tryUnbindPacket(){
    Unit unit = player.unit();
    if(!(unit instanceof PacketUnitc pack) || pack.packets().isEmpty()) return;

    pack.throwPacket(pack.packets().first());
  }

  protected void updateMovement(Unit unit){
    boolean omni = unit.type.omniMovement;

    float speed = unit.speed();
    float xa = Core.input.axis(Binding.moveX);
    float ya = Core.input.axis(Binding.moveY);
    boolean boosted = (unit instanceof Mechc && unit.isFlying());

    if(settings.getBool("detach-camera")){
      Vec2 targetPos = camera.position;

      movement.set(targetPos).sub(player).limit(speed);

      if(player.within(targetPos, 15f)){
        movement.setZero();
        unit.vel.approachDelta(Vec2.ZERO, unit.speed() * unit.type().accel / 2f);
      }
    }else{
      movement.set(xa, ya).nor().scl(speed);
      if(Core.input.keyDown(Binding.mouseMove)){
        movement.add(input.mouseWorld().sub(player).scl(1f / 25f * speed)).limit(speed);
      }
    }

    float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
    boolean aimCursor = omni && player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted;

    if(aimCursor){
      unit.lookAt(mouseAngle);
    }else{
      unit.lookAt(unit.prefRotation());
    }

    unit.movePref(movement);

    unit.aim(Core.input.mouseWorld());
    unit.controlWeapons(true, player.shooting && !boosted);

    player.boosting = Core.input.keyDown(Binding.boost);
    player.mouseX = unit.aimX();
    player.mouseY = unit.aimY();

    //update payload input
    if(unit instanceof Payloadc){
      if(Core.input.keyTap(Binding.pickupCargo)){
        tryPickupPayload();
        lastPayloadKeyTapMillis = Time.millis();
      }

      if(Core.input.keyDown(Binding.pickupCargo)
        && Time.timeSinceMillis(lastPayloadKeyHoldMillis) > 20
        && Time.timeSinceMillis(lastPayloadKeyTapMillis) > 200){
        tryPickupPayload();
        lastPayloadKeyHoldMillis = Time.millis();
      }

      if(Core.input.keyTap(Binding.dropCargo)){
        tryDropPayload();
        lastPayloadKeyTapMillis = Time.millis();
      }

      if(Core.input.keyDown(Binding.dropCargo)
        && Time.timeSinceMillis(lastPayloadKeyHoldMillis) > 20
        && Time.timeSinceMillis(lastPayloadKeyTapMillis) > 200){
        tryDropPayload();
        lastPayloadKeyHoldMillis = Time.millis();
      }
    }

    //新增部分
    if(unit instanceof PacketUnitc){
      if(Core.input.keyTap(Binding.pickupCargo)){
        tryBindPacketRound();
        lastPacketKeyTapMillis = Time.millis();
      }

      if(Core.input.keyDown(Binding.pickupCargo)
        && Time.timeSinceMillis(lastPacketKeyHoldMillis) > 20
        && Time.timeSinceMillis(lastPacketKeyTapMillis) > 200){
        tryBindPacketRound();
        lastPacketKeyHoldMillis = Time.millis();
      }

      if(Core.input.keyTap(Binding.dropCargo)){
        tryUnbindPacket();
        lastPacketKeyTapMillis = Time.millis();
      }

      if(Core.input.keyDown(Binding.dropCargo)
        && Time.timeSinceMillis(lastPacketKeyHoldMillis) > 20
        && Time.timeSinceMillis(lastPacketKeyTapMillis) > 200){
        tryUnbindPacket();
        lastPacketKeyHoldMillis = Time.millis();
      }
    }
  }

}
