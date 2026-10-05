package LLL.world.neoplasm.effect;

import LLL.content.*;
import LLL.content.blocks.*;
import LLL.type.*;
import LLL.type.neoplasm.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class NeoplasmNeuron extends NeoplasmGanglion implements NeoplasmBlockModule, TrophosomeTargetBlockModule{
  public float[] pixDis = {30, 51, 73, 95, 116, 134};

  public TextureRegion[] neuronPetals;
  public TextureRegion centerRegion;

  public NeoplasmVessel vessel = (NeoplasmVessel)Neoplasm.neoplasmVessel;
  public float neoplasmProduce = 2000f;
  public float produceInterval = 1f;

  public Func2<Float, Float, Position> nodeConstructor = Vec2::new;

  public Color bottomPetals = Color.valueOf("9c3040");
  public int priority = -1;
  public float maxHealOnce = 12f;

  public NeoplasmNeuron(String name){
    super(name);

    size = 6;
    liquidCapacity = 3000f;

    hasItems = true;
    itemCapacity = 1000;

    maxHealOnce = 10f;

    clipSize = 99999;

    configurable = true;
  }

  @Override
  public void load(){
    super.load();

    neuronPetals = new TextureRegion[pixDis.length];
    for(int i = 0; i < pixDis.length; i++){
      neuronPetals[i] = Core.atlas.find(name + "-petal-" + i);
    }

    centerRegion = Core.atlas.find(name + "-center");
  }

  @Annotations.ImplEntries
  public class NeoplasmNeuronBuild extends NeoplasmGanglionBuild implements NeoplasmBuildModule, TrophosomeTargetBuildModule{
    public boolean noEffect = false;
    public float produceTimer = 0;

    public PrefrontalCortex cortex;
    public Seq<Unit> requires = new Seq<>();
    public Seq<Visible> visibles = new Seq<>();
    public int currentVisible = -1;

    @Override
    public boolean canAcceptPayload(UnlockableContent type){
      return OvulamUnitTypes.trophosomeUnitTypes.containsValue(type, true);
    }

    @Override
    public int getPayloadCount(UnlockableContent type){
      return 0;
    }

    @Override
    public int getPayloadCapacity(UnlockableContent type){
      return 999;
    }

    @Override
    public boolean isSourceOrgan(){
      return true;
    }

    @Override
    public void handlePayload(Building source, Payload payload){
      ItemStack[] stacks = payload.requirements();

      for(ItemStack stack : stacks){
        handleStack(stack.item, stack.amount, source);
      }
    }

    @Override
    public NeoplasmBuildModule parent(){
      return this;
    }

    @Override
    public void updateTile(){
      produceTimer += delta();
      if(produceTimer >= produceInterval){
        neoplasm().addClamp(neoplasmProduce, liquidCapacity);
        produceTimer = 0;
      }
      cortex.update();
    }

    @Override
    public void buildConfiguration(Table table){
      table.table(Styles.black5, t -> {
        ButtonGroup<ImageButton> group = new ButtonGroup<>();
        group.setMinCheckCount(0);

        for(int i = 0; i < visibles.size; i++){
          int finalI = i;
          ImageButton button = t.button(Icon.eye, Styles.clearNoneTogglei, () -> {
            currentVisible = currentVisible == finalI ? -1 : finalI;
          }).checked(ib -> currentVisible == finalI).get();
          group.add(button);

          Vars.ui.addDescTooltip(button, visibles.get(i).description());

          if((i + 1) % 5 == 0){
            t.row();
          }
        }
      }).margin(8);
    }

    @Override
    public void updateNeoplasmScale(){
      setNeoplasmScale(Mathf.approach(neoplasmScale(), cortex.underStressed ? Mathf.sin(Time.time, scl / 2, 0.1f) + 1 : 1f, 0.01f * Time.delta));
    }

    public void stressTrigger(){
    }

    @Override
    public void draw(){
      Draw.rect(baseRegion(), x, y);

      float z = Draw.z();

      Draw.z(z + 0.15f);
      //Draw.rect(centerRegion, x, y);

      Vec2 v2 = Tmp.v2;

      for(int i = 0; i < 12; i++){
        v2.setZero();

        for(int j = 0; j < pixDis.length; j++){
          TextureRegion petal = neuronPetals[j];

          Vec2 v1 = Tmp.v1.set(pixDis[j] / 4f * neoplasmScale(), 0).rotate(i * 30 + Mathf.sin(Time.time - j * 5, scl, 25 + j * 7));
          Draw.mixcol(bottomPetals, 1);
          Draw.z(z + 0.1f);
          Vec2 v3 = Tmp.v3.set(v1).sub(v2).setLength(v1.len() - v2.len());

          Draw.scl(1.6f * neoplasmScale());
          Draw.rect(petal, v2.x * 1.3f + v3.x + x, v2.y * 1.3f + v3.y + y, (v3.angle() - 90));

          Draw.scl(neoplasmScale());
          Draw.mixcol();
          Draw.z(z + 0.2f);
          Draw.rect(petal, -(v2.x + v3.x) + x, v2.y + v3.y + y, (90 - v3.angle()));

          v2.set(v1);
        }
      }
      Draw.reset();

      Draw.z(z + 0.3f);

      Draw.z(Layer.flyingUnit);
      if(currentVisible > -1){
        visibles.get(currentVisible).draw();
      }
    }

    @Override
    public void created(){
      super.created();

      cortex = new PrefrontalCortex(this);
      cortex.handleOwnedBlock(this, tileX(), tileY(), getBuilding().rotation, true);
      cortex.handleSource(this);
      setNeoplasmScale(1);
    }

    @Override
    public void remove(){
      cortex.remove();

      super.remove();
    }

    @Override
    public NeoplasmNeuronBuild neuron(){
      return this;
    }

    @Override
    public boolean neuronValid(){
      return true;
    }

    @Override
    public PrefrontalCortex cortex(){
      return cortex;
    }

    @Override
    public boolean neoplasmActivity(){
      return true;
    }

    @Override
    public void write(Writes write){
      super.write(write);

      cortex.write(write);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);

      cortex.read(read);
    }

    @Override
    public void addToWrite(Seq<Entityc> toWrite){
      cortex.addToWrite(toWrite);
    }

    @Override
    public void getFromRead(Queue<Entityc> toRead){
      cortex.getFromRead(toRead);
    }

    @Override
    public void readPayloads(Reads read, byte revision){
    }

    @Override
    public void writePayloads(Writes write){
    }
  }
}
