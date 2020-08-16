package multiblock.ppe;
import generator.Settings;
import multiblock.Block;
import multiblock.Multiblock;
public class ClearInvalid extends PostProcessingEffect{
    public ClearInvalid(){
        super("Remove Invalid Blocks", true, true, true);
    }
    @Override
    public void apply(Multiblock multiblock, Settings settings){
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    Block b = multiblock.getBlock(x, y, z);
                    if(b==null)continue;
                    if(!b.isValid())multiblock.setBlock(x, y, z, null);
                }
            }
        }
    }
    @Override
    public boolean defaultEnabled(){
        return true;
    }
}