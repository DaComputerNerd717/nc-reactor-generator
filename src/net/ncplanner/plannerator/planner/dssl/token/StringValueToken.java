package net.ncplanner.plannerator.planner.dssl.token;
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
        String txt = text;
        txt = txt.replace("\\t", "\t");
        txt = txt.replace("\\b", "\b");
        txt = txt.replace("\\n", "\n");
        txt = txt.replace("\\r", "\r");
        txt = txt.replace("\\f", "\f");
        txt = txt.replace("\\'", "\'");
        txt = txt.replace("\\\"", "\"");
        txt = txt.replace("\\\\", "\\");
        value = txt.substring(1, txt.length()-1);
    }
}