package LLL.content;

import LLL.content.blocks.*;
import LLL.entities.gen.*;
import LLL.world.blocks.packet.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.world.*;

public class OvulamBlocks{
  //region Packet
  private static @Annotations.EntityDef(value = {Buildingc.class, PacketTransporterc.class}, serialize = false) PacketBlock a;

  public static Block itemPacker;
  public static Block itemUnpacker;
  public static Block liquidPacker;
  public static Block liquidUnpacker;
  public static Block powerPacker;
  public static Block powerUnpacker;
  public static Block packetVoid;
  public static Block packetSource;
  public static Block packetCompositeConveyor;

  //region Distribution
  public static Block packetConveyor;
  public static Block packetConveyorRouter;
  public static Block packetConveyorSorter;
  public static Block packetConveyorDiverter;

  //region UnderWater
  public static Block lodeExcavator;
  public static Block gasHydrateCollector;
  public static Block packetCollector;
  public static Block clusterCollector;

  //region Crafter
  public static Block hydrothermalVat;
  public static Block assemblingMachine;
  //endregion

  //region Defense
  public static Block setCement, setMortar, setConcrete;

  //region Environments
  public static Block hydrothermalVent, gasHydrateFloor;

  //region Neoplasm

  //region Misc
  public static Block amethystCrystalCore, manganeseNoduleProp;
  public static Block blastCylinder;
  public static Block ferroManganeseRodGroup;
  public static Block coreBackflow;
  public static Block coreCausality;

  public static void load(){
    Defense.load();
    Packet.load();
    Distribution.load();
    Crafter.load();
    Environments.load();
    Neoplasm.load();
    UnderWater.load();
    Misc.load();
  }
}
