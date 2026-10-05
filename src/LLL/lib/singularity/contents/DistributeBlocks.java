package LLL.lib.singularity.contents;

import LLL.lib.singularity.type.*;
import LLL.lib.singularity.world.blocks.distribute.*;
import LLL.lib.singularity.world.blocks.distribute.matrixGrid.*;
import LLL.lib.singularity.world.blocks.distribute.netcomponents.*;
import LLL.lib.singularity.world.distribution.*;
import LLL.lib.singularity.world.draw.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

public class DistributeBlocks implements ContentList{
  /**
   * 运输节点
   */
  public static Block transport_node,
  /**
   * 相位运输节点
   */
  phase_transport_node,
  /**
   * 铱制高效运输节点
   */
  iridium_transport_node,
  /**
   * 矩阵中枢
   */
  matrix_core,
  /**
   * 矩阵桥
   */
  matrix_bridge,
  /**
   * 矩阵塔
   */
  matrix_tower,
  /**
   * 网格控制器
   */
  matrix_controller,
  /**
   * 网格框架
   */
  matrix_grid_node,
  /**
   * io端点
   */
  io_point,
  /**
   * 能源管理器
   */
  matrix_energy_manager,
  /**
   * 能量接口
   */
  matrix_power_interface,
  /**
   * 中子接口
   */
  matrix_neutron_interface,
  /**
   * 矩阵储能簇
   */
  matrix_energy_buffer,
  /**
   * 矩阵组件接口
   */
  matrix_component_interface,
  /**
   * 矩阵处理单元
   */
  matrix_process_unit,
  /**
   * 矩阵拓扑容器
   */
  matrix_topology_container,
  /**
   * 通用物质缓存器
   */
  matrix_buffer,
  /**
   * 自动回收组件
   */
  automatic_recycler_component;

  @Override
  public void load(){
    transport_node = new ItemNode("transport_node"){{
      requirements(Category.distribution, ItemStack.empty);

      range = 4;
      arrowTimeScl = 6;
      transportTime = 3;
    }};

    phase_transport_node = new ItemNode("phase_transport_node"){{
      requirements(Category.distribution, ItemStack.empty);

      researchCostMultiplier = 1.5f;
      itemCapacity = 15;
      maxItemCapacity = 60;
      range = 12;
      arrowPeriod = 0.9f;
      arrowTimeScl = 2.75f;
      hasPower = true;
      pulse = true;
      envEnabled |= Env.space;
      transportTime = 1f;
      newConsume();
      consume.power(0.4f);
    }};

    iridium_transport_node = new ItemNode("iridium_transport_node"){{
      requirements(Category.distribution, ItemStack.empty);

      researchCostMultiplier = 2;
      itemCapacity = 20;
      maxItemCapacity = 80;
      range = 20;
      siphon = true;
      arrowPeriod = 1.1f;
      arrowTimeScl = 2.25f;
      hasPower = true;
      pulse = true;
      envEnabled |= Env.space;
      transportTime = 0.5f;
      newConsume();
      consume.power(1f);
    }};

    matrix_core = new DistNetCore("matrix_core"){{
      requirements(SglCategory.matrix, ItemStack.empty);

      size = 6;

      matrixEnergyUse = 1f;
    }};

    matrix_bridge = new MatrixBridge("matrix_bridge"){{
      requirements(SglCategory.matrix, ItemStack.empty);

      size = 2;

      newConsume();
      consume.powerCond(0.8f, 0, (MatrixBridge.MatrixBridgeBuild e) -> !e.distributor.network.netStructValid());

      matrixEnergyUse = 0.02f;
    }};

    matrix_tower = new MatrixBridge("matrix_tower"){{
      requirements(SglCategory.matrix, ItemStack.empty);

      crossLinking = true;
      size = 3;
      maxLinks = 4;

      linkRange = 45;

      newConsume();
      consume.powerCond(1.6f, 0, (MatrixBridge.MatrixBridgeBuild e) -> !e.distributor.network.netStructValid());

      matrixEnergyUse = 0.05f;
    }};

    matrix_controller = new MatrixGridCore("matrix_controller"){{
      requirements(SglCategory.matrix, ItemStack.empty);

      linkOffset = 8;
      size = 4;

      matrixEnergyUse = 1.2f;
    }};

    matrix_grid_node = new MatrixEdgeBlock("matrix_grid_node"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      linkOffset = 4.5f;
      size = 2;
    }};

    io_point = new GenericIOPoint("io_point"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 1;
    }};

    matrix_energy_manager = new DistEnergyManager("matrix_energy_manager"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 4;
    }};

    matrix_energy_buffer = new DistEnergyBuffer("matrix_energy_buffer"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 3;

      matrixEnergyCapacity = 16384;
    }};

    matrix_power_interface = new DistPowerEntry("matrix_power_interface"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 2;

      consPower = 1000;
      eneProd = 480;
    }};

    matrix_neutron_interface = new DistNeutronEntry("matrix_neutron_interface"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 2;
    }};

    matrix_component_interface = new ComponentInterface("matrix_component_interface"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 2;
      topologyUse = 0;
      matrixEnergyUse = 0.2f;

      draw = new DrawMulti(
        new DrawDefault(),
        new DrawDirSpliceBlock<ComponentInterfaceBuild>(){{
          simpleSpliceRegion = true;
          spliceBits = e -> e.interSplice;
        }},
        new DrawEdgeLinkBits<ComponentInterfaceBuild>(){{
          layer = Layer.blockOver;
          compLinked = e -> e.connectSplice;
        }}
      );
    }};

    matrix_process_unit = new CoreNeighbourComponent("matrix_process_unit"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 3;

      computingPower = 8;
      matrixEnergyUse = 0.6f;
    }};

    matrix_topology_container = new CoreNeighbourComponent("matrix_topology_container"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 4;

      topologyCapaity = 16;
      matrixEnergyUse = 0.8f;
    }};

    matrix_buffer = new NetPluginComp("matrix_buffer"){{
      requirements(SglCategory.matrix, ItemStack.empty);
      size = 3;
      bufferSize = ObjectMap.of(
        DistBufferType.itemBuffer, 512,
        DistBufferType.liquidBuffer, 512
      );
      matrixEnergyUse = 0.6f;
    }};

    automatic_recycler_component = new AutoRecyclerComp("automatic_recycler_component"){{
      requirements(SglCategory.matrix, ItemStack.empty);

      hasItems = hasLiquids = true;

      setRecycle(DistBufferType.itemBuffer, e -> e.items.clear());
      setRecycle(DistBufferType.liquidBuffer, e -> e.liquids.clear());

      size = 3;
      matrixEnergyUse = 0.4f;

      buildType = () -> new AutoRecyclerCompBuild(){
        @Override
        public int acceptStack(Item item, int amount, Teamc source){
          return distributor.network.getCore() == source ? amount : 0;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
          return distributor.network.getCore() == source;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
          return distributor.network.getCore() == source;
        }
      };
    }};
  }
}
