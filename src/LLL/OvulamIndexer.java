package LLL;

import LLL.entities.gen.*;
import LLL.world.*;
import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;

public class OvulamIndexer{
  public Seq<Packetc> all = new Seq<>();
  public QuadTree<LodePacketc> lodePacketTree;

  public EntityGroup<EntityPacketc> entityPackets = new EntityGroup<>(EntityPacketc.class, true, true);
  public EntityGroup<TileEntityc> tileEntity = new EntityGroup<>(TileEntityc.class, true, true);

  public OvulamIndexer(){
//        Events.run(EventType.Trigger.draw, () -> {
//            Draw.z(76);
//            drawQuad(tileEntity.tree());
//        });

    Events.run(EventType.Trigger.update, () -> {
      entityPackets.updatePhysics();
    });

    Events.on(EventType.ResetEvent.class, e -> {
      all.clear();
      entityPackets.clear();
      tileEntity.clear();
    });

    Events.on(OvulamWorld.WorldResizeEvent.class, e -> {
      Rect bounds = new Rect();

      lodePacketTree = new QuadTree<>(Vars.world.getQuadBounds(bounds));
      entityPackets.resize(bounds.x, bounds.y, bounds.width, bounds.height);
      tileEntity.resize(bounds.x, bounds.y, bounds.width, bounds.height);
    });
  }
}
