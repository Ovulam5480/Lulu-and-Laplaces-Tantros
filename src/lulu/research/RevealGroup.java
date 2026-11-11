package lulu.research;

import arc.*;
import arc.struct.*;
import lulu.*;
import lulu.game.*;
import mindustry.ctype.*;

public class RevealGroup{
  public final String name;
  public final Seq<UnlockableContent> contents = new Seq<>();

  public boolean isRevealed;

  public RevealGroup require;
  public RevealGroup subGroup;

  public String localizedName;
  public String description;

  public RevealGroup(String name, UnlockableContent... contents){
    this.name = name;
    this.contents.addAll(contents);
  }

  public void addContent(UnlockableContent... contents){
    this.contents.addAll(contents);
  }

  public void init(){
    isRevealed = LuluMod.globals.getBool("reveal_" + name, false);
  }

  public void reset(){
    isRevealed = false;
  }

  public boolean isRevealed(){
    return isRevealed;
  }

  public void reveal(){
    if(isRevealed) return;

    isRevealed = true;
    if(subGroup != null) subGroup.reveal();

    for(UnlockableContent content : contents){
      content.unlock();
    }

    LuluMod.globals.put("reveal_" + name, true);

    Events.fire(new OvulamEventTypes.RevealedEvent(this));
  }

  public void unReveal(){
    if(!isRevealed) return;

    isRevealed = false;
    if(subGroup != null) subGroup.unReveal();

    for(UnlockableContent content : contents){
      content.clearUnlock();
    }

    LuluMod.globals.put("reveal_" + name, false);
  }
}
