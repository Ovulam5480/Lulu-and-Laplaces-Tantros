package LLL.world.buildComp;

import LLL.world.modules.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import ent.anno.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;
import static mindustry.world.blocks.payloads.PayloadBlock.*;

@Annotations.EntityComponent
abstract class PayloadBuildComp implements Buildingc{
  private static final PayloadSeq tempPayloads = new PayloadSeq();
  private static final Vec2 tempVec = new Vec2();

  public transient int payloadCapacity;
  public transient float payloadSpeed;
  public transient float payloadRotateSpeed;

  public transient int blendBit;
  public transient boolean carried;

  public PayloadModule payloads = new PayloadModule();
  public Payload inputting;
  public Payload outputting;
  public float stackAlpha;
  public boolean outputLocking;

  @Annotations.Import
  float x, y, rotation;
  @Annotations.Import
  Block block;
  @Annotations.Import
  Team team;

  public float handleOutputPayload(){
    if(outputting != null){
      Vec2 outputVec = outputtingOffset();
      outputting.set(
        Mathf.approachDelta(outputting.x(), x + outputVec.x, payloadSpeed),
        Mathf.approachDelta(outputting.y(), y + outputVec.y, payloadSpeed),
        Angles.moveToward(outputting.rotation(), Tmp.v1.set(outputVec).add(self()).sub(outputting).angle(), payloadRotateSpeed * Time.delta)
      );

      return 1 - Mathf.clamp(Mathf.len(x + outputVec.x - outputting.x(), y + outputVec.y - outputting.y()) / outputVec.len());
    }

    return 0;
  }


  public float handleInputPayload(){
    if(inputting != null){
      inputting.set(
        Mathf.approachDelta(inputting.x(), x, payloadSpeed),
        Mathf.approachDelta(inputting.y(), y, payloadSpeed),
        Mathf.approachDelta(inputting.rotation(), rotation * 90, payloadRotateSpeed)
      );

      return 1 - Mathf.len(inputting.x() - x, inputting.y() - y) / (block.size * tilesize / 2f);
    }

    return 0;
  }

  public Vec2 outputtingOffset(){
    return tempVec.set(
      Angles.trnsx(rotation * 90, block.size * tilesize / 2f),
      Angles.trnsy(rotation * 90, block.size * tilesize / 2f)
    );
  }

  public boolean blends(int direction){
    return PayloadBlock.blends(self(), direction);
  }

  public void onRemoved(){
    onRemovedPayloadBuild();
  }

  public void onRemovedPayloadBuild(){
    if(!carried){
      for(Payload payload : payloads.iterate()){
        payload.dump();
      }
    }
  }

  public void onProximityUpdate(){
    onProximityUpdatePayloadBuild();
  }

  public void onProximityUpdatePayloadBuild(){
    int bit = 0;
    for(int i = 0; i < 4; i++){
      if(blends(i)) bit |= 1 << i;
    }
    blendBit = bit;
  }

  public void pickedUp(){
    pickedUpPayloadBuild();
  }

  public void pickedUpPayloadBuild(){
    carried = true;
  }

  public void drawTeamTop(){
    drawTeamTopPayloadBuild();
  }

  public void drawTeamTopPayloadBuild(){
    carried = false;
  }

  @Annotations.Replace(1)
  public PayloadSeq getPayloads(){
    return getPayloadsPayloadBuild();
  }

  public PayloadSeq getPayloadsPayloadBuild(){
    tempPayloads.clear();
    for(Payload payload : payloads.iterate()){
      tempPayloads.add(payload.content());
    }
    return tempPayloads;
  }

  @Annotations.Replace(1)
  public Payload takePayload(){
    return takePayloadPayloadBuild();
  }

  public Payload takePayloadPayloadBuild(){
    return payloads.take();
  }

  @Annotations.Replace(1)
  public Payload getPayload(){
    return getPayloadPayloadBuild();
  }

  public Payload getPayloadPayloadBuild(){
    return payloads.get();
  }

  @Annotations.Replace(1)
  public boolean acceptPayload(Building source, Payload payload){
    return acceptPayloadPayloadBuild(source, payload);
  }

  public boolean acceptPayloadPayloadBuild(Building source, Payload payload){
    return payloads.total() < payloadCapacity;
  }

  public void popPayload(){
    if(outputLocking) return;
    outputting = takePayload();
    outputLocking = outputting != null;
    if(outputLocking) stackAlpha = 0;
  }

  public boolean acceptUnitPayload(Unit unit){
    return inputting == null;
  }

  public void updateTile(){
    updateTilePayloadBuild();
  }

  public void updateTilePayloadBuild(){
    if(!outputLocking){
      stackAlpha = (Mathf.approachDelta(stackAlpha, inputting != null && outputting == null ? 0 : 1, payloadSpeed / (block.size * tilesize / 2f)));
    }

    for(Payload payload : payloads.iterate()){
      payload.update(null, self());
    }

    Vec2 offset = outputtingOffset();
    Building targetTile;
    boolean front = false;
    if(Math.max(Math.abs(offset.x), Math.abs(offset.y)) <= block.size / 2f * tilesize + 0.5f){
      targetTile = front();
      front = true;
    }else targetTile = Vars.world.buildWorld(x + offset.x, y + offset.y);

    boolean canDump = targetTile == null || !targetTile.tile.solid();
    boolean canMove = targetTile != null && (targetTile.block.acceptsPayload || targetTile.block.outputsPayload) && targetTile.interactable(team);

    if(!outputLocking && (canDump || canMove)) popPayload();

    float inputProgress = handleInputPayload();
    float outputProgress = handleOutputPayload();

    if(inputProgress >= 0.999f){
      if(payloads.total() < payloadCapacity){
        payloads.add(inputting);
        stackAlpha = 1;
        inputting = null;
      }
    }

    if(canDump && !canMove){
      pushOutput(outputting, outputProgress);
    }

    if(outputProgress >= 0.999f){
      if(canMove){
        if(targetTile.acceptPayload(self(), outputting)){
          float rot = outputting.rotation();
          targetTile.handlePayload(self(), outputting);

          if(!front && targetTile instanceof PayloadBlock.PayloadBlockBuild<?> build){
            build.payload.set(build.x, build.y, rot);
            build.payVector.setZero();
            build.payRotation = rot;
          }

          released(outputting);
          outputting = (null);
          outputLocking = (false);
        }
      }else if(canDump){
        if(outputting.dump()){
          released(outputting);
          outputting = (null);
          outputLocking = (false);
        }
      }
    }
  }

  public void released(Payload payload){
  }

  abstract Building front();

  @Annotations.Replace(1)
  public void handlePayload(Building source, Payload payload){
    handlePayloadPayloadBuild(source, payload);
  }

  public void handlePayloadPayloadBuild(Building source, Payload payload){
    if(source != self()){
      inputting = payload;
    }else{
      payloads.add(payload);
      stackAlpha = 1;
    }
  }

  public void drawPayloadPayloadBuild(){
    if(inputting != null){
      inputting.draw();
    }
    if(outputting != null){
      outputting.draw();
    }

    Payload p = getPayload();
    if(p != null){
      Draw.scl(stackAlpha);
      Draw.alpha(stackAlpha);
      p.draw();
      Draw.reset();
    }
  }

//    @universecore.annotations.Annotations.MethodEntry(entryMethod = "write", paramTypes = "arc.util.io.Writes -> write")
//    public void writePayloads(Writes write) {
//        payloads.write(write);
//        Payload.write(inputting, write);
//        Payload.write(outputting, write);
//        write.f(stackAlpha);
//        write.bool(outputLocking);
//    }
//
//    @universecore.annotations.Annotations.MethodEntry(entryMethod = "read", paramTypes = {"arc.util.io.Reads -> read", "byte -> revision"})
//    public void readPayloads(Reads read, byte revision) {
//        payloads.read(read, revision < version());
//        inputting(Payload.read(read));
//        outputting(Payload.read(read));
//        stackAlpha(read.f());
//        outputLocking(read.bool());
//    }
}
