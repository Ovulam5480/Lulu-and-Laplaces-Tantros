package LLL.content.tantros;

import LLL.content.*;

import static mindustry.content.TechTree.*;

public class TantrosTechTree{
  public static void load(){

    LaplacesTantros.tantros.techTree = nodeRoot("tantros", OvulamBlocks.coreCausality, () -> {
      node(OvulamBlocks.gasHydrateCollector, () -> {
        node(OvulamBlocks.packetCollector, () -> {
          node(OvulamBlocks.lodeExcavator, () -> {
            node(OvulamBlocks.assemblingMachine);
          });
          node(OvulamBlocks.hydrothermalVat, () -> {
          });
        });
      });

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

      nodeProduce(OvulamItems.manganeseNodule, () -> {
        nodeProduce(OvulamItems.ferroManganese, () -> {
        });

        nodeProduce(OvulamItems.cobaltNodule, () -> {
          nodeProduce(OvulamItems.cobalt, () -> {
          });
        });

        nodeProduce(OvulamItems.gasHydrate, () -> {
          nodeProduce(OvulamItems.amethyst, () -> {
          });
          nodeProduce(OvulamLiquids.gas, () -> {
          });
        });
      });

      node(OvulamBlocks.coreBackflow);
    });
  }
}
