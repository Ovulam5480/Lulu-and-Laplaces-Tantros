package LLL.type;

import arc.*;
import arc.struct.*;
import mindustry.*;
import mindustry.editor.*;
import mindustry.game.*;
import mindustry.io.*;
import mindustry.ui.dialogs.*;
import universecore.util.handler.*;

import java.io.*;

public class CustomRules implements SaveFileReader.CustomChunk{
  private static Seq<CustomRule> customRules = new Seq<>();

  static{
    Events.on(EventType.ClientLoadEvent.class, e -> {
      Seq<CustomRulesDialog> ruless = new Seq<>();
      MapInfoDialog infoDialog = FieldHandler.getValueDefault(Vars.ui.editor, "infoDialog");
      MapPlayDialog playDialog = FieldHandler.getValueDefault(Vars.ui.editor, "playtestDialog");
      CustomRulesDialog rulesDialog1 = FieldHandler.getValueDefault(infoDialog, "ruleInfo");
      CustomRulesDialog rulesDialog2 = FieldHandler.getValueDefault(Vars.ui.paused, "rulesDialog");
      CustomRulesDialog rulesDialog3 = FieldHandler.getValueDefault(playDialog, "dialog");
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
