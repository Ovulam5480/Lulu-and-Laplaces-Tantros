package LLL.type.neoplasm;

import LLL.graphics.*;
import LLL.type.*;
import LLL.util.struct.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.ui.*;
import mindustry.world.*;

import java.io.*;

public class NeoplasmPermeate implements SaveFileReader.CustomChunk, Visible{
  static{
    SaveVersion.addCustomChunk("neoplasmPermeate", new NeoplasmPermeate());
  }

  private final static int[] d4x = {1, 0, -1, 0};
  private final static int[] d4y = {0, 1, 0, -1};

  private static final int[] d8x = {1, 1, 0, -1, -1, -1, 0, 1};
  private static final int[] d8y = {0, 1, 1, 1, 0, -1, -1, -1};

  private static final Point2[] territoryCircle = {
    new Point2(1, 0), new Point2(1, 1), new Point2(0, 1), new Point2(-1, 1),
    new Point2(-1, 0), new Point2(-1, -1), new Point2(0, -1), new Point2(1, -1),
    new Point2(2, 0), new Point2(2, 1), new Point2(1, 2), new Point2(0, 2),
    new Point2(-1, 2), new Point2(-2, 1), new Point2(-2, 0), new Point2(-2, -1),
    new Point2(-1, -2), new Point2(0, -2), new Point2(1, -2), new Point2(2, -1)
  };

  //territory
  private static final GridIntMap<NeoplasmNeuron.NeoplasmNeuronBuild> territory = new GridIntMap<>();
  //stemCell
  private final GridIntMap<Tile> stemCell = new GridIntMap<>();
  //瘤液血管的存在区域
  private final GridIntMap<Integer> structure;

  private NeoplasmNeuron.NeoplasmNeuronBuild neuron;

  private static GridBits visited = new GridBits(Vars.world.width(), Vars.world.height());
  private final static Seq<Tile> stack = new Seq<>();

  static{
    Events.on(EventType.ResetEvent.class, e -> {
      territory.clear();
    });

    Events.on(EventType.WorldLoadEvent.class, e -> {
      visited = new GridBits(Vars.world.width(), Vars.world.height());
    });
  }

  public NeoplasmPermeate(NeoplasmNeuron.NeoplasmNeuronBuild neuron, GridIntMap<Integer> structure){
    this.neuron = neuron;
    this.structure = structure;

    int x = neuron.tileX();
    int y = neuron.tileY();

    structure.put(x, y, 0);
    addTerritory(x, y);

    for(int k = 0; k < 4; k++){
      int nx = x + d4x[k], ny = y + d4y[k];

      if(contain(nx, ny)){
        stemCell.put(nx, ny, Vars.world.tile(nx, ny));
      }
    }

    initVisible();
  }

  public NeoplasmPermeate(){
    structure = new GridIntMap<>();
  }

  void requestStructure(int x, int y, boolean stemCell){
    int index = getMinIndexD4(structure, x, y);
    if(index == 99999) return;

    structure.put(x, y, index + 1);

    if(stemCell){
      addStemCellNear(x, y);
    }
  }

  public void drawPermate(){
    Draw.z(Layer.flyingUnitLow - 5);
    Draw.color(Color.white);

    Font f = Fonts.outline;

    f.getData().setScale(0.1f);

    OvulamDraw.eachCameraTiles(tile -> {
      if(territory.containsKey(tile.x, tile.y)){
        Draw.alpha(0.2f);
        Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
      }

      if(structure.containsKey(tile.x, tile.y)){
        Draw.alpha(0.6f);
        Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
        Draw.alpha(1);
        f.draw(structure.get(tile.x, tile.y) + "", tile.x * 8, tile.y * 8, Align.center);
      }
    });

    f.getData().setScale(1f);

    Draw.color(Color.acid, 0.4f);
    for(Tile tile : stemCell.values().toArray()){
      Fill.square(tile.x * 8, tile.y * 8, 4);
    }
  }

  private void addTerritory(int x, int y){
    for(Point2 point2 : territoryCircle){
      territory.putIfAbsent(x + point2.x, y + point2.y, neuron);
    }
  }

  void clearTerritory(){
    for(IntMap.Entry<NeoplasmNeuron.NeoplasmNeuronBuild> entry : territory.map){
      if(entry.value == neuron){
        territory.map.remove(entry.key);
      }
    }
  }

  void updatePermeate(Func2<PermeateState, Tile, ResultState> growValid){
    if(stemCell.size() == 0){
      growValid.get(PermeateState.fail, null);
      return;
    }

    Tile tile = stemCell.random().value;
    int x = tile.x;
    int y = tile.y;

    boolean hasActivity = false;
    boolean hasValid = false;
    boolean hasNear = false;

    int[] index = {-1, -1};
    boolean isDisconnected = false;
    int type = 0;

    for(int k = 0; k < 4; k++){
      int nx = x + d4x[k], ny = y + d4y[k];

      if(contain(nx, ny)){
        Building b = Vars.world.build(nx, ny);
        if(b instanceof NeoplasmBuildModule m){
          if(m.neuron() == neuron){
            if(m.neoplasmActivity()){
              hasActivity = true;
            }

            if(!b.isHealSuppressed() && m.neuronValid()){
              hasValid = true;
            }

            hasNear = true;
          }

          int curType = Mathf.sign(m.neuronValid());

          if(structure.containsKey(nx, ny)){
            if(index[0] == -1){
              index[0] = structure.get(nx, ny);
              type = curType;
            }else if(curType + type == 0){//断开的位置
              index[1] = structure.get(nx, ny);
              isDisconnected = true;
            }
          }
        }
      }
    }

    if(!hasValid){
      growValid.get(PermeateState.fail, tile);
      if(!hasNear){
        if(neuron.cortex.underStressed){
          stemCell.remove(x, y);
        }else{
          neuron.cortex.handleStress();
        }
      }
      return;
    }

    stemCell.remove(x, y);

    if(!hasActivity){
      growValid.get(PermeateState.fail, tile);
      return;
    }

    if(!isDisconnected){
      int[] counts = new int[5]; // sameO, diffO, sameD, diffD, numO
      countOD(structure, x, y, counts);
      int sameO = counts[0], diffO = counts[1], sameD = counts[2], diffD = counts[3], numO = counts[4];

      if(!((sameO > diffO) || (sameO == diffO && sameD > diffD))
        || ringFlips(structure, x, y) > 2 * numO - 4
        || (sameO == 4 && !structure.containsKey(x, y))){
        growValid.get(PermeateState.fail, tile);
        return;
      }
    }else{
      structure.put(x, y, Math.min(index[0], index[1]) + 1);
    }

    addTerritory(x, y);

    if(isDisconnected || tryFlipKeepIfGood(structure, x, y)){
      ResultState result = growValid.get(structure.containsKey(x, y) ? PermeateState.grow : PermeateState.suicide, tile);

      if(result == ResultState.valid){
        if(isDisconnected) addBranch(tile);
        else addStemCellNear(x, y);
      }else if(result == ResultState.obstruct){
        stemCell.put(x, y, tile);
        structure.remove(x, y);
      }else{
        structure.remove(x, y);
      }
      return;
    }

    growValid.get(PermeateState.fail, tile);
  }

  private void addStemCellNear(int x, int y){
//        for (int k = 0; k < 8; k++){
//            int nx = x + d8x[k], ny = y + d8y[k];
//
//            if (contain(nx, ny) && territory.get(nx, ny) == neuron) {
//                stemCell.put(nx, ny, Vars.world.tile(nx, ny));
//            }
//        }
    for(int k = 0; k < 4; k++){
      int nx = x + d4x[k], ny = y + d4y[k];

      if(contain(nx, ny) && territory.get(nx, ny) == neuron){
        stemCell.put(nx, ny, Vars.world.tile(nx, ny));
      }
    }
  }

  void getRemoved(NeoplasmBuildModule build){
    if(build.neuron().isAdded()){
      //外围
      for(Point2 edge : build.getBlock().getEdges()){
        int nx = build.getTile().x + edge.x, ny = build.getTile().y + edge.y;

        if(!contain(nx, ny)) continue;

        Tile toAdd = Vars.world.tile(build.getTile().x + edge.x, build.getTile().y + edge.y);

        stemCell.put(toAdd.x, toAdd.y, toAdd);
      }

      int size = build.getBlock().size;
      //内围
      for(int i = 0; i < size * 4; i++){
        if(i % size == i / 2) continue;

        Point2 edge = build.getBlock().getInsideEdges()[i];
        int nx = build.getTile().x + edge.x, ny = build.getTile().y + edge.y;

        if(!contain(nx, ny)) continue;

        Tile toAdd = Vars.world.tile(nx, ny);
        stemCell.put(toAdd.x, toAdd.y, toAdd);
      }
    }

    removeBranch(build.getTile());

    build.getTile().getLinkedTilesAs(build.getBlock(), stack);
    stack.each(t -> {
      structure.remove(t.x, t.y);
    });
  }

  void addBranch(Tile t){
    stack.clear();
    visited.clear();

    Seq<Building> added = new Seq<>();

    int fromX = t.x;
    int fromY = t.y;

    stack.add(t);
    added.add(t.build);
    visited.set(t.x, t.y, true);

    while(!stack.isEmpty()){
      Tile current = stack.pop();
      int cx = current.x;
      int cy = current.y;

      if(!structure.containsKey(cx, cy)){
        continue;
      }

      for(int k = 0; k < 4; k++){
        int nx = cx + Geometry.d4x(k);
        int ny = cy + Geometry.d4y(k);

        if(nx == fromX && ny == fromY){
          continue;
        }

        Tile n = Vars.world.tile(nx, ny);

        if(!visited.get(nx, ny)){
          visited.set(nx, ny, true);

          if(!structure.containsKey(nx, ny)){
            continue;
          }

          if(!(n.build instanceof NeoplasmBuildModule m)){
            n.getLinkedTilesAs(n.block(), tile -> {
              structure.remove(tile.x, tile.y);
            });
            stemCell.put(nx, ny, n);
            continue;
          }

          if(m.getBlock().isMultiblock() || !m.neuronValid()){
            if(!m.neuronValid()){
              m.setNeuronValid(true);
              added.add(m.getBuilding());
            }

            structure.put(nx, ny, structure.get(cx, cy) + 1);
            stack.add(n);
          }
        }
      }

      fromX = cx;
      fromY = cy;
    }

    added.each(Building::onProximityUpdate);
  }

  void removeBranch(Tile t){
    if(structure.containsKey(t.x, t.y)){
      stack.clear();
      visited.clear();

      int fromX = t.x;
      int fromY = t.y;

      stack.add(t);
      visited.set(t.x, t.y, true);

      while(!stack.isEmpty()){
        Tile current = stack.pop();
        int cx = current.x;
        int cy = current.y;

        for(int k = 0; k < 4; k++){
          int nx = cx + Geometry.d4x(k);
          int ny = cy + Geometry.d4y(k);

          if(nx == fromX && ny == fromY){
            continue;
          }

          if(!visited.get(nx, ny)){
            visited.set(nx, ny, true);

            if(structure.get(nx, ny, -1) > structure.get(t.x, t.y)){
              Tile tile = Vars.world.tile(nx, ny);
              stack.add(tile);

              if(tile.build instanceof NeoplasmBuildModule m){
                m.setNeuronValid(false);
              }
            }
          }
        }

        fromX = cx;
        fromY = cy;
      }

      structure.remove(t.x, t.y);
    }
  }

  public boolean hasStemCell(){
    return stemCell.size() > 0;
  }

  private static boolean contain(int x, int y){
    return x >= 0 && x < Vars.world.width() && y >= 0 && y < Vars.world.height();
  }

  @Override
  public void draw(){
    Draw.color(Color.white);

    Font f = Fonts.outline;
    f.getData().setScale(0.1f);

    OvulamDraw.eachCameraTiles(tile -> {
      if(territory.containsKey(tile.x, tile.y)){
        Draw.alpha(0.2f);
        Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
      }

      if(structure.containsKey(tile.x, tile.y)){
        Draw.alpha(0.6f);
        Fill.rect(tile.worldx(), tile.worldy(), 8, 8);
        Draw.alpha(1);
        f.draw(structure.get(tile.x, tile.y) + "", tile.x * 8, tile.y * 8, Align.center);
      }
    });

    f.getData().setScale(1f);

    Draw.color(Color.acid, 0.4f);
    for(Tile tile : stemCell.values().toArray()){
      Fill.square(tile.x * 8, tile.y * 8, 4);
    }
  }

  @Override
  public void initVisible(){
    neuron.visibles.add(this);
  }

  @Override
  public String description(){
    return "显示瘤液的蔓延";
  }

  public enum PermeateState{
    fail,
    grow,
    suicide
  }

  public enum ResultState{
    valid,
    invalid,
    obstruct
  }

  private static void countOD(GridIntMap<Integer> grid, int x, int y, int[] counts){
    boolean col = grid.containsKey(x, y);
    int sameO = 0, diffO = 0, sameD = 0, diffD = 0, numO = 0;

    for(int k = 0; k < 4; k++){
      int nx = x + d4x[k], ny = y + d4y[k];
      if(contain(nx, ny)){
        numO++;
        if(grid.containsKey(nx, ny) == col){
          sameO++;
        }else{
          diffO++;
        }
      }
    }

    for(int k = 0; k < 4; k++){
      int nx, ny;
      if(k == 0){
        nx = x - 1;
        ny = y - 1;
      }else if(k == 1){
        nx = x - 1;
        ny = y + 1;
      }else if(k == 2){
        nx = x + 1;
        ny = y + 1;
      }else{ //k == 3
        nx = x + 1;
        ny = y - 1;
      }

      if(contain(nx, ny)){
        if(grid.containsKey(nx, ny) == col){
          sameD++;
        }else{
          diffD++;
        }
      }
    }

    counts[0] = sameO;
    counts[1] = diffO;
    counts[2] = sameD;
    counts[3] = diffD;
    counts[4] = numO;
  }

  private static int ringFlips(GridIntMap<Integer> grid, int x, int y){
    int ringBits = 0;
    int validCount = 0;

    for(int k = 0; k < 8; k++){
      int nx = x + d8x[k], ny = y + d8y[k];
      if(contain(nx, ny)){
        if(grid.containsKey(nx, ny)){
          ringBits |= (1 << validCount);
        }
        validCount++;
      }
    }

    if(validCount < 2){
      return 0;
    }

    int flips = 0;
    for(int i = 0; i < validCount; i++){
      int currentBit = (ringBits >> i) & 1;
      int nextBit = (ringBits >> ((i + 1) % validCount)) & 1;

      if(currentBit != nextBit){
        flips++;
      }
    }

    return flips;
  }

  private static boolean tryFlipKeepIfGood(GridIntMap<Integer> grid, int x, int y){
    boolean remove = grid.containsKey(x, y);
    boolean ok;

    if(remove){
      //将该处1换为0, 并且检查附近的1是否出现断链
      grid.remove(x, y);
      ok = oneConnected4(grid, x, y);
    }else{
      //将该处0换为1, 并且检查附近的0不会出现断链
      grid.put(x, y, getMinIndexD4(grid, x, y) + 1);
      ok = zeroConnected4(grid, x, y);
    }

    if(!ok){
      if(remove){
        grid.put(x, y, getMinIndexD4(grid, x, y) + 1);
      }else{
        grid.remove(x, y);
      }
    }

    return ok;
  }

  //检查周围的1与索引是否匹配
  private static boolean oneConnected4(GridIntMap<Integer> grid, int x, int y){
    for(int i = 0; i < 4; i++){
      int nx = x + d4x[i], ny = y + d4y[i];
      if(!grid.containsKey(nx, ny)) continue;

      int cur = grid.get(nx, ny);
      boolean has = false;

      for(int j = 0; j < 4; j++){
        int nx2 = nx + d4x[j], ny2 = ny + d4y[j];
        if(!grid.containsKey(nx2, ny2) || (Math.abs(i - j) == 2)) continue;

        int next = grid.get(nx2, ny2);

        if(cur - 1 == next){
          has = true;
          break;
        }
      }

      if(!has) return false;
    }

    return true;
  }

  //附近的0均保持互相连接
  private static boolean zeroConnected4(GridIntMap<Integer> grid, int x, int y){
    int parent = -1;
    int current = grid.get(x, y);

    for(int i = 0; i < 4; i++){
      int nx = x + d4x[i], ny = y + d4y[i];

      if(grid.get(nx, ny, 99999) < current){
        parent = i;
      }
    }

    int d82 = (parent * 2 + 4) % 8;
    int d81 = (parent * 2 + 3) % 8;
    int d83 = (parent * 2 + 5) % 8;
    return contain(x + d8x[d81], y + d8y[d81])
      && hasVacancy(grid, x + d8x[d81], y + d8y[d81])
      && contain(x + d8x[d83], y + d8y[d83])
      && hasVacancy(grid, x + d8x[d83], y + d8y[d83])
      && hasVacancy(grid, x + d8x[d82], y + d8y[d82]);
  }

  private static boolean hasVacancy(GridIntMap<Integer> grid, int x, int y){
    return !(grid.containsKey(x, y) && Vars.world.build(x, y) instanceof NeoplasmBuildModule m && m.neuron() != null);
  }

  private static int getMinIndexD4(GridIntMap<Integer> grid, int x, int y){
    int index = 99999;

    for(int k = 0; k < 4; k++){
      int nx = x + d4x[k], ny = y + d4y[k];

      index = Math.min(index, grid.get(nx, ny, 99999));
    }

    return index;
  }

  public void write(Writes write){
    write.i(stemCell.size());
    stemCell.each((x, y, t) -> write.i(t.pos()));

    write.i(structure.size());
    structure.each((x, y, t) -> {
      write.i(x);
      write.i(y);
      write.i(t);
    });
  }

  public void read(Reads read){
    int size = read.i();
    for(int i = 0; i < size; i++){
      Tile t = Vars.world.tile(read.i());
      stemCell.put(t.x, t.y, t);
    }

    size = read.i();
    for(int i = 0; i < size; i++){
      int x = read.i();
      int y = read.i();
      int t = read.i();
      structure.put(x, y, t);
    }
  }

  @Override
  public void write(DataOutput stream) throws IOException{
    stream.writeInt(territory.size());
    for(IntMap.Entry<NeoplasmNeuron.NeoplasmNeuronBuild> entry : territory.map){
      stream.writeInt(Point2.x(entry.key));
      stream.writeInt(Point2.y(entry.key));
      stream.writeInt(entry.value.pos());
    }
  }

  @Override
  public void read(DataInput stream) throws IOException{
    int size = stream.readInt();

    for(int i = 0; i < size; i++){
      int x = stream.readInt();
      int y = stream.readInt();
      int pos = stream.readInt();
      territory.put(x, y, (NeoplasmNeuron.NeoplasmNeuronBuild)Vars.world.build(pos));
    }
  }
}

