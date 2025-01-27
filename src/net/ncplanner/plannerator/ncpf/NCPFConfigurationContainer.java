package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.configuration.UnknownNCPFConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
public class NCPFConfigurationContainer extends DefinedNCPFObject{
    public static ArrayList<String> configOrder = new ArrayList<>();
    public static HashMap<String, Supplier<NCPFConfiguration>> recognizedConfigurations = new HashMap<>();
    public HashMap<String, NCPFConfiguration> configurations = new HashMap<>();
    public static boolean isRecognized(Supplier<NCPFConfiguration> config){
        return recognizedConfigurations.containsKey(config.get().name);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String key : ncpf.keySet()){
            configurations.put(key, ncpf.getDefinedNCPFObject(key, recognizedConfigurations.getOrDefault(key, UnknownNCPFConfiguration::new)));
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String key : configurations.keySet()){
            ncpf.setDefinedNCPFObject(key, configurations.get(key));
        }
    }
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        NCPFConfiguration c = configurations.get(config.get().name);
        if(c instanceof UnknownNCPFConfiguration)return null;
        return (T)c;
    }
    public boolean hasConfiguration(Supplier<NCPFConfiguration> config){
        return configurations.containsKey(config.get().name);
    }
    public void setConfiguration(NCPFConfiguration config){
        if(config==null)return;
        if(!recognizedConfigurations.containsKey(config.name))throw new IllegalArgumentException("Cannot set unrecognized configuration: "+config.name+"!");
        configurations.put(config.name, config);
    }
    public <T extends NCPFConfiguration> void withConfiguration(Supplier<T> config, Consumer<T> func){
        T t = getConfiguration(config);
        if(t!=null){
            func.accept(t);
        }
    }
    /**
     * Add all parts of another configuration to this one
     * @param addon The addon to add
     */
    public void conglomerate(NCPFConfigurationContainer addon){
        for(String key : addon.configurations.keySet()){
            NCPFConfiguration addonConfig = addon.configurations.get(key);
            if(configurations.containsKey(key)){
                configurations.get(key).conglomerate(addonConfig);
            }else configurations.put(key, addonConfig.copyTo(recognizedConfigurations.get(addonConfig.name)));
        }
    }
    public void setReferences(){
        configurations.values().forEach(NCPFConfiguration::setReferences);
    }
    public void makePartial(List<Design> designs){
        for(String key : configurations.keySet()){
            NCPFConfiguration cfg = configurations.get(key);
            cfg.makePartial(designs);
        }
    }
    public String getNameAndVersion(){
        for(String key : NCPFConfigurationContainer.configOrder){
            if(configurations.containsKey(key)){
                NCPFConfiguration cfg = configurations.get(key);
                ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
                if(module!=null&&module.name!=null)return module.name+" "+module.version;
            }
        }
        for(NCPFConfiguration cfg : configurations.values()){
            ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
            if(module!=null&&module.name!=null)return module.name+" "+module.version;
        }
        return "Unknown Configuration";
    }
}