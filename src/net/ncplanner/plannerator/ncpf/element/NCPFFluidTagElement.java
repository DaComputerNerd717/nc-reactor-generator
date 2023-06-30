package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFFluidTagElement extends NCPFElementDefinition{
    public String name;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        NCPFFluidTagElement other = (NCPFFluidTagElement) definition;
        return name.equals(other.name);
    }
}