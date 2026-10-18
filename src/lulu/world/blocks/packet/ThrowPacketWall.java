package lulu.world.blocks.packet;

import lulu.content.*;
import lulu.ctype.packet.*;
import lulu.entities.gen.*;
import lulu.type.resourceStacks.*;
import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class ThrowPacketWall extends Block{
  public Seq<ResourceStack<?>> resources = new Seq<>();
  public PacketType packetType;
  public float amount = 1;
  public float randAmount = 1;
  public float randChance = 0.3f;

  public ThrowPacketWall(String name, PacketType packetType){
    super(name);
    rebuildable = false;
    destructible = true;
    category = Category.defense;
    buildVisibility = BuildVisibility.shown;
    this.packetType = packetType;
  }

  public class ThrowPacketWallBuild extends Building{
    @Override
    public void killed(){
      super.killed();
      for(int i = 0; i < amount + randAmount; i++){
        if(i < amount || Mathf.chance(randChance)){
          Packetc packetc = packetType.create(null, x + Mathf.range(size * 8 / 2f), y + Mathf.range(size * 8 / 2f));
          packetc.copyResourceStacks(resources);

          if(packetc instanceof EntityPacketc){
            packetc.vel().trns(Mathf.random(360), Mathf.random(1, 4));
          }
        }
      }
    }
  }
}
