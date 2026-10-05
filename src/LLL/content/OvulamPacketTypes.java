package LLL.content;

import LLL.content.resourceTypes.*;
import LLL.ctype.packet.*;
import LLL.ctype.packet.packetTypes.*;
import LLL.entities.gen.*;
import arc.func.*;
import arc.struct.*;
import ent.anno.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class OvulamPacketTypes{
  public @Annotations.EntityDef({EntityPacketc.class})
  static UnitTypes unitTypes;
  public static PacketType
    smallItemPacket, middleItemPacket, largeItemPacket, bolt,
    smallLiquidPacket, middleLiquidPacket, largeLiquidPacket,
    payloadPacket,
    powerPacket;
  public static @Annotations.EntityDef({LodePacketc.class}) PacketType lodePacket;

  //todo contentType
  public static Seq<PacketType> packetTypes = new Seq<>();

  public static void load(){
    smallItemPacket = EntityRegistry.content("small-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 1;
      capacity = 3;
    }});

    middleItemPacket = EntityRegistry.content("middle-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 2;
      capacity = 24;
    }});

    largeItemPacket = EntityRegistry.content("large-item-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Item.class;
      size = 3;
      capacity = 81;
    }});

    AccessoriesType type = AccessoriesType.bolt;
    bolt = EntityRegistry.content("small-" + type.name() + "-packet", EntityPacket.class, name -> new AccessoriesPacketType(name, type){{
      resourceClass = Item.class;
      size = 1;
      capacity = 4;
      drag = 0.03f;
    }});

    smallLiquidPacket = EntityRegistry.content("small-liquid-packet", EntityPacket.class, name -> new MetaBallPacketType(name){{
      resourceClass = Liquid.class;
      size = 1;
      capacity = 10;
    }});

    middleLiquidPacket = EntityRegistry.content("middle-liquid-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Liquid.class;
      size = 2;
      capacity = 80;
    }});

    largeLiquidPacket = EntityRegistry.content("large-liquid-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Liquid.class;
      size = 3;
      capacity = 270;
    }});

    powerPacket = EntityRegistry.content("power-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      resourceClass = Power.class;
      size = 1;
      capacity = 2500;

      collisionLayer = 2;
    }});

    lodePacket = EntityRegistry.content("ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      resourceClass = Item.class;
      size = 1;

      drawLayer = Layer.blockUnder;

      tenacity = 0.1f;
      capacity = 100;
    }});

    EntityRegistry.content("liquid-ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      resourceClass = Liquid.class;
      size = 1;

      tenacity = 0.1f;
      capacity = 100;
    }});

    EntityRegistry.content("payload-packet", EntityPacket.class, name -> new SinglePacketType(name){{
      size = 2;

      resourceClass = UnlockableContent.class;
    }});

    EntityRegistry.content("payload-ore-packet", LodePacket.class, name -> new LodePacketType(name){{
      size = 2;

      resourceClass = UnlockableContent.class;
    }});
//        unitItemPacket = EntityRegistry.content("unit-item-packet", PacketUnit.class, name -> {
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

  public static <T extends Packetc> PacketType registerAccessoriesPacketType(AccessoriesType accessoriesType, Class<T> entityClass, String prefix, Cons<PacketType> cons){
    return registerAccessoriesPacketType(accessoriesType, entityClass, prefix, "packet", cons);
  }

  public static <T extends Packetc> PacketType registerAccessoriesPacketType(AccessoriesType accessoriesType, Class<T> entityClass, String prefix, String suffix, Cons<PacketType> cons){
    return EntityRegistry.content(prefix + "-" + accessoriesType.name() + "-" + suffix, entityClass, name -> {
      PacketType packetType = new AccessoriesPacketType(name, accessoriesType);
      cons.get(packetType);
      return packetType;
    });
  }
}