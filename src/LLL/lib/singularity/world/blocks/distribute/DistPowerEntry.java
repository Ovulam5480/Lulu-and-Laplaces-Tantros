package LLL.lib.singularity.world.blocks.distribute;

public class DistPowerEntry extends DistEnergyEntry{
  public float consPower;
  public float eneProd;

  public DistPowerEntry(String name){
    super(name);

    hasPower = consumesPower = true;
    buildType = DistPowerEntryBuild::new;
  }

  @Override
  public void init(){
    super.init();
    newConsume();
    consume.power(consPower);
  }

  public class DistPowerEntryBuild extends DistEnergyEntryBuild{
    public float energyProduct;

    @Override
    public void updateTile(){
      super.updateTile();
      energyProduct = eneProd * power.status;
    }

    @Override
    public float matrixEnergyProduct(){
      return energyProduct;
    }
  }
}
