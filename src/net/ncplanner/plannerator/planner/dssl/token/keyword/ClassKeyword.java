package net.ncplanner.plannerator.planner.dssl.token.keyword;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackClass;
import net.ncplanner.plannerator.planner.dssl.object.StackLabel;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class ClassKeyword extends Keyword{
    public ClassKeyword(){
        super("class");
    }
    @Override
    public Keyword newInstance(){
        return new ClassKeyword();
    }
    @Override
    public void run(Script script){
        StackObject value = script.pop();
        StackLabel key = script.pop().asLabel();
        script.variables.put(key.getValue(), new StackVariable(key.getValue(), new StackClass(key.getValue(), value.getBaseObject().asMethod())));
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}