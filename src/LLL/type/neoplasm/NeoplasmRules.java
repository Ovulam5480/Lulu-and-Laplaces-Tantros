package LLL.type.neoplasm;

import LLL.entities.neoplasmBehavior.*;
import arc.func.*;
import arc.struct.*;
import mindustry.*;
import mindustry.editor.*;
import mindustry.ui.dialogs.*;
import universe.util.reflect.*;

public class NeoplasmRules{
  public float permeateAmount = 4;
  public float ownerlessReturn = 0.1f;
  public float stressPermeateAmount = 6;
  public float traumaticStress = 5000;
  public float stressReduction = 3f;
  //todo 瘤液吸收/消耗率
  public Prov<NeoplasmTrees.NeoplasmTree> behaviorProv = NeoplasmTrees::createPorifera;


  public NeoplasmRules(){
    Seq<CustomRulesDialog> ruless = new Seq<>();
    MapInfoDialog infoDialog = Reflection.accessField(MapEditorDialog.class, MapInfoDialog.class, "infoDialog").get(Vars.ui.editor);
    MapPlayDialog playDialog = Reflection.accessField(MapEditorDialog.class, MapPlayDialog.class, "playtestDialog").get(Vars.ui.editor);
    CustomRulesDialog rulesDialog1 = Reflection.accessField(MapInfoDialog.class, CustomRulesDialog.class, "ruleInfo").get(infoDialog);
    CustomRulesDialog rulesDialog2 = Reflection.accessField(PausedDialog.class, CustomRulesDialog.class, "rulesDialog").get(Vars.ui.paused);
    CustomRulesDialog rulesDialog3 = Reflection.accessField(MapPlayDialog.class, CustomRulesDialog.class, "dialog").get(playDialog);
    ruless.add(rulesDialog1, rulesDialog2, rulesDialog3);

    ruless.each(c -> c.additionalSetup.add(() -> {
      c.category("瘤液规则");
      c.number("血管蔓延", f -> permeateAmount = f, () -> permeateAmount, 0, 60);
      c.number("应激血管蔓延", f -> stressPermeateAmount = f, () -> stressPermeateAmount, 0, 60);
      c.number("应激计数器", f -> traumaticStress = f, () -> traumaticStress, 0, 10000);
      c.number("应激衰减", f -> stressReduction = f, () -> stressReduction, 0, 100);
      c.number("无主器官返还", f -> ownerlessReturn = f, () -> ownerlessReturn, 0, 1);
      c.current.button("中枢行为, WIP", () -> {
        //todo
      });
    }));
  }
}
