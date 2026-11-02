package lulu.lib.singularity.world.blocks.research;

import arc.struct.*;
import mindustry.world.Block;

/**
 * 研究模块（可放置方块）。
 * <p>研究台需要其周围相邻的研究模块才能推进研究；同时每个研究可声明其所要求的模块类型
 * （{@link lulu.lib.singularity.game.researchs.ResearchProject#requireDevices}），相邻模块须与之兼容。
 */
public class ResearchDevice extends Block{
  /** 与之兼容的研究模块（用于研究要求校验）。 */
  public Seq<ResearchDevice> compatibles = new Seq<>();

  /** 该模块可提供的科技点（预留，当前未参与实际消耗计算）。 */
  public int provTechPoints = 0;

  public ResearchDevice(String name){
    super(name);
  }

  public boolean isCompatible(ResearchDevice device){
    return compatibles.contains(device);
  }

  public ResearchDevice setCompatibles(ResearchDevice... compatibles){
    this.compatibles.addAll(compatibles);
    return this;
  }
}
