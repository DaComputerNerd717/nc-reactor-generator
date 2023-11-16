package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class PlusOperator extends Operator{
    public PlusOperator(){
        super("+");
    }
    @Override
    public Operator newInstance(){
        return new PlusOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        if(v1.getBaseType()==StackObject.Type.INT&&v2.getBaseType()==StackObject.Type.INT){
            return new StackInt(v1.asInt().getValue()+v2.asInt().getValue());
        }else{
            return new StackFloat(v1.asNumber().getValue().doubleValue()+v2.asNumber().getValue().doubleValue());
        }
    }
    @Override
    public String getOverload(){
        return "add";
    }
}