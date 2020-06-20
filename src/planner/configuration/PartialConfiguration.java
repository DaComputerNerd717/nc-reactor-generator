package planner.configuration;
public class PartialConfiguration extends Configuration{
    public PartialConfiguration(String name, String version){
        super(name, version);
    }
    @Override
    protected boolean isPartial(){
        return true;
    }
}