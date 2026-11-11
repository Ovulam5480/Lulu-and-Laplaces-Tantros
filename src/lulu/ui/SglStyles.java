package lulu.ui;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static lulu.ui.SglUI.*;
import static mindustry.gen.Tex.*;

public class SglStyles{
  public static TextureRegionDrawable BLUR_BACK;

  public static Button.ButtonStyle underline, sideButtonRight;
  public static Dialog.DialogStyle blurBack, transparentBack;

  public static void load(){
    BLUR_BACK = new TextureRegionDrawable(Core.atlas.white()){
      @Override
      public void draw(float x, float y, float width, float height){
        uiBlur.directDraw(() -> super.draw(x, y, width, height));

        Styles.black5.draw(x, y, width, height);
      }

      @Override
      public void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation){
        uiBlur.directDraw(() -> super.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation));

        Styles.black5.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
      }
    };

    underline = new Button.ButtonStyle(){{
      up = Tex.underline;
      down = underlineWhite;
      over = underlineOver;
    }};

    sideButtonRight = new Button.ButtonStyle(){{
      up = Tex.buttonSideRight;
      down = Tex.buttonSideRightDown;
      over = Tex.buttonSideRightOver;
    }};

    blurBack = new Dialog.DialogStyle(){{
      stageBackground = BLUR_BACK;
      titleFont = Fonts.def;
      background = windowEmpty;
      titleFontColor = Pal.accent;
    }};
  }
}
