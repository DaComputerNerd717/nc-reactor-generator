package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class StringValueToken extends Token{
    public String value;
    public StringValueToken(){
        super(quote+s_char_sequence+quote);
    }
    @Override
    public Token newInstance(){
        return new StringValueToken();
    }
    @Override
    public void load(){
        value = text.substring(1, text.length()-1);
    }
    @Override
    public void run(Script script){
        script.push(new StackString(value));
    }
}