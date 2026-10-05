package LLL.entities.comp.unitcomps;

import LLL.entities.gen.*;
import LLL.graphics.*;
import LLL.world.blocks.module.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import ent.anno.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static mindustry.type.UnitType.*;

@Annotations.EntityComponent
public abstract class MovableBuildUnitComp implements BlockUnitc, MDTXc, Syncc, HitboxEntityCollisionc{
  @Annotations.Import
  Building tile;
  @Annotations.Import
  boolean added;

  boolean isModule;

  public MovableBlockModule blockAs(){
    return (MovableBlockModule)tile.block;
  }

  @Annotations.Replace
  @Override
  public void tile(Building tile){
    this.tile = tile;

    //sets up block stats
    maxHealth(tile.block.health);
    health(tile.health);

    hitSize(tile.block.size * tilesize);
    set(tile);
    isModule = tile.block instanceof MovableBlockModule;
    if(isModule){
      ((MovableBuildModule)tile).unitSelf(self());
    }
  }

  @Annotations.Replace
  @Override
  public void display(Table table){
    tile.display(table);
  }

  @Annotations.Replace(999)
  public TextureRegion getRegion(){
    return OvulamGraphics.renderToRegion(() -> tile.draw(), Tmp.tr1, x(), y(), hitSize(), hitSize());
  }

  @Override
  public void update(){
    tile.set(this);
    tile.update();

    if(elevation() <= 0.01f && Build.validPlace(tile.block, team(), tileX(), tileY(), tile.rotation, false, true)){
      Building building = tile;
      Tile tile = building.tile = Vars.world.tileWorld(x() - building.block.offset, y() - building.block.offset);

      tile.setBlock(building.block, building.team, building.rotation, () -> building);
      Fx.placeBlock.at(building.x, building.y, building.block.size);

      tile.getLinkedTiles(t -> {
        if(Mathf.chance(0.2f)){
          Fx.coreLandDust.at(t.worldx(), t.worldy(), building.angleTo(t.worldx(), t.worldy()) + Mathf.range(30f), Tmp.c1.set(t.floor().mapColor).mul(1.5f + Mathf.range(0.15f)));
        }
      });

      remove();
    }
  }

  @Annotations.MethodPriority(-999)
  @Override
  public void add(){
    if(tile == null){
      tile = Blocks.router.newBuilding();
    }
  }

  @Override
  public void remove(){
    if(isModule){
      ((MovableBuildModule)tile).unitSelf(null);
    }
  }

  @Annotations.Replace
  @Override
  public void draw(){
    float z = isFlying() ? type().flyingLayer : Layer.block;

    Draw.draw(z, () -> {
      Draw.scl(1 + elevation() * 0.35f);

      drawShadow(self());

      if(isModule && blockAs().drawThrusters()){
        drawThrusters();
      }

      tile.draw();
      if(tile.damaged()) tile.drawCracks();
    });
  }

  public void drawThrusters(){
    float length = (-1 + elevation()) * 9;
    for(int i = 0; i < 4; i++){
      TextureRegion reg = i >= 2 ? blockAs().thruster2() : blockAs().thruster1();
      float dx = Geometry.d4x[i] * length, dy = Geometry.d4y[i] * length;
      Draw.rect(reg, x() + dx, y() + dy, i * 90);
    }
  }

  @Annotations.Replace
  @Override
  public void killed(){
    if(!tile.dead) tile.kill();
    remove();
  }

  public void drawShadow(Unit unit){
    float e = Mathf.clamp(unit.elevation, unit.type.shadowElevation, 1f) * unit.type.shadowElevationScl * (1f - unit.drownTime);

    Drawf.squareShadow(unit.x + shadowTX * e, unit.y + shadowTY * e, tile.block.size * tilesize * 1.85f, 1);
    Draw.color();
  }

  @Annotations.Replace(999)
  @Override
  public boolean isValid(){
    return !dead() && isAdded();
  }

  @Annotations.Replace(999)
  @Override
  public boolean isAdded(){
    return added;
  }

  @Override
  public void write(Writes write){
    write.s(tile.block.id);
    write.b(tile.version());
    tile.writeAll(write);
  }

  @Override
  public void read(Reads read){
    Block block = content.block(read.s());
    Building building = block.newBuilding().create(block, team());

    byte version = read.b();
    building.readAll(read, version);
    building.tile = emptyTile;
    building.set(this);

    tile(building);
  }

  @Override
  public void afterRead(){
    tile(tile);
    controller(type().createController(self()));
  }

  @Override
  public void writeSync(Writes write){
    write.s(tile.block.id);
    write.b(tile.version());
    tile.writeSync(write);
  }

  @Override
  public void readSync(Reads read){
    Block block = content.block(read.s());
    Building building = block.newBuilding().create(block, team());

    byte version = read.b();
    building.readSync(read, version);
    building.tile = emptyTile;

    tile(building);
  }
}
