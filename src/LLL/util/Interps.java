package LLL.util;

import arc.math.*;

public class Interps{
  public static Interp v = a -> {
    a *= 2;
    a -= 1;
    return a * a;
  };

  public static Interp A = a -> {
    a *= 2;
    a -= 1;
    return 1 - a * a;
  };
}
