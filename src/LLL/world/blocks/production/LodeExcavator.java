package LLL.world.blocks.production;

import LLL.*;
import LLL.content.*;
import LLL.ctype.packet.packetTypes.*;
import LLL.entities.gen.*;
import LLL.type.resourceStacks.*;
import LLL.world.blocks.packet.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;


public class LodeExcavator extends UnpackerBlock{
  protected final ObjectIntMap<TextureRegion> oreCount = new ObjectIntMap<>();
  protected final ObjectFloatMap<TextureRegion> oreSpeed = new ObjectFloatMap<>();
  private final Rect tmp = new Rect();

  public float strength = 0;
  //单个封包进行挖掘的基础挖掘时间
  public float drillTime = 400f;
  public float liquidBoostIntensity = 3.24f;
  public float warmupSpeed = 0.015f;
  public Effect updateEffect = Fx.pulverizeSmall;
  public float updateEffectChance = 0.02f;

  public TextureRegion outRegion;
  public TextureRegion rotateRegion;
  public TextureRegion glowRegion;//说实话, 水下发热有点怪

  public Color heatColor = Color.valueOf("ff5512");

  public LodeExcavator(String name){
    super(name);
    update = true;
    solid = true;
    group = BlockGroup.drills;
    sync = true;
    rotate = true;

    hasLiquids = true;
    liquidCapacity = 50f;
    hasItems = true;
    //ambientSound = Sounds.drill;
    ambientSoundVolume = 0.018f;
    flags = EnumSet.of(BlockFlag.drill);

    category = Category.production;
    buildVisibility = BuildVisibility.shown;
  }

  @Override
  public void load(){
    super.load();
    outRegion = Core.atlas.find(name + "-out");
    rotateRegion = Core.atlas.find(name + "-rotate");
    glowRegion = Core.atlas.find(name + "-glow");
  }

  @Override
  public void setBars(){
    super.setBars();
  }

  @Override
  public boolean canPlaceOn(Tile tile, Team team, int rotation){
    if(tile != null){
      tmp.setCentered(tile.worldx() + offset, tile.worldy() + offset, size * 8);

      return LuluMod.oIndexer.lodePacketTree.any(tmp.x, tmp.y, tmp.width, tmp.height);
    }
    return false;
  }

  @Override
  public void drawPlanRegionDir(BuildPlan plan, Eachable<BuildPlan> list){
    Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    Draw.rect(rotateRegion, plan.drawx(), plan.drawy());
    super.drawPlanRegionDir(plan, list);
  }

  @Override
  protected TextureRegion[] icons(){
    return new TextureRegion[]{outRegion, rotateRegion, region};
  }

  protected void countOre(Tile tile){
    oreSpeed.clear();
    oreCount.clear();

    tmp.setCentered(tile.worldx() + offset, tile.worldy() + offset, size * 8);

    LuluMod.oIndexer.lodePacketTree.intersect(tmp, lp -> {
      float sum = lp.amount();

      for(ResourceStack<?> stack : lp.resources()){
        TextureRegion icon = stack.getIcon();
        int amount = Mathf.floor(stack.amount);

        oreCount.increment(icon, amount);
        oreSpeed.increment(icon, 0, 60f / getDrillTime(lp) * amount / sum);
      }
    });
  }

  public float getDrillTime(LodePacketc lodePacketc){
    float tenacity = ((LodePacketType)lodePacketc.packetType()).tenacity;

    return drillTime * (1 + Math.max(0, tenacity - strength));
  }

  @Override
  public void drawPlace(int x, int y, int rotation, boolean valid){
    super.drawPlace(x, y, rotation, valid);
    Tile tile = Vars.world.tile(x, y);
    if(tile == null) return;
    countOre(tile);

    if(oreSpeed.size > 0){
      int i = 0;
      for(ObjectFloatMap.Entry<TextureRegion> entry : oreSpeed){
        TextureRegion icon = entry.key;
        float speed = entry.value;

        float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", speed, 2) + "(共" + oreCount.get(icon) + ")", x, y + i++, valid);
        float dx = x * tilesize + offset - width / 2f - 4f,
          dy = y * tilesize + offset + size * tilesize / 2f - 3 + i * tilesize,
          s = iconSmall / 4f;
        Draw.mixcol(Color.darkGray, 1f);
        Draw.rect(icon, dx, dy - 1, s, s);
        Draw.reset();
        Draw.rect(icon, dx, dy, s, s);
      }
    }
  }

  public class LodeExcavatorBuilding extends UnpackerBuild<LodePacketc>{
    public boolean hasFinished = false;
    public float drillTime = 0;

    @Override
    public boolean shouldConsume(){
      return super.shouldConsume() && !hasFinished;
    }

    @Override
    public void draw(){
      Draw.rect(outRegion, x, y, rotdeg());

      if(payload != null) payload.draw();
      Draw.z(Layer.blockOver);

      Drawf.spinSprite(rotateRegion, x, y, drillTime);

      drawDirRegion();
    }

    @Override
    public void updateTile(){
      if(toUnpack == null){
        setStack();
      }

      drillTime += warmup * delta();

      if(efficiency > 0){
        speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency);

        warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
        progress += edelta() * speed * warmup * targetSpeed / 60f;

        if(Mathf.chanceDelta(updateEffectChance * warmup))
          updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
      }else{
        warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
      }

      if(hasFinished) return;

      super.updateTile();
    }

    @Override
    public void setStack(){
      if(toUnpacks.isEmpty()){
        hasFinished = true;
        return;
      }

      if(toUnpack == null){
        toUnpack = toUnpacks.random();
      }

      stack = toUnpack.resources().random();
    }

    @Override
    public void unpack(){
      toUnpack.unpack(this, excavateAmount);

      if(!toUnpack.isAdded()){
        removePacket();
      }

      toUnpack = null;
    }

    @Override
    public void removePacket(){
      super.removePacket();

      calculateSpeed();
      //todo Events.fire, 贫瘠矿?
//            if(lodePackets.isEmpty()){
//
//            }
    }

    public void calculateSpeed(){
      targetSpeed = 0;
      toUnpacks.each(lp -> targetSpeed += 60f / getDrillTime(lp));
    }

    //todo 应用过滤器
    @Override
    public void add(){
      super.add();

      hitbox(Tmp.r1);
      LuluMod.oIndexer.lodePacketTree.intersect(Tmp.r1, lp -> toUnpacks.addUnique(lp));

      calculateSpeed();
    }

    @Override
    public Building create(Block block, Team team){
      super.create(block, team);

      if((Vars.state.rules.env & Env.underwater) == 0){
        liquids.add(OvulamLiquids.air, liquidCapacity);
      }
      return this;
    }
  }
}
