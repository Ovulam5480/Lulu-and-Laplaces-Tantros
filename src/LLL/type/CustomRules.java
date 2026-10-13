package LLL.type;

import arc.*;
import arc.struct.*;
import mindustry.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.io.*;
import mindustry.ui.dialogs.*;
import universe.util.reflect.*;

import java.io.*;

public class CustomRules implements SaveFileReader.CustomChunk{
  private static Seq<CustomRule> customRules = new Seq<>();

  static{
    Events.on(EventType.ClientLoadEvent.class, e -> {
      Seq<CustomRulesDialog> ruless = new Seq<>();
      MapInfoDialog infoDialog = Reflection.accessField(MapEditorDialog.class, MapInfoDialog.class, "infoDialog").get(Vars.ui.editor);
      MapPlayDialog playDialog = Reflection.accessField(MapEditorDialog.class, MapPlayDialog.class, "playtestDialog").get(Vars.ui.editor);
      CustomRulesDialog rulesDialog1 = Reflection.accessField(MapInfoDialog.class, CustomRulesDialog.class, "ruleInfo").get(infoDialog);
      CustomRulesDialog rulesDialog2 = Reflection.accessField(PausedDialog.class, CustomRulesDialog.class, "rulesDialog").get(Vars.ui.paused);
      CustomRulesDialog rulesDialog3 = Reflection.accessField(MapPlayDialog.class, CustomRulesDialog.class, "dialog").get(playDialog);
      ruless.add(rulesDialog1, rulesDialog2, rulesDialog3);

      ruless.each(c -> c.additionalSetup.add(() -> customRules.each(cr -> cr.dialog(c))));
    });
  }

  public static void add(CustomRule rule){
    customRules.add(rule);
  }

  @Override
  public void write(DataOutput stream) throws IOException{
    for(CustomRule rule : customRules){
      rule.write(stream);
    }
  }

  @Override
  public void read(DataInput stream) throws IOException{
    for(CustomRule rule : customRules){
      rule.read(stream);
    }
  }

  public static abstract class CustomRule{
    public abstract void dialog(CustomRulesDialog dialog);

    public abstract void write(DataOutput stream) throws IOException;

    public abstract void read(DataInput stream) throws IOException;
  }
}
