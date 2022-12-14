package net.ncplanner.plannerator.planner.dssl.object;
public class StackFloat extends StackNumber{
    public StackFloat(double value){
        super(value);
    }
    @Override
    public Type getType(){
        return Type.FLOAT;
    }
    @Override
    public Double getValue(){
        return value.doubleValue();
    }
    @Override
    public StackObject duplicate(){
        return new StackFloat(value.doubleValue());
    }
    @Override
    public StackObject cast(StackObject obj){
        StackObject bas = obj.getBaseObject();
        if(bas instanceof StackString){
            return new StackFloat(Double.parseDouble(((StackString)bas).getValue()));
        }
        return obj.asFloat();
    }
}