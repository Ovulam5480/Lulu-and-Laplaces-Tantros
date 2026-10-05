package LLL.lib.singularity.ui;

import LLL.lib.singularity.graphic.*;
import LLL.lib.singularity.ui.fragments.entityinfo.*;
import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

import static LLL.lib.singularity.ui.SglUI.*;
import static mindustry.gen.Tex.*;

public class SglStyles{
  public static TextureRegionDrawable BLUR_BACK;

  public static Slider.SliderStyle sliderLine;
  public static Button.ButtonStyle underline, sideButtonRight;
  public static Dialog.DialogStyle blurBack, transparentBack, transGrayBack;

  public static void load(){
    HealthBarStyle.loadAll();

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

    sliderLine = new Slider.SliderStyle(){{
      background = Core.atlas.drawable("singularity-slider_line_back");
      knob = sliderKnob;
      knobOver = sliderKnobOver;
      knobDown = sliderKnobDown;
    }};

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

    transparentBack = new Dialog.DialogStyle(){{
      stageBackground = SglDrawConst.transparent;
      titleFont = Fonts.outline;
      background = SglDrawConst.transparent;
      titleFontColor = Pal.accent;
    }};

    transGrayBack = new Dialog.DialogStyle(){{
      stageBackground = SglDrawConst.grayUIAlpha;
      titleFont = Fonts.outline;
      background = windowEmpty;
      titleFontColor = Pal.accent;
    }};
  }
}
