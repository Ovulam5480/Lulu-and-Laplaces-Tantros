package LLL.content.blocks;

import LLL.content.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.packet.*;
import LLL.world.blocks.working.*;
import mindustry.type.*;

public class Packet{
  public static void load(){
    OvulamBlocks.itemPacker = new ConsumePacketPacker("item-packer"){{
      size = 2;

      requirements = ItemStack.with(OvulamItems.ferroManganese, 10);
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 20);
      packetType = OvulamPacketTypes.smallItemPacket;
    }};

    OvulamBlocks.itemUnpacker = new PacketUnpacker("item-unpacker"){{
      size = 2;

      excavateTime = 30f;

      requirements = ItemStack.with(OvulamItems.ferroManganese, 5);
      researchCost = ItemStack.with(OvulamItems.ferroManganese, 10);
      filter.add(ItemResourceStack.class);
    }};

    OvulamBlocks.liquidPacker = new ConsumePacketPacker("liquid-packer"){{
      size = 2;

      liquidCapacity = 30f;

      requirements = ItemStack.with(OvulamItems.cobalt, 5, OvulamItems.amethyst, 10);
      researchCost = ItemStack.with(OvulamItems.cobalt, 15, OvulamItems.amethyst, 30);
      packetType = OvulamPacketTypes.smallLiquidPacket;
    }};

    OvulamBlocks.liquidUnpacker = new PacketUnpacker("liquid-unpacker"){{
      size = 2;
      filter.add(LiquidResourceStack.class);
      excavateAmount = 12f;

      liquidCapacity = 30f;

      requirements = ItemStack.with(OvulamItems.amethyst, 5);
      researchCost = ItemStack.with(OvulamItems.amethyst, 15);
    }};

    OvulamBlocks.powerPacker = new ConsumePacketPacker("power-packer"){{
      size = 2;

      packetType = OvulamPacketTypes.powerPacket;
    }};

    //todo
    OvulamBlocks.powerUnpacker = new PacketUnpacker("power-unpacker"){{
      size = 2;
      filter.add(PowerResourceStack.class);

      excavateAmount = 1200f;
    }};

    OvulamBlocks.packetVoid = new PacketVoid("packet-void"){{
      size = 2;
    }};

    OvulamBlocks.packetSource = new PacketSource("packet-source"){{
      size = 2;
    }};
  }
}
