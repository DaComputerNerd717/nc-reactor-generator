package multiblock;
import simplelibrary.Queue;
import simplelibrary.opengl.Renderer2D;
public class MultiblockBit extends Renderer2D{
    protected static final Queue<Direction> directions = directions();
    private static Queue<Direction> directions(){
        Queue<Direction> directions = new Queue<>();
        for(Direction d : Direction.values())directions.enqueue(d);
        return directions;
    }
    protected static final Queue<Axis> axes = axes();
    private static Queue<Axis> axes(){
        Queue<Axis> axes = new Queue<>();
        for(Axis a : Axis.values())axes.enqueue(a);
        return axes;
    }
    protected String percent(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = (Math.round(n*fac*100)/(double)Math.round(fac));
        return (digits==0?Math.round(d):d)+"%";
    }
    protected String round(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = Math.round(n*fac)/(double)Math.round(fac);
        return (digits==0?Math.round(d):d)+"";
    }
}