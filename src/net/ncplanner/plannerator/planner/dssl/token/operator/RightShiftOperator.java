package net.ncplanner.plannerator.planner.dssl.token.operator;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
public class RightShiftOperator extends Operator{
    public RightShiftOperator(){
        super(">>");
    }
    @Override
    public Operator newInstance(){
        return new RightShiftOperator();
    }
    @Override
    public StackObject evaluate(Script script, StackObject v1, StackObject v2){
        return new StackInt(v1.asInt().getValue()>>v2.asInt().getValue());
    }
}