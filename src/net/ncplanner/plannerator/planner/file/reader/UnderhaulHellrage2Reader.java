package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
public class UnderhaulHellrage2Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        return major==1&&minor==2&&build>=23;//&&build<=25;
    }
    @Override
    public synchronized NCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        UnderhaulSFRDesign sfr = new UnderhaulSFRDesign(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        sfr.fuel = recovery.recoverUnderhaulSFRFuel(fuelName, usedFuel.getFloat("BaseHeat"), usedFuel.getFloat("BasePower"));
        JSON.JSONObject compressedReactor = hellrage.getJSONObject("CompressedReactor");
        for(String name : compressedReactor.keySet()){
            Block block = recovery.recoverUnderhaulSFRBlock(name);
            JSON.JSONArray blocks = compressedReactor.getJSONArray(name);
            for(Object blok : blocks){
                JSON.JSONObject blokLoc = (JSON.JSONObject) blok;
                int x = blokLoc.getInt("X");
                int y = blokLoc.getInt("Y");
                int z = blokLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        Project file = new Project();
        file.designs.add(sfr);
        return file;
    }
}