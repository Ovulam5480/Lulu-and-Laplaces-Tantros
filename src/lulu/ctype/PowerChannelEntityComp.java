package lulu.ctype;

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

  float getDelta();

  boolean shouldConsumePower();

  boolean shouldProducePower();
}
