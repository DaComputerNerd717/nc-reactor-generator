package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random.RandomBlockMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random.RandomFuelMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.MathUtil;
public class LiteUnderhaulSFR implements LiteMultiblock<UnderhaulSFR>{
    public final CompiledUnderhaulSFRConfiguration configuration;
    public int[][][] cellEfficiency;
    public int[][][] blockEfficiency;
    public int[][][] blockActive;
    public final int[] dims;
    public final int[][][] blocks;
    
    public int fuel;
    
    public int netHeat;
    private int power, heat, cooling, cells;
    private float powerf, heatf, efficiency, heatMult;
    private static final int[][] directions = new int[6][];
    static{
        directions[0] = new int[]{1,0,0};
        directions[1] = new int[]{0,1,0};
        directions[2] = new int[]{0,0,1};
        directions[3] = new int[]{-1,0,0};
        directions[4] = new int[]{0,-1,0};
        directions[5] = new int[]{0,0,-1};
    }
    private int[] blockCount;
    private int[][] coolerCalculationStepIndicies;
    private Variable[] vars;
    public LiteUnderhaulSFR(CompiledUnderhaulSFRConfiguration configuration){
        this.configuration = configuration;
        blocks = new int[configuration.maxSize][configuration.maxSize][configuration.maxSize];
        for(int x = 0; x<configuration.maxSize; x++){
            for(int y = 0; y<configuration.maxSize; y++){
                for(int z = 0; z<configuration.maxSize; z++){
                    blocks[x][y][z] = -1;
                }
            }
        }
        dims = new int[]{configuration.minSize,configuration.minSize,configuration.minSize};//default to minimum size
    }
    public void countBlocks(){
        blockCount = new int[configuration.blockName.length];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0)blockCount[blocks[x][y][z]]++;
                }
            }
        }
    }
    public void calculateCells(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        blockEfficiency[x][y][z] = cellEfficiency[x][y][z] = countAdjacents(x, y, z, configuration.blockFuelCell, configuration.blockModerator, configuration.neutronReach)+1;
                        powerf+=cellEfficiency[x][y][z]*configuration.fuelPower[fuel];
                        heatf+=(cellEfficiency[x][y][z]*(cellEfficiency[x][y][z]+1))/2f*configuration.fuelHeat[fuel];
                        cells+=blockEfficiency[x][y][z]>0?1:0;
                    }
                }
            }
        }
    }
    public void calculateModerators(){
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockModerator[blocks[x][y][z]]){
                        if(x>0)blockEfficiency[x][y][z]+=cellEfficiency[x-1][y][z];
                        if(y>0)blockEfficiency[x][y][z]+=cellEfficiency[x][y-1][z];
                        if(z>0)blockEfficiency[x][y][z]+=cellEfficiency[x][y][z-1];
                        if(x<dims[0]-1)blockEfficiency[x][y][z]+=cellEfficiency[x+1][y][z];
                        if(y<dims[1]-1)blockEfficiency[x][y][z]+=cellEfficiency[x][y+1][z];
                        if(z<dims[2]-1)blockEfficiency[x][y][z]+=cellEfficiency[x][y][z+1];
                        powerf+=blockEfficiency[x][y][z]*configuration.fuelPower[fuel]*configuration.moderatorExtraPower/6;
                        heatf+=blockEfficiency[x][y][z]*configuration.fuelHeat[fuel]*configuration.moderatorExtraHeat/6;
                    }
                }
            }
        }
    }
    public void optimizeCoolerSteps(){
        int steps = 0;
        int[][] newCCSIs = new int[configuration.coolerCalculationStepIndicies.length][];
        for(int[] indicies : configuration.coolerCalculationStepIndicies){
            int stps = 0;
            int[] newIndicies = new int[indicies.length];
            for(int index : indicies){
                if(blockCount[index]>0){
                    newIndicies[stps] = index;
                    stps++;
                }
            }
            int[] newNewIndicies = new int[stps];
            for(int i = 0; i<stps; i++){
                newNewIndicies[i] = newIndicies[i];
            }
            newCCSIs[steps] = newNewIndicies;
            steps++;
        }
        coolerCalculationStepIndicies = new int[steps][];
        for(int i = 0; i<steps; i++){
            coolerCalculationStepIndicies[i] = newCCSIs[i];
        }
    }
    public void calculateCoolers(){
        int[] adjacents = new int[]{-2,-2,-2,-2,-2,-2};
        int[] active = new int[6];
        int somethingChanged;
        do{
            somethingChanged = 0;
            for(int[] indicies : coolerCalculationStepIndicies){
                for(int x = 0; x<dims[0]; x++){
                    for(int y = 0; y<dims[1]; y++){
                        for(int z = 0; z<dims[2]; z++){
                            B:for(int c : indicies){
                                if(blocks[x][y][z]==c){
                                    if(x>0){
                                        adjacents[0] = blocks[x-1][y][z];
                                        active[0] = blockEfficiency[x-1][y][z];
                                    }
                                    if(y>0){
                                        adjacents[1] = blocks[x][y-1][z];
                                        active[1] = blockEfficiency[x][y-1][z];
                                    }
                                    if(z>0){
                                        adjacents[2] = blocks[x][y][z-1];
                                        active[2] = blockEfficiency[x][y][z-1];
                                    }
                                    if(x<dims[0]-1){
                                        adjacents[3] = blocks[x+1][y][z];
                                        active[3] = blockEfficiency[x+1][y][z];
                                    }
                                    if(y<dims[1]-1){
                                        adjacents[4] = blocks[x][y+1][z];
                                        active[4] = blockEfficiency[x][y+1][z];
                                    }
                                    if(z<dims[2]-1){
                                        adjacents[5] = blocks[x][y][z+1];
                                        active[5] = blockEfficiency[x][y][z+1];
                                    }
                                    int was = blockEfficiency[x][y][z];
                                    blockEfficiency[x][y][z] = 1;
                                    for(CompiledUnderhaulSFRPlacementRule rule : configuration.blockPlacementRules[c]){
                                        if(!rule.isValid(adjacents, active, configuration)){
                                            blockEfficiency[x][y][z] = 0;
                                            cooling-=configuration.blockCooling[c]*(was-blockEfficiency[x][y][z]);
                                            somethingChanged += was-blockEfficiency[x][y][z];
                                            break B;
                                        }
                                    }
                                    cooling+=configuration.blockCooling[c]*(blockEfficiency[x][y][z]-was);
                                    somethingChanged += blockEfficiency[x][y][z]-was;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }while(somethingChanged>0&&configuration.hasRecursiveRules);
    }
    public void calculate(){
        //reset stats
        efficiency = heatMult = powerf = heatf = power = heat = netHeat = cooling = cells = 0;
        blockEfficiency = new int[dims[0]][dims[1]][dims[2]];//probably faster than clearing it manually
        cellEfficiency = new int[dims[0]][dims[1]][dims[2]];
        countBlocks();
        calculateCells();
        calculateModerators();
        optimizeCoolerSteps();
        calculateCoolers();
        this.heat = (int)heatf;
        this.power = (int)powerf;
        netHeat = this.heat-cooling;
        heatMult = (float)this.heat/cells/configuration.fuelHeat[fuel];
        efficiency = (float)this.power/cells/configuration.fuelPower[fuel];
    }
    public UnderhaulSFR unpack(Configuration config){
        if(!configuration.matches(config))throw new IllegalArgumentException("Unable to unpack Underhaul SFR: Configuration does not match!");
        UnderhaulSFR sfr = new UnderhaulSFR(config, dims[0], dims[1], dims[2], config.underhaul.fissionSFR.allFuels.get(fuel));
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    sfr.setBlock(x+1, y+1, z+1, new Block(config, x+1, y+1, z+1, config.underhaul.fissionSFR.allBlocks.get(blocks[x][y][z])));
                }
            }
        }
        sfr.buildDefaultCasing();
        return sfr;
    }
    private int countAdjacents(int x, int y, int z, boolean[] endTest, boolean[] pathTest, int distance){
        int count = 0;
        for(int[] direction : directions){
            count+=findAdjacent(x, y, z, direction[0], direction[1], direction[2], endTest, pathTest, distance);
        }
        return count;
    }
    private int findAdjacent(int x, int y, int z, int dx, int dy, int dz, boolean[] endTest, boolean[] pathTest, int distance){
        for(int dist = 0; dist<=distance; dist++){
            x+=dx;
            y+=dy;
            z+=dz;
            if(x<0||y<0||z<0||x>=dims[0]||y>=dims[1]||z>=dims[2]){
                return 0;//hit casing
            }
            if(endTest[blocks[x][y][z]])return 1;
            else if(!pathTest[blocks[x][y][z]])return 0;
        }
        return 0;
    }
    @Override
    public void importAndConvert(UnderhaulSFR sfr){
        dims[0] = sfr.getInternalWidth();
        dims[1] = sfr.getInternalHeight();
        dims[2] = sfr.getInternalDepth();
        sfr.forEachInternalPosition((x, y, z) -> {
            Block block = sfr.getBlock(x, y, z);
            String name = block==null?null:block.template.name;
            int b = -1;
            for(int i = 0; i<configuration.blockName.length; i++){
                if(configuration.blockName[i].equals(name))b = i;
            }
            blocks[x-1][y-1][z-1] = b;
        });
        int f = 0;
        for(int i = 0; i<configuration.fuelName.length; i++){
            if(configuration.fuelName[i].equals(sfr.fuel.name))f = i;
        }
        fuel = f;
    }
    @Override
    public String getTooltip(){
        return "Power Generation: "+power+"RF/t\n"
                + "Total Heat: "+heat+"H/t\n"
                + "Total Cooling: "+cooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Efficiency: "+MathUtil.percent(efficiency, 0)+"\n"
                + "Heat multiplier: "+MathUtil.percent(heatMult, 0)+"\n"
                + "Fuel cells: "+cells;
    }
    private void genVariables(){
        vars = new Variable[7+blockCount.length];
        vars[0] = new VariableInt("Net Heat"){
            @Override
            public int getValue(){
                return netHeat;
            }
        };
        vars[1] = new VariableInt("Total Output"){
            @Override
            public int getValue(){
                return power;
            }
        };
        vars[2] = new VariableInt("Total Heat"){
            @Override
            public int getValue(){
                return heat;
            }
        };
        vars[3] = new VariableInt("Total Cooling"){
            @Override
            public int getValue(){
                return cooling;
            }
        };
        vars[4] = new VariableInt("Cell Count"){
            @Override
            public int getValue(){
                return cells;
            }
        };
        vars[5] = new VariableFloat("Total Efficiency"){
            @Override
            public float getValue(){
                return efficiency;
            }
        };
        vars[6] = new VariableFloat("Heat Multiplier"){
            @Override
            public float getValue(){
                return heatMult;
            }
        };
        for(int i = 0; i<blockCount.length; i++){
            int j = i;
            vars[7+i] = new VariableInt("Block Count: "+configuration.blockName[i]){
                @Override
                public int getValue(){
                    return blockCount[j];
                }
            };
        }
    }
    @Override
    public int getVariableCount(){
        if(vars==null)genVariables();
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        if(vars==null)genVariables();
        return vars[i];
    }
    @Override
    public void getMutators(ArrayList<Supplier<Mutator>> mutators){
        mutators.add(() -> {
            return new RandomBlockMutator(this);
        });
        mutators.add(() -> {
            return new RandomFuelMutator(this);
        });
    }
}