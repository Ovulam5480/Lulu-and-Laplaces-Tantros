package LLL.entities.comp.unitcomps;

import LLL.world.neoplasm.effect.*;
import ent.anno.*;
import mindustry.gen.*;
import mindustry.type.*;

@Annotations.EntityComponent
public abstract class NeuronUnitComp implements Unitc{
  @Annotations.Import
  UnitType type;
  public NeoplasmNeuron.NeoplasmNeuronBuild neuron;
}
