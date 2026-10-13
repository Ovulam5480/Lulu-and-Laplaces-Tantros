package LLL.content;

import LLL.content.resourceTypes.*;
import LLL.ctype.packet.*;
import LLL.ctype.packet.packetTypes.*;
import LLL.entities.gen.*;
import arc.func.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class OvulamPacketTypes{
  @Annotations.EntityDef({EntityPacketc.class})
  public static class EntityPacketComp{
  }

  @Annotations.EntityDef({LodePacketc.class})
  public static class LodePacketComp{
  }

  public static PacketType
    smallItemPacket, middleItemPacket, largeItemPacket, bolt,
    smallLiquidPacket, middleLiquidPacket, largeLiquidPacket,
    payloadPacket,
    powerPacket;
  public static PacketType itemLodePacket, liquidLodePacket, payloadLodePacket;

  public static Seq<PacketType> packetTypes = new Seq<>();

  public static void load(){
    smallItemPacket = registerType("small-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 1;
      capacity = 3;
    }});

    middleItemPacket = registerType("middle-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 2;
      capacity = 24;
    }});

    largeItemPacket = registerType("large-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 3;
      capacity = 81;
    }});

    AccessoriesType type = AccessoriesType.bolt;
    bolt = registerType("small-" + type.name() + "-packet", EntityPacket.class, name -> new AccessoriesPacketType(name, type){{
      resourceClass = Item.class;
      size = 1;
      capacity = 4;
      drag = 0.03f;
    }});

    smallLiquidPacket = registerType("small-liquid-packet", EntityPacket.class, name -> new MetaBallPacketType(name){{
      resourceClass = Liquid.class;
      size = 1;
      capacity = 10;
    }});

    middleLiquidPacket = registerType("middle-liquid-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Liquid.class;
      size = 2;
      capacity = 80;
    }});

    largeLiquidPacket = registerType("large-liquid-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Liquid.class;
      size = 3;
      capacity = 270;
    }});

    powerPacket = registerType("power-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Power.class;
      size = 1;
      capacity = 2500;

      collisionLayer = 2;
    }});

    itemLodePacket = registerType("ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      resourceClass = Item.class;
      size = 1;

      drawLayer = Layer.blockUnder;

      tenacity = 0.1f;
      capacity = 100;
    }});

    liquidLodePacket = registerType("liquid-ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      resourceClass = Liquid.class;
      size = 1;

      tenacity = 0.1f;
      capacity = 100;
    }});

    payloadPacket = registerType("payload-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      size = 2;

      resourceClass = UnlockableContent.class;
    }});

    payloadLodePacket = registerType("payload-ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      size = 2;

      resourceClass = UnlockableContent.class;
    }});
//        unitItemPacket = registerType("unit-item-packet", PacketUnit.class, name -> {
//            return new SingleItemUnitPacketType(name, new UnitType(name){{
//                health = Float.MAX_VALUE;
//                hitSize = 2;
//            }}){{
//                size = 1;
//                capacity = 2500;
//
//                collisionLayer = 2;
//            }};
//        });
  }

  public static void initAndLoad(){
    packetTypes.each(PacketType::init);
    packetTypes.each(PacketType::load);
  }

  public static <T extends Packetc> PacketType registerType(String name, Class<T> type, Func<String, PacketType> construct){
    PacketType packetType = construct.get(name);
    packetType.constructor = EntityRegistry.get(type);
    return packetType;
  }
}