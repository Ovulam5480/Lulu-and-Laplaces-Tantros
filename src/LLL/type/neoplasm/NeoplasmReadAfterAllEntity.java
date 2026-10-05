package LLL.type.neoplasm;

import arc.struct.*;
import mindustry.io.*;
import mindustry.world.*;

import java.io.*;

public class NeoplasmReadAfterAllEntity implements SaveFileReader.CustomChunk{
  public PrefrontalCortex cortex;

  public NeoplasmReadAfterAllEntity(PrefrontalCortex cortex){
    this.cortex = cortex;
    SaveVersion.addCustomChunk("neoplasmPermeate", this);
  }

  @Override
  public void write(DataOutput stream) throws IOException{
  }

  @Override
  public void read(DataInput stream) throws IOException{
    for(Seq<Tile> value : cortex.ownedBlocks.values()){
      for(Tile tile : value){
        if(tile.build != null){
          tile.build.onProximityUpdate();
        }
      }
    }
  }
}
