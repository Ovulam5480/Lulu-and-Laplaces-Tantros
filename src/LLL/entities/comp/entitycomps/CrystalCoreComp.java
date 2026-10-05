package LLL.entities.comp.entitycomps;

import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.entity.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import ent.anno.*;
import mindustry.world.*;

@Annotations.EntityComponent
@Annotations.EntityDef(CrystalCorec.class)
abstract class CrystalCoreComp implements TileEntityc, SerializationPacketSeqc{
  @Annotations.Import
  float x, y;
  @Annotations.Import
  Block block;
  @Annotations.Import
  Seq<Packetc> packets = new Seq<>();

  float timer = 0;
  public static Seq<Tile> tmp = new Seq<>();

  @Annotations.Replace
  public CrystalCoreBlock blockAs(){
    return (CrystalCoreBlock)block;
  }

  @Override
  public void update(){
    CrystalCoreBlock crystalCore = blockAs();

    packets.remove(p -> p.owner() != null || !p.isAdded());

    if(packets.size < crystalCore.maxSpawners && (timer += Time.delta) > blockAs().spawnTime){
      spawnCrystalCluster(crystalCore);

      timer = 0;
    }
  }

  //todo call
  public void spawnCrystalCluster(CrystalCoreBlock crystalCore){
    Vec2 spawnerPos = Tmp.v1.trns(Mathf.random(360), Mathf.random(crystalCore.spawnerRadius)).add(this);

    Packetc packetc = crystalCore.packetType.create(null, spawnerPos.x, spawnerPos.y);

    for(ResourceStack<?> resource : crystalCore.resources){
      packetc.resources().add(resource.copy());
    }

    packets.add(packetc);

    crystalCore.spawnerEffect.at(spawnerPos.x, spawnerPos.y);
  }

  @Override
  public void draw(){
    //Drawf.shadow(blockAs().coreRegion, x - 2, y - 2);
    Draw.rect(blockAs().coreRegion, x, y);
  }
}
