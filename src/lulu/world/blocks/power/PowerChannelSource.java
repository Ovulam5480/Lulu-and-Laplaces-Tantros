package lulu.world.blocks.power;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import lulu.content.*;
import lulu.ctype.*;
import lulu.world.*;
import lulu.world.components.blockComps.*;
import lulu.world.components.buildComps.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import universecore.annotations.*;

@Annotations.ImplEntries
public class PowerChannelSource extends PowerNode implements PowerChannelBlockComp{
  public float defaultPower = 10000f;

  public PowerChannelSource(String name){
    super(name);
    maxNodes = 100;
    outputsPower = true;
    consumesPower = false;
    drawDisabled = true;
    envEnabled = Env.any;
    buildVisibility = BuildVisibility.sandboxOnly;
    configurable = true;

    // 块级标志，对应 PowerChannelComp 的 @BindField setter
    producesPowerChannel(true);
    consumesPowerChannel(false);
    bufferedPowerChannel(false);
  }

  @Annotations.ImplEntries
  public class PowerChannelSourceBuild extends PowerNodeBuild implements PowerChannelBuildComp{
    private boolean[] channelEnabled;
    private float[] channelValues;

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);
      initArrays();
      createPowerChannelBuild();
      return this;
    }

    private void initArrays(){
      int n = OvulamContents.powers().size;
      if(channelEnabled == null || channelEnabled.length != n){
        channelEnabled = new boolean[n];
        channelValues = new float[n];
        for(int i = 0; i < n; i++) channelValues[i] = defaultPower;
      }
    }

    @Override
    public Power[] getPowerChannel(){
      return OvulamContents.powers().toArray(Power.class);
    }

    @Override
    public float getPowerProduce(int id){ return channelEnabled[id] ? channelValues[id] : 0f; }

    @Override
    public float getPowerConsume(int id){ return 0f; }

    @Override
    public void buildConfiguration(Table table){
      Seq<Power> all = OvulamContents.powers();
      for(int i = 0; i < all.size; i++){
        int id = i;
        Power p = all.get(i);
        table.table(Styles.black5, t -> {
          t.check(p.localizedName, channelEnabled[id], b -> { channelEnabled[id] = b; sync(); }).left();

          TextField tf = new TextField(String.valueOf(channelValues[id]));
          tf.setFilter((f, c) -> Character.isDigit(c) || c == '.' || c == '-');
          tf.changed(() -> {
            if(!tf.getText().isEmpty()){
              channelValues[id] = Strings.parseFloat(tf.getText(), 0f);
              sync();
            }
          });
          t.add(tf).width(120);
        }).row();
      }
    }

    @Override
    public void configured(Unit builder, Object value){
      if(value instanceof Object[] a){
        channelEnabled = (boolean[]) a[0];
        channelValues = (float[]) a[1];
        rebuildGraph();
      }else{
        super.configured(builder, value);
      }
    }

    private void sync(){ configure(new Object[]{ channelEnabled.clone(), channelValues.clone() }); }

    private void rebuildGraph(){
      PowerGraph g = power().graph;
      if(g != null) g.remove(self());
      power().init = false;
      power().graph = null;
      Building found = null;
      Seq<Building> out = new Seq<>();
      for(Building o : getPowerConnections(out)){
        if(o != null && o.power != null && o.power.graph != null){ found = o; break; }
      }
      if(found != null) found.power.graph.reflow(self());
      else { OvulamPowerGraph ng = new OvulamPowerGraph(); ng.checkAdd(); ng.add((Building) self()); ng.update(); }
    }

    @Override
    public void write(Writes write){
      super.write(write);
      write.b(channelEnabled.length);
      for(boolean b : channelEnabled) write.bool(b);
      for(float v : channelValues) write.f(v);
    }

    @Override
    public void read(Reads read, byte revision){
      super.read(read, revision);
      int n = read.b();
      channelEnabled = new boolean[n];
      for(int i = 0; i < n; i++) channelEnabled[i] = read.bool();
      channelValues = new float[n];
      for(int i = 0; i < n; i++) channelValues[i] = read.f();
    }
  }
}
