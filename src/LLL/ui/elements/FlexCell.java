package LLL.ui.elements;

import arc.func.*;
import arc.scene.*;
import arc.util.*;

public class FlexCell<E extends Element>{
  E element;
  float padLeft, padRight, padTop, padBottom;
  float minWidth, maxWidth;
  float minHeight, maxHeight;
  float expandX, expandY;
  float fillX, fillY;
  boolean isRow;
  int alignChild = Align.center;

  public FlexCell<E> update(Cons<E> cons){
    element.update(() -> cons.get(element));

    return this;
  }
}
