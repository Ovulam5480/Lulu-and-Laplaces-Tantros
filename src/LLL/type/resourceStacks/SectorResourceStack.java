//package LLL.type.resourceStacks;
//
//import LLL.world.blocks.packet.*;
//import arc.graphics.g2d.*;
//import arc.struct.*;
//import arc.util.io.*;
//import mindustry.*;
//import mindustry.gen.*;
//import mindustry.io.*;
//import mindustry.type.*;
//import mindustry.world.*;
//
//public class SectorResourceStack extends ResourceStack<Sector>{
//  public static Seq<Sector> sectors = new Seq<>();
//  private Tiles tiles;
//
//  @Override
//  public String name(){
//    return item.name();
//  }
//
//  @Override
//  public boolean discrete(){
//    return true;
//  }
//
//  @Override
//  public int id(){
//    return 10;
//  }
//
//  @Override
//  public void register(){
//    ResourceStackManager.register(Sector.class, SectorResourceStack.class, SectorResourceStack::new, new SectorResourceStack(), id());
//  }
//
//  @Override
//  public void init(){
//    Vars.content.planets().each(p -> sectors.addAll(p.sectors));
//    ResourceStackManager.init(Sector.class, sectors);
//  }
//
//  @Override
//  public void applyBlock(Block block){
//  }
//
//  @Override
//  public TextureRegion getItemIcon(Sector sector){
//    return sector.icon();
//  }
//
//  @Override
//  public Sector findAvailableResource(Entityc entityc, float amount){
//    return null;
//  }
//
//  @Override
//  public void applyPack(Entityc entityc, Object object, float amount){
//  }
//
//  @Override
//  public boolean unpack(Entityc entityc, float amount){
//    return false;
//  }
//
//  @Override
//  public boolean canUnpack(Entityc entityc){
//    return false;
//  }
//
//  @Override
//  public void dumpOutputs(UnpackerBlock.UnpackerBuild<?> building){
//  }
//
//  @Override
//  public void write(Writes writes){
//    TypeIO.writeContent(writes, item.planet);
//    writes.i(item.id);
//    writes.f(amount);
//  }
//
//  @Override
//  public ResourceStack<Sector> read(Reads reads){
//    Sector sector = ((Planet)TypeIO.readContent(reads)).sectors.get(reads.i());
//    return ResourceStackManager.getResourceInstance(sector, reads.f());
//  }
//}
