package LLL.lib.singularity.world.distribution;

import arc.*;

public enum GridChildType{
  output,
  input,
  acceptor,
  container;

  public String locale(){
    return Core.bundle.get("misc." + name());
  }
}
