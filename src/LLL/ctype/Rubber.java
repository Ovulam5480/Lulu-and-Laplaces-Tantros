package LLL.ctype;

import arc.graphics.*;
import mindustry.type.*;

public class Rubber extends Item{
  public float
    //强度
    strength,
  //耐低温性
  lowTempResistance,
  //耐高温性
  highTempResistance,
  //加工性能
  processability,
  //气密性
  gasImpermeability,
  //耐磨性
  abrasionResistance,
  //耐化学性
  chemicalResistance;

  //该橡胶是否可以用硫进行硫化, 极性橡胶或者是馒头为false
  public boolean thiolatable = true;

  public Rubber(String name, Color color){
    super(name, color);
  }
}