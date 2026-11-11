package lulu.world.components.buildEntityComps;

import arc.math.*;
import arc.math.geom.*;
import ent.anno.Annotations.*;
import lulu.entities.gen.*;
import lulu.type.*;
import lulu.world.components.blockComps.*;
import lulu.world.modules.*;
import lulu.world.move.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;

@EntityComponent
abstract class MultiPayloadBuildComp implements OvulamBuildingc{
  static Building tmp = Building.create();

  @Import
  float x, y;
  @Import
  Block block;

  public PositionPayloadModule positionPayloads;

  @Override
  public void afterCreate(){
    createPayloadBuild();
  }

  public void createPayloadBuild(){
    positionPayloads = new PositionPayloadModule(self());
  }

  public MultiPayloadBlockComp getMultiPayloadBlock(){
    return (MultiPayloadBlockComp)block;
  }

  public boolean acceptUnitPayload(Unit unit){
    return true;
  }

  @Replace(1)
  public boolean canControlSelect(Unit unit){
    return canControlSelectMultiPayloadBuild(unit);
  }

  public boolean canControlSelectMultiPayloadBuild(Unit unit){
    return !unit.spawnedByCore
      && unit.type.allowedInPayloads
      && acceptUnitPayload(unit)
      && unit.tileOn() != null
      && unit.tileOn().build == self()
      && hasPayloadCapital(unit.type.hitSize);
  }

  @Replace(1)
  public void onControlSelect(Unit player){
    onControlSelectMultiPayloadBuild(player);
  }

  public void onControlSelectMultiPayloadBuild(Unit player){
    float x = player.x, y = player.y;
    int size = block.size;

    Vec2 currentPosition = new Vec2().set(x, y).sub(this).clamp(
      -size * tilesize / 2f, -size * tilesize / 2f,
      size * tilesize / 2f, size * tilesize / 2f);

    this.<Building>self().handleUnitPayload(player, p -> positionPayloads.add(p, currentPosition, setTargetPosition(positionPayloads.total() + 1, getMultiPayloadBlock().moveInMover())));
  }

  //基础载荷接受: 仅判断数量容量. 具体配方筛选由 MultiPayloadCrafterBuildComp.acceptPayload 覆盖
  @Replace(1)
  public boolean acceptPayload(Building source, Payload payload){
    return acceptPayloadMultiPayloadBuild(source, payload);
  }

  public boolean acceptPayloadMultiPayloadBuild(Building source, Payload payload){
    return hasPayloadCapital(payload);
  }

  public int getTotalPayloads(){
    return positionPayloads.total();
  }

  public float payloadUsed(){
    return positionPayloads.payloads.payloads.sumf(p -> Mathf.sqr(p.payload.size() / tilesize));
  }

  public boolean hasPayloadCapital(Payload payload){
    return hasPayloadCapital(payload.size() / tilesize);
  }

  public boolean hasPayloadCapital(float size){
    return getTotalPayloads() < getMultiPayloadBlock().payloadAmountCapacity();
  }

  @Replace(1)
  public Payload takePayload(){
    return takePayloadMultiPayloadBuild();
  }

  public Payload takePayloadMultiPayloadBuild(){
    return positionPayloads.take();
  }

  @Replace(1)
  public Payload getPayload(){
    return getPayloadMultiPayloadBuild();
  }

  public Payload getPayloadMultiPayloadBuild(){
    if(positionPayloads.total() == 0) return null;
    return positionPayloads.get();
  }

  @Replace(1)
  public PayloadSeq getPayloads(){
    return getPayloadsMultiPayloadBuild();
  }

  public PayloadSeq getPayloadsMultiPayloadBuild(){
    return positionPayloads.payloads;
  }

  public Payload getIndexPayload(int index){
    return positionPayloads.payloads.payloads.get(index).payload;
  }

  public boolean facing(Building building){
    return Mathf.mod(Mathf.angle(building.x - x, building.y - y) + 45 - this.<Building>self().rotdeg(), 360) < 90;
  }

  public Building dumpTargetBuilding(PositionPayload todump){
    return this.<Building>self().proximity.find(b -> {
      float ang = Mathf.angle(b.x - todump.x(self()), b.y - todump.y(self()));
      return Math.abs(ang - this.<Building>self().rotdeg()) < 45f;
    });
  }

  public boolean dumpPositionPayload(PositionPayload todump){
    Building target = dumpTargetBuilding(todump);
    if(target == null || !target.acceptPayload(self(), todump.payload)) return false;

    tmp.set(todump.x(self()), todump.y(self()));

    target.handlePayload(tmp, todump.payload);
    positionPayloads.remove(todump);
    return true;
  }

  @Replace(1)
  public void handlePayload(Building source, Payload payload){
    handlePayloadMultiPayloadBuild(source, payload);
  }

  public void handlePayloadMultiPayloadBuild(Building source, Payload payload){
    int size = block.size;
    Vec2 currentPosition = new Vec2().set(source).sub(this).clamp(
      -size * tilesize / 2f, -size * tilesize / 2f,
      size * tilesize / 2f, size * tilesize / 2f);
    positionPayloads.add(new PositionPayload(payload, currentPosition, setTargetPosition(positionPayloads.total() + 1, getMultiPayloadBlock().moveInMover())));
  }

  public void handlePositionPayload(PositionPayload positionPayload){
    positionPayloads.add(positionPayload);
    updatePayload(positionPayload);
  }

  public void updatePayload(PositionPayload payload){
    float rotdeg = this.<Building>self().rotdeg();
    float payRotation = Angles.moveToward(payload.getRotation(), block.rotate ? rotdeg : 90f, getMultiPayloadBlock().payloadRotateSpeed() * this.<Building>self().delta());
    payload.currentPosition.approach(payload.targetPosition, getMultiPayloadBlock().payloadSpeed() * this.<Building>self().delta());
    payload.payload.set(payload.x(self()), payload.y(self()), payRotation);
  }

  public Vec2 setTargetPosition(int index, MovePayload mover){
    return mover.setTargetPosition(self(), index);
  }

  public void movePayloads(PositionPayload payload){
    payload.targetPosition = setTargetPosition(positionPayloads.payloads.payloads.indexOf(payload), getMultiPayloadBlock().moveInMover());
    updatePayload(payload);
  }

  public void drawPayload(){
    if(positionPayloads == null) return;
    positionPayloads.each(PositionPayload::draw);
  }
}
