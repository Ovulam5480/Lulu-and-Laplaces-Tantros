package LLL.lib.singularity.ui.fragments.notification;

import LLL.lib.singularity.graphic.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import universecore.util.*;

import java.util.*;

public abstract class Notification implements DataPackable{
  public final Date date = new Date();

  public String title;
  public String information;

  public boolean buildWindow, activeWindow;
  public float duration = 5f;

  public boolean readed;

  public abstract Drawable getIcon();

  public abstract void activity();

  public abstract void buildWindow(Table table);

  public abstract Color getIconColor();

  public abstract Color getTitleColor();

  public abstract Color getInformationColor();

  public Notification(String title, String information){
    this.title = title;
    this.information = information;
  }

  public Notification activeWindow(){
    this.activeWindow = true;
    return this;
  }

  public Notification duration(float duration){
    this.duration = duration;
    return this;
  }

  @Override
  public void write(Writes write){
    write.l(date.getTime());
    write.bool(readed);
    write.str(title);
    write.str(information);
  }

  public void read(Reads read){
    date.setTime(read.l());
    readed = read.bool();
    title = read.str();
    information = read.str();
  }

  public static class Note extends Notification{
    public static final long typeID = 6373849572987459234L;

    public static void assign(){
      DataPackable.assignType(typeID, args -> new Note());
    }

    //Internal usage
    Note(){
      super("", "");
    }

    public Note(String title, String information){
      super(title, information);
    }

    @Override
    public Drawable getIcon(){
      return SglDrawConst.techPoint;
    }

    @Override
    public void activity(){
    }

    @Override
    public void buildWindow(Table table){
    }

    @Override
    public Color getIconColor(){
      return SglDrawConst.matrixNet;
    }

    @Override
    public Color getTitleColor(){
      return Pal.accent;
    }

    @Override
    public Color getInformationColor(){
      return Color.white;
    }

    @Override
    public long typeID(){
      return typeID;
    }
  }

  public static class Warning extends Note{
    public static final long typeID = 7824385902876518494L;

    public static void assign(){
      DataPackable.assignType(typeID, args -> new Warning());
    }

    {
      duration = -1;
    }

    //Internal usage
    Warning(){
      super("", "");
    }

    public Warning(String title, String information){
      super(title, information);
    }

    @Override
    public Drawable getIcon(){
      return Icon.warning;
    }

    @Override
    public Color getIconColor(){
      return Tmp.c1.set(Color.crimson).lerp(Color.white, Mathf.absin(10, 1));
    }

    @Override
    public Color getTitleColor(){
      return SglDrawConst.fexCrystal;
    }

    @Override
    public long typeID(){
      return typeID;
    }
  }

}
