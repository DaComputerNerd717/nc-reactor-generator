package planner.multiblock;
import java.util.ArrayList;
import java.util.List;
public abstract class Multiblock<T extends Block>{
    public Block[][][] blocks;
    public Multiblock(int x, int y, int z){
        blocks = new Block[x][y][z];
    }
    public T getBlock(int x, int y, int z){
        return (T) blocks[x][y][z];
    }
    public abstract String getDefinitionName();
    public abstract Multiblock<T> newInstance();
    public abstract void getAvailableBlocks(List<T> blocks);
    public final List<T> getAvailableBlocks(){
        ArrayList<T> list = new ArrayList<>();
        getAvailableBlocks(list);
        return list;
    }
    public int getX(){
        return blocks.length;
    }
    public int getY(){
        return blocks[0].length;
    }
    public int getZ(){
        return blocks[0][0].length;
    }
}