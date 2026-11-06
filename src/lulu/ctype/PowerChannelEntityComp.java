package lulu.ctype;

import arc.util.*;
import lulu.content.*;
import lulu.world.modules.*;

public interface PowerChannelEntityComp{
  default Power[] getPowerChannel(){
    return new Power[]{OvulamResource.power};
  }

  PowerChannelModule power();

  PowerChannelComp getPowerChannelComp();

  float getPowerConsume(int id);

  float getPowerProduce(int id);

  float getPowerCapacity(int id);

  float getPowerStored(int id);

  float getTotalPowerCapacity(int id);

  default float getDelta(){
    return Time.delta;
  }

  boolean shouldConsumePower();

  boolean shouldProducePower();
}
