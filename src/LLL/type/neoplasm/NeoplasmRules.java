package LLL.type.neoplasm;

import LLL.entities.neoplasmBehavior.*;
import LLL.type.*;
import arc.func.*;

public class NeoplasmRules extends CustomRules{
  public float permeateAmount = 4;
  public float ownerlessReturn = 0.1f;
  public float stressPermeateAmount = 6;
  public float traumaticStress = 5000;
  public float stressReduction = 3f;
  //todo 瘤液吸收/消耗率
  public transient Prov<NeoplasmTrees.NeoplasmTree> behaviorProv = NeoplasmTrees::createPorifera;

  public NeoplasmRules(){
//    CustomRules.add(c -> {
//      c.category("瘤液规则");
//      c.number("血管蔓延", f -> permeateAmount = f, () -> permeateAmount, 0, 60);
//      c.number("应激血管蔓延", f -> stressPermeateAmount = f, () -> stressPermeateAmount, 0, 60);
//      c.number("应激计数器", f -> traumaticStress = f, () -> traumaticStress, 0, 10000);
//      c.number("应激衰减", f -> stressReduction = f, () -> stressReduction, 0, 100);
//      c.number("无主器官返还", f -> ownerlessReturn = f, () -> ownerlessReturn, 0, 1);
//      c.current.button("中枢行为, WIP", () -> {
//        //todo
//      });
//    });
  }
}
