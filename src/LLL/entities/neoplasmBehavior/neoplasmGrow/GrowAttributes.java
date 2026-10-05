package LLL.entities.neoplasmBehavior.neoplasmGrow;

import LLL.entities.neoplasmBehavior.*;
import LLL.graphics.*;
import LLL.lib.gdxAI.btree.*;
import LLL.util.struct.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.world.*;

//在特定地形属性生长器官
public class GrowAttributes extends GrowLeafTask{
  public GridIntMap<Float> attributes = new GridIntMap<>();
  public Boolf3<Tile, Block, Team> getAttribute;
  public boolean removeFailed = true;

  public GrowAttributes(Block block, Boolf<Tile> getAttribute){
    this(block, (t, b, te) -> getAttribute.get(t));
  }

  public GrowAttributes(Block block, Boolf3<Tile, Block, Team> getAttribute){
    this.block = block;
    this.getAttribute = getAttribute;

//        Color color = new Color(Color.red).shiftHue(Mathf.random(360f)).a(0.4f);
//        Events.run(EventType.Trigger.draw, () -> {
//            OvulamDraw.drawCameraTiles(color, t -> attributes.containsKey(t.x, t.y));
//        });
  }

  public void handleStructureChange(Tile tile, boolean add){
    if(add){
      if(getAttribute.get(tile, block, neuron.team)){
        attributes.put(tile.x, tile.y, 1f);
      }
    }else{
      attributes.remove(tile.x, tile.y);
    }
  }

  @Override
  public Status grow(){
    int max = 5;
    while(max > 0 && attributes.size() > 0){
      Tile tile = Vars.world.tile(attributes.random().key);
      //Log.info("put: " + neuron.cortex.structure.containsKey(tile.x, tile.y)); //todo fix -> cortox updateCP

      if(getObject().tryPlaceBlock(tile, block)){
        attributes.remove(tile.x, tile.y);
        return Status.SUCCEEDED;
      }else if(removeFailed){
        attributes.remove(tile.x, tile.y);
      }

      max--;
    }

    return Status.FAILED;
  }

  @Override
  public void write(Writes write){
    write.i(attributes.size());
    attributes.each((x, y, value) -> {
      write.i(Point2.pack(x, y));
      write.f(value);
    });
  }

  @Override
  public void read(Reads read){
    int size = read.i();
    for(int i = 0; i < size; i++){
      int xy = read.i();
      attributes.put(Point2.x(xy), Point2.y(xy), read.f());
    }
  }

  @Override
  protected Task<NeoplasmBehavior> copyTo(Task<NeoplasmBehavior> task){
    GrowAttributes growTask = (GrowAttributes)task;
    attributes.each((x, y, value) -> growTask.attributes.put(x, y, value));

    return growTask;
  }

  @Override
  public void reset(){
    super.reset();
    attributes.clear();
  }

  @Override
  public void draw(){
    Draw.color(Color.acid, 0.7f);
    OvulamDraw.eachCameraTiles(t -> {
      if(attributes.containsKey(t.x, t.y)){
        Fill.rect(t.worldx(), t.worldy(), 8, 8);
      }
    });
  }
}
