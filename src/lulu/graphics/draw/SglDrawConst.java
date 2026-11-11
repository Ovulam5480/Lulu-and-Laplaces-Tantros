package lulu.graphics.draw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.util.*;
import lulu.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SglDrawConst{
  public static final TextureRegion[] EMP_REGIONS = new TextureRegion[0];

  //Colors
  public static final Color transColor = new Color(0, 0, 0, 0);
  public static final Color fexCrystal = Color.valueOf("FF9584");
  public static final Color matrixNet = Color.valueOf("D3FDFF");
  public static final Color matrixNetDark = Color.valueOf("9ECBCD");
  public static final Color ion = Color.valueOf("#D1D19F");
  public static final Color dew = Color.valueOf("ff6214");
  public static final Color frost = Color.valueOf("AFF7FF");
  public static final Color winter = Color.valueOf("6CA5FF");

  //Text colors
  public static final String COLOR_ACCENT = "[accent]";

  //Texture regions
  public static Drawable time, techPoint, inspire,
    grayUI, padGrayUI, darkgrayUI,
    grayUIAlpha, padGrayUIAlpha, darkgrayUIAlpha;

  public static void load(){
    time = ModFiles.getModDrawable("time");
    techPoint = ModFiles.getModDrawable("tech_point");
    inspire = ModFiles.getModDrawable("inspire");

    grayUI = ((TextureRegionDrawable)Tex.whiteui).tint(Pal.darkerGray);
    padGrayUI = ((TextureRegionDrawable)Tex.whiteui).tint(Pal.darkerGray);
    padGrayUI.setLeftWidth(8);
    padGrayUI.setRightWidth(8);
    padGrayUI.setTopHeight(8);
    padGrayUI.setBottomHeight(8);
    darkgrayUI = ((TextureRegionDrawable)Tex.whiteui).tint(Pal.darkestGray);

    grayUIAlpha = ((TextureRegionDrawable)Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
    padGrayUIAlpha = ((TextureRegionDrawable)Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
    padGrayUIAlpha.setLeftWidth(8);
    padGrayUIAlpha.setRightWidth(8);
    padGrayUIAlpha.setTopHeight(8);
    padGrayUIAlpha.setBottomHeight(8);
    darkgrayUIAlpha = ((TextureRegionDrawable)Tex.whiteui).tint(Tmp.c1.set(Pal.darkestGray).a(0.7f));
  }
}
