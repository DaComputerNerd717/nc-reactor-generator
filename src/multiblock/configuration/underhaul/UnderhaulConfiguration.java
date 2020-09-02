package multiblock.configuration.underhaul;
import java.util.ArrayList;
import multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import simplelibrary.config2.Config;
public class UnderhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null){
            config.set("fissionSFR", fissionSFR.save(parent, partial));
        }
        return config;
    }
    public void applyPartial(UnderhaulConfiguration partial, ArrayList<Multiblock> multiblocks){
        if(fissionSFR!=null){
            partial.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.applyPartial(partial.fissionSFR, multiblocks);
        }
    }
}