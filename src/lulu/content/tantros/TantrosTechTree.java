package lulu.content.tantros;

import lulu.content.*;
import mindustry.content.*;

import static mindustry.content.TechTree.*;

public class TantrosTechTree{
  public static void load(){

    LaplacesTantros.tantros.techTree = nodeRoot("tantros", OvulamBlocks.coreCausality, () -> {
      //factory
      node(OvulamBlocks.undertowDrill, () -> {
        node(OvulamBlocks.packetCollector, () -> {
          node(OvulamBlocks.lodeExcavator, () -> {
            //node(OvulamBlocks.assemblingMachine);
          });
          node(OvulamBlocks.hydrothermalVat, () -> {
          });
        });
      });

      //distribute
      node(OvulamBlocks.itemPacker, () -> {
        node(OvulamBlocks.itemUnpacker, () -> {
          node(OvulamBlocks.liquidUnpacker);
        });
        node(OvulamBlocks.liquidPacker, () -> {
        });

        node(OvulamBlocks.packetConveyor, () -> {
          node(OvulamBlocks.packetConveyorSorter);
          node(OvulamBlocks.packetConveyorDiverter);
        });
      });

      node(OvulamPacketTypes.smallItemPacket, () -> {
        node(OvulamPacketTypes.middleItemPacket, () -> {
          node(OvulamPacketTypes.largeItemPacket, () -> {
          });
        });

        node(OvulamPacketTypes.smallLiquidPacket, () -> {
          node(OvulamPacketTypes.middleLiquidPacket, () -> {
            node(OvulamPacketTypes.largeLiquidPacket, () -> {
            });
          });
        });

        node(OvulamPacketTypes.powerPacket, () -> {

        });

        node(OvulamPacketTypes.payloadPacket, () -> {

        });
      });

      //resources
      node(OvulamItems.manganeseNodule, () -> {
        node(OvulamItems.ferroManganese, () -> {
        });

        node(OvulamItems.cobaltNodule, () -> {
          node(OvulamItems.cobalt, () -> {
          });
        });

        node(OvulamItems.gasHydrate, () -> {
          node(OvulamItems.amethyst, () -> {
          });
          node(OvulamLiquids.gas, () -> {
          });
        });

        //todo 石灰
        node(Items.sand, () -> {
          node(OvulamLiquids.cementSlurry, () -> {
            node(OvulamLiquids.mortar, () -> {});
            node(OvulamLiquids.concrete, () -> {});
          });
        });

        node(OvulamResource.power, () -> {
          node(OvulamResource.surge, () -> {});
          node(OvulamResource.pulse, () -> {});
          node(OvulamResource.drain, () -> {});
        });
      });

      //core
      node(OvulamBlocks.coreBackflow);
    });
  }
}
