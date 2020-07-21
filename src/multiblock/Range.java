package multiblock;
public class Range<T>{
    public final T obj;
    public final int min;
    public final int max;
    public Range(T obj, int min, int max){
        this.obj = obj;
        this.min = min;
        this.max = max;
    }
}