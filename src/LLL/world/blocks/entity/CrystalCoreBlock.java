package LLL.world.blocks.entity;

import LLL.ctype.packet.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

public class CrystalCoreBlock extends TileEntityBlock{
  public PacketType packetType;
  public Seq<ResourceStack<?>> resources = new Seq<>();

  public float spawnTime = 160f;
  public float spawnerRadius = 80f;
  public Effect spawnerEffect = Fx.explosion;

  public int maxSpawners = 5;
  public TextureRegion coreRegion;

  public CrystalCoreBlock(String name){
    super(name);

    entityProvider = CrystalCore::create;
    //todo
    clipSize = 22;
    size = 2;
  }

  @Override
  public void load(){
    super.load();

    coreRegion = Core.atlas.find(name + "-entity");
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{coreRegion};
  }

  @Override
  public void drawOverlay(float x, float y, int rotation){
    Drawf.circles(x, y, spawnerRadius);
  }
}
