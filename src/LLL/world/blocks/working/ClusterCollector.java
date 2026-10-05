package LLL.world.blocks.working;

import LLL.world.blocks.packet.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

//晶体采集器
public class ClusterCollector extends PackerBlock{
  //public UnitType collectUnitType = OvulamUnitTypes.ccptu;
  public float unitBindRange = 220f;

  public ClusterCollector(String name){
    super(name);
  }

  @Override
  public void init(){
    super.init();
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);
    Drawf.circles(x * tilesize + offset, y * tilesize + offset, unitBindRange);
  }
}
