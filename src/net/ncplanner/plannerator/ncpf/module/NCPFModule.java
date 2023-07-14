package net.ncplanner.plannerator.ncpf.module;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFModuleContainer;
public abstract class NCPFModule extends DefinedNCPFObject{
    public final String name;
    public NCPFModule(String name){
        this.name = name;
    }
    public abstract void conglomerate(NCPFModule addon);
    public void setLocalReferences(DefinedNCPFModularObject parentObject){}
}