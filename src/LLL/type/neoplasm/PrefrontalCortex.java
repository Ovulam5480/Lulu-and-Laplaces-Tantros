package LLL.type.neoplasm;

import LLL.*;
import LLL.content.blocks.*;
import LLL.entities.neoplasmBehavior.*;
import LLL.lib.singularity.graphic.*;
import LLL.type.*;
import LLL.util.struct.*;
import LLL.world.blocks.module.*;
import LLL.world.neoplasm.*;
import LLL.world.neoplasm.effect.*;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.storage.*;

import static LLL.entities.neoplasmBehavior.neoplasmGrow.GrowLeafTask.*;
import static mindustry.Vars.*;
import static mindustry.world.Build.*;

public class PrefrontalCortex implements Visible{
  private static final Seq<Tile> tmpArray = new Seq<>();
  public static NeoplasmEnvironmentState environmentState;
  public final ObjectMap<Block, Seq<Tile>> ownedBlocks = new ObjectMap<>();
  public final GridIntMap<Integer> structure = new GridIntMap<>();
  private final NeoplasmNeuron.NeoplasmNeuronBuild neuron;
  private final NeoplasmNeuron neuronBlock;
  private final NeoplasmPermeate permeate;
  private final NeoplasmBehavior behavior;
  private final VesselClusters clusters;
  private final NeoplasmReadAfterAllEntity readAfterAllEntity;
  private final Seq<NeoplasmBuildModule> suicides = new Seq<>();
  private final ObjectIntMap<NeoplasmBuildModule> toUpdateCP = new ObjectIntMap<>();
  private final Seq<NeoplasmBuildModule> toUpdateBranch = new Seq<>();
  private final Seq<NeoplasmBuildModule> sourceOrgans = new Seq<>();
  private final Seq<NeoplasmBuildModule> thrombusOrgans = new Seq<>();
  private final Interval interval = new Interval(8);
  public float stressedInterval;
  public boolean underStressed;
  public boolean placeIgnoreCore;
  public int cpUndateTime = 10;
  public Seq<Block> betterVesselTypes = Seq.with(Neoplasm.neoplasmAortus, Neoplasm.neoplasmArterius, Neoplasm.neoplasmVessel);
  private NeoplasmDendron dendron;
  private float permeateTimer = 0;

  public static NeoplasmRules rules = LuluMod.neoplasmRules;

  public PrefrontalCortex(NeoplasmNeuron.NeoplasmNeuronBuild neuron){
    this.neuron = neuron;
    neuronBlock = (NeoplasmNeuron)neuron.block;

    permeate = new NeoplasmPermeate(neuron, structure);
    behavior = new NeoplasmBehavior(neuron, rules.behaviorProv.get());
    behavior.update();

    clusters = new VesselClusters(neuron);

    readAfterAllEntity = new NeoplasmReadAfterAllEntity(this);
    initVisible();
  }

  public static void load(){
    environmentState = new NeoplasmEnvironmentState();
  }

  public void update(){
    float delta = neuron.delta();

    if(shouldNetworkUpdate()){
      if(interval.get(0, 3)){
        for(NeoplasmBuildModule organ : sourceOrgans){
          if(!organ.isAdded()){
            sourceOrgans.remove(organ);
            continue;
          }
          clusters.updateVesselLiquid(organ);
        }
      }

      if(interval.get(1, 15)){
        for(NeoplasmBuildModule organ : thrombusOrgans){
          if(!organ.isAdded()){
            thrombusOrgans.remove(organ);
            continue;
          }
          clusters.updateVesselLiquid(organ);
        }
      }
    }

    if(!suicides.isEmpty()){
      for(NeoplasmBuildModule suicided : suicides){
        regrowVessel(suicided);
      }
      suicides.clear();
    }

    if(shouldNetworkUpdate()){
      permeateTimer += delta;
      while(permeateTimer > 0){
        updatePermeate((s, t) -> {
          NeoplasmPermeate.ResultState result = NeoplasmPermeate.ResultState.valid;

          switch(s){
            case grow -> {
              int rot = getMinDirectionIndex(t.x, t.y);

              if(validPlace(neuronBlock.vessel, neuron.team, t.x, t.y, rot)){
                Block type = neuronBlock.vessel;
                Call.constructFinish(t, type, null, (byte)rot, neuron.team, neuron);

                if(t.build instanceof NeoplasmVessel.NeoplasmVesselBuild v && v.neuron() == neuron){
                  toUpdateBranch.add(v);
                }
              }else if(t.build instanceof NeoplasmBuildModule m){
                if(m.neuron() == null){
                  if(m.getTile() == t){
                    m.configureNeuron(null, neuron);
                    permeateTimer += rules.ownerlessReturn;
                  }else if(Mathf.dst(t.x, t.y, m.getTile().x, m.getTile().y) < 1.1f){
                    requestStructure(m.getTile().x, m.getTile().y, true);
                    m.configureNeuron(null, neuron);
                    permeateTimer += rules.ownerlessReturn;
                  }else{
                    m.suicide();
                    result = NeoplasmPermeate.ResultState.obstruct;
                  }
                }else if(m.neuron() != neuron){
                  result = NeoplasmPermeate.ResultState.invalid;
                }
              }else if(t.build != null){
                result = NeoplasmPermeate.ResultState.obstruct;
              }else{
                result = NeoplasmPermeate.ResultState.invalid;
              }
            }
            case suicide -> {
              if(t.build == null){
                result = NeoplasmPermeate.ResultState.invalid;
              }else if(t == t.build.tile){
                ((NeoplasmBuildModule)t.build).suicide();
              }
            }
            case fail -> {
            }
          }

          return result;
        });
        permeateTimer -= 1 / (underStressed ? rules.stressPermeateAmount : rules.permeateAmount);
      }
    }

    if(stressedInterval >= rules.traumaticStress && !underStressed){
      underStressed = true;

      neuron.stressTrigger();
    }else if(underStressed){
      stressedInterval -= delta * rules.stressReduction;

      if(stressedInterval <= 0){
        underStressed = false;
      }
    }

    if(shouldNetworkUpdate()){
      behavior.update();
    }

    if(interval.get(2, 1)){
      //dendron.update();
    }

    updateCP();
    toUpdateBranch.each(clusters::updateVesselLiquidBranchParent);
    toUpdateBranch.clear();
  }

  public void updateCP(){
    Seq<NeoplasmBuildModule> toRemove = new Seq<>();
    for(ObjectIntMap.Entry<NeoplasmBuildModule> entry : toUpdateCP){
      NeoplasmBuildModule build = entry.key;

      if(build.getBuilding().dead){
        toRemove.add(build);
        continue;
      }

      build.children().clear();
      //build.setParent(null);

      if(build.parent() != null){
        build.parent().children().remove(build);
        build.setParent(null);
      }

      Tile tile = build.getTile();
      int tileX = tile.x, tileY = tile.y;

      if(build.getBlock().size == 1 && build.getBlock().rotate){
        for(int i = 0; i < Geometry.d4.length; i++){
          Point2 edge = Geometry.d4[i];

          if(!structure.containsKey(tileX + edge.x, tileY + edge.y)) continue;
          NeoplasmBuildModule other = (NeoplasmBuildModule)Vars.world.build(tileX + edge.x, tileY + edge.y);
          if(other == null) continue;

          addCP(build, other, i == build.getBuilding().rotation);
        }
      }else{
        NeoplasmBuildModule bestParent = null;
        int minIndex = 999999;
        int length = build.getBlock().getEdges().length;

        for(int i = 0; i < length; i++){
          Point2 edge = build.getBlock().getEdges()[i];
          Point2 inside = build.getBlock().getInsideEdges()[i];

          if(!structure.containsKey(tileX + edge.x, tileY + edge.y)
            || !structure.containsKey(tileX + inside.x, tileY + inside.y)) continue;
          NeoplasmBuildModule other = (NeoplasmBuildModule)Vars.world.build(tileX + edge.x, tileY + edge.y);
          if(other == null) continue;

          if(other == build.parent() || build.children().contains(other)) continue;

          int e = structure.get(tileX + edge.x, tileY + edge.y);
          int is = structure.get(tileX + inside.x, tileY + inside.y);

          if(Math.abs(e - is) != 1) continue;

          if(is > e){
            if(minIndex > e){
              bestParent = other;
              minIndex = e;
            }
          }else{
            addCP(build, other, false);
          }
        }

        if(bestParent != null){
          addCP(build, bestParent, true);
        }
      }

      if(toUpdateCP.increment(build, 0, -1) <= 0){
        toRemove.add(build);
      }
    }

    toRemove.each(build -> toUpdateCP.remove(build, 0));
  }

  public void addCP(NeoplasmBuildModule build, NeoplasmBuildModule other, boolean isParent){
    if(isParent){
      if(build.parent() == null){
        build.setParent(other);
        other.children().add(build);
      }
      //other.children().add(build);
    }else{
      if(other.parent() == null){
        other.setParent(build);
      }

      if(other.parent() == build){
        build.children().add(other);
      }
    }
  }

  public void handleStress(){
    stressedInterval++;
  }

  public void updatePermeate(Func2<NeoplasmPermeate.PermeateState, Tile, NeoplasmPermeate.ResultState> growValid){
    if(!underStressed){
      permeate.updatePermeate(growValid);
    }else{
      int maxLoop = 15;
      while(permeate.hasStemCell() && maxLoop-- > 0){
        permeate.updatePermeate((s, t) -> {
          if(s != NeoplasmPermeate.PermeateState.fail){
            return growValid.get(s, t);
          }
          return NeoplasmPermeate.ResultState.invalid;
        });
      }
    }
  }

  public void regrowVessel(NeoplasmBuildModule suicided){
    int[] type = {999};
    int[] ownedIndex = {-1};
    suicided.getBuilding().eachEdge(t -> {
      Block block = t.block();
      if(betterVesselTypes.contains(block) && betterVesselTypes.indexOf(block) < type[0]){
        int index = ownedBlocks.get(block).indexOf(t);

        if(index == -1) return;

        type[0] = betterVesselTypes.indexOf(block);
        ownedIndex[0] = index;
      }
    });

    if(ownedIndex[0] == -1) return;

    Block vessel = betterVesselTypes.get(type[0]);
    Seq<Tile> tiles = ownedBlocks.get(vessel);

    suicided.getTile().getLinkedTilesAs(suicided.getBlock(), t -> {
      if(structure.get(t.x, t.y) != null){
        byte rot = (byte)getMinDirectionIndex(t.x, t.y);

        if(validPlace(vessel, neuron.team, t.x, t.y, rot)){
          Call.constructFinish(t, vessel, null, rot, neuron.team, neuron);

          if(tiles.peek() == t){
            tiles.pop();
            tiles.insert(ownedIndex[0], t);
          }
        }
      }
    });
  }

  public boolean validPlace(Block type, Team team, int x, int y, int rotation){
    return Build.checkNoUnitOverlap(type, x, y) && validPlaceIgnoreUnits(type, team, x, y, rotation, !placeIgnoreCore);
  }

  public boolean checkValid(NeoplasmBuildModule build){
    return structure.get(build.tileX(), build.tileY()) != null;
  }


  public void addStructure(NeoplasmVessel.NeoplasmVesselBuild build){
    behavior.handleStructureChange(build, build.tileX(), build.tileY(), true);
  }

  public void removeStructure(NeoplasmVessel.NeoplasmVesselBuild build){
    behavior.handleStructureChange(build, build.tileX(), build.tileY(), false);
  }

  public void handleSuicide(NeoplasmBuildModule build){
    suicides.add(build);
  }

  public void handleKill(NeoplasmBuildModule build){
    permeate.getRemoved(build);
  }

  public void requestStructure(int x, int y, boolean stemCell){
    permeate.requestStructure(x, y, stemCell);
  }

  public void handleOwnedBlock(NeoplasmBuildModule build, int x, int y, int rotation, boolean add){
    Block block = build.getBlock();

    if(add){
      if(!ownedBlocks.containsKey(block)){
        ownedBlocks.put(block, new Seq<>());
      }

      ownedBlocks.get(block).add(build.getTile());
    }else if(ownedBlocks.containsKey(block)){//排除退出某一局游戏时的情况
      ownedBlocks.get(block).remove(build.getTile());
    }
  }

  public void handleThrombusChange(NeoplasmBuildModule build, boolean add){
    if(add){
      thrombusOrgans.add(build);
    }else{
      thrombusOrgans.remove(build);
    }
  }

  public void handleSource(NeoplasmBuildModule build){
    sourceOrgans.add(build);
  }

  public void rotationChange(NeoplasmBuildModule build){
    build.getBuilding().rotation = getMinDirectionIndex(build.tileX(), build.tileY());
  }

  public void addParentChildren(NeoplasmBuildModule build){
    if(structure.containsKey(build.tileX(), build.tileY())){//todo fix -> growAttributes
      toUpdateCP.put(build, cpUndateTime);
    }else{
      //Log.info("addParentChildren " + build.tileX() + " " + build.tileY());
    }
  }

  public int getMinDirectionIndex(int x, int y){
    int rotation = -1;
    int index = 99999;

    for(int k = 0; k < 4; k++){
      Point2 point2 = Geometry.d4[k];

      int nx = x + point2.x, ny = y + point2.y;

      int get = structure.get(nx, ny, 999999);
      if(get < index){
        rotation = k;
        index = get;
      }
    }

    return rotation;
  }

  public void getItemDumpTarget(NeoplasmBuildModule build){
    Building building = build.getBuilding();
    Tile tile = build.getTile();

    Tile target = null;
    int minI = 999999;

    for(Point2 edge : build.getBlock().getEdges()){
      int i = structure.get(tile.x + edge.x, tile.y + edge.y, 999999);
      if(i < minI){
        minI = i;
        target = Vars.world.tile(tile.x + edge.x, tile.y + edge.y);
      }
    }

    if(target != null && target.build != null && target.build.team == building.team){
      build.setDumpTarget(target.build);
    }

    building.onProximityUpdate();
  }

  public void remove(){
    permeate.clearTerritory();
  }

  public void write(Writes write){
    behavior.writeTask(write);
    permeate.write(write);

    write.bool(underStressed);
    write.bool(placeIgnoreCore);

  }

  public void read(Reads read){
    behavior.readTask(read);
    permeate.read(read);

    underStressed = read.bool();
    placeIgnoreCore = read.bool();
  }

  public void addToWrite(Seq<Entityc> toWrite){
    behavior.addToWrite(toWrite);
  }

  public void getFromRead(Queue<Entityc> toRead){
    behavior.getFromRead(toRead);
  }

  private final Rect camera = new Rect();

  @Override
  public void draw(){
    Core.camera.bounds(camera);

    ownedBlocks.each((b, ts) -> {
      for(Tile tile : ts){
        Building building = tile.build;

        if(building == null) continue;

        if(!Tmp.r1.set(camera).grow(building.block.clipSize).contains(building.x, building.y)){
          continue;
        }

        NeoplasmBuildModule build = (NeoplasmBuildModule)building;

        for(NeoplasmBuildModule child : build.children()){
          if(!Tmp.r1.set(camera).grow(child.getBlock().clipSize).contains(child.x(), child.y())){
            continue;
          }
          SglDraw.gradientLine(child.x(), child.y(), build.x(), build.y(), Color.acid, Color.red, 0);
        }
      }
    });
  }

  @Override
  public void initVisible(){
    neuron.visibles.add(this);
  }

  @Override
  public String description(){
    return "显示瘤液的父子关系树";
  }

  public static boolean validPlaceIgnoreUnits(Block type, Team team, int x, int y, int rotation, boolean checkCoreRadius){
    //the wave team can build whatever they want as long as it's visible - banned blocks are not applicable
    if(type == null){
      return false;
    }

    if(!state.rules.editor && checkCoreRadius){
      //find closest core, if it doesn't match the team, placing is not legal
      if(state.rules.polygonCoreProtection){
        float mindst = Float.MAX_VALUE;
        CoreBlock.CoreBuild closest = null;
        for(Teams.TeamData data : state.teams.active){
          if(!data.team.rules().protectCores){
            continue;
          }

          for(CoreBlock.CoreBuild tile : data.cores){
            float dst = tile.dst2(x * tilesize + type.offset, y * tilesize + type.offset);
            if(dst < mindst){
              closest = tile;
              mindst = dst;
            }
          }
        }
        if(closest != null && closest.team != team){
          return false;
        }
      }else if(state.teams.anyEnemyCoresWithinBuildRadius(team, x * tilesize + type.offset, y * tilesize + type.offset)){
        return false;
      }
    }

    Tile tile = world.tile(x, y);

    if(tile == null) return false;

    if(!type.canPlaceOn(tile, team, rotation)){
      return false;
    }

    //floors have different checks
    if(type.isFloor()){
      return type.isOverlay() ? tile.overlay() != type : tile.floor() != type;
    }

    //campaign darkness check
    if(!type.ignoreBuildDarkness && world.getDarkness(x, y) >= 3){
      return false;
    }

    if(!type.requiresWater && !contactsShallows(tile.x, tile.y, type) && !type.placeableLiquid){
      return false;
    }

    int offsetx = -(type.size - 1) / 2;
    int offsety = -(type.size - 1) / 2;

    for(int dx = 0; dx < type.size; dx++){
      for(int dy = 0; dy < type.size; dy++){
        int wx = dx + offsetx + tile.x, wy = dy + offsety + tile.y;

        Tile check = world.tile(wx, wy);

        if(
          check == null || //nothing there
            (type.size == 2 && world.getDarkness(wx, wy) >= 3) ||
            (state.rules.staticFog && state.rules.fog && !fogControl.isDiscovered(team, wx, wy)) ||
            (check.floor().isDeep() && !type.floating && !type.requiresWater && !type.placeableLiquid) || //deep water
            (!state.rules.derelictRepair && check.team() == Team.derelict && check.build != null) ||
            (type == check.block() && check.build != null && rotation == check.build.rotation && type.rotate && !((type == check.block() && team != Team.derelict && check.team() == Team.derelict))) || //same block, same rotation
            !check.interactable(team) || //cannot interact
            !check.floor().placeableOn && !type.ignoreBuildDarkness || //solid floor
            //when you have a payload, you cannot place blocks on things, even if normal placement rules allow it. this is a hack that assumes checkVisible = true means it's coming from a payload
            !(((type.canReplace(check.block()) || (check.build != null && check.build.canBeReplaced(type)) || (type == check.block() && team != Team.derelict && check.team() == Team.derelict)) || //can replace type OR can replace derelict block of same type
              (check.build instanceof ConstructBlock.ConstructBuild build && build.current == type && check.centerX() == tile.x && check.centerY() == tile.y)) && //same type in construction
              type.bounds(tile.x, tile.y, Tmp.r1).grow(0.01f).contains(check.block().bounds(check.centerX(), check.centerY(), Tmp.r2))) || //no replacement
            (type.requiresWater && check.floor().liquidDrop != Liquids.water) //requires water but none found
        ) return false;
      }
    }

    if(state.rules.placeRangeCheck && checkCoreRadius && !state.isEditor() && getEnemyOverlap(type, team, x, y) != null){
      return false;
    }

    return true;
  }
}
