package LLL.toMigration.Ovulam.world.type;

import arc.struct.*;
import mindustry.type.*;

public class Recipe{
  public Seq<ItemStack> itemStacks;
  public Seq<LiquidStack> liquidStacks;
  public Seq<RecipePayloadManager> payloadManagers;
  public float power;
  public boolean liquidCompletely;

  //todo contentStacks?
  public Recipe(Seq<ItemStack> itemStacks, Seq<LiquidStack> liquidStacks, Seq<RecipePayloadManager> payloadManagers,
                float power, boolean liquidCompletely){
    this.itemStacks = itemStacks;
    this.liquidStacks = liquidStacks;
    this.payloadManagers = payloadManagers;
    this.power = power;
    this.liquidCompletely = liquidCompletely;
  }

  public Seq<PayloadStack> payloadStacks(){
    return RecipePayloadManager.getPayloadStacks(payloadManagers);
  }
}