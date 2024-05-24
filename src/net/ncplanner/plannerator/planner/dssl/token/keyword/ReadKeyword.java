package net.ncplanner.plannerator.planner.dssl.token.keyword;
public class ReadKeyword extends Keyword{
    public ReadKeyword(){
        super("read");
    }
    @Override
    public Keyword newInstance(){
        return new ReadKeyword();
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}