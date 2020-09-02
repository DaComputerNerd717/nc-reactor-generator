package multiblock.symmetry;
import java.util.ArrayList;
import java.util.Locale;
import multiblock.Block;
import multiblock.Multiblock;
public abstract class AxialSymmetry extends Symmetry{
    public static AxialSymmetry X = new AxialSymmetry("X"){
        @Override
        public void apply(Multiblock multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                int X = multiblock.getX()-b.x-1;
                multiblock.setBlock(X, b.y, b.z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public static AxialSymmetry Y = new AxialSymmetry("Y"){
        @Override
        public void apply(Multiblock multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                int Y = multiblock.getY()-b.y-1;
                multiblock.setBlock(b.x, Y, b.z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public static AxialSymmetry Z = new AxialSymmetry("Z"){
        @Override
        public void apply(Multiblock multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                int Z = multiblock.getZ()-b.z-1;
                multiblock.setBlock(b.x, b.y, Z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public AxialSymmetry(String axis){
        super(axis+" Symmetry");
    }
    public boolean matches(String sym){
        switch(sym.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("-", "").replace("symmetry", "").replace("symmetrical", "")){
            case "x":
                return this==X;
            case "y":
                return this==Y;
            case "z":
                return this==Z;
            case "xy":
                return this==X||this==Y;
            case "yz":
                return this==Y||this==Z;
            case "xz":
                return this==X||this==Z;
            case "xyz":
                return this==X||this==Y||this==Z;
            case "":
                return true;
        }
        return false;
    }
}