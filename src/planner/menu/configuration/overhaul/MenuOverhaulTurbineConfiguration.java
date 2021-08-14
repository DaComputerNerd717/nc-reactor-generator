package planner.menu.configuration.overhaul;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.turbine.Block;
import multiblock.configuration.overhaul.turbine.Recipe;
import planner.Core;
import planner.menu.configuration.MultiblockConfigurationMenu;
import planner.menu.configuration.overhaul.turbine.MenuBlockConfiguration;
import planner.menu.configuration.overhaul.turbine.MenuComponentBlock;
import planner.menu.configuration.overhaul.turbine.MenuComponentRecipe;
import planner.menu.configuration.overhaul.turbine.MenuRecipeConfiguration;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuOverhaulTurbineConfiguration extends MultiblockConfigurationMenu{
    public MenuOverhaulTurbineConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, "Overhaul Turbine");
        addSettingInt("Minimum Width", configuration.overhaul.turbine::getMinWidth, configuration.overhaul.turbine::setMinWidth).setTooltip("The minimum width of this multiblock");
        addSettingInt("Maximum Size", configuration.overhaul.turbine::getMaxSize, configuration.overhaul.turbine::setMaxSize).setTooltip("The maximum size of this multiblock");
        addSettingFloat("Throughput Efficiency Leniency Mult", configuration.overhaul.turbine::getThroughputEfficiencyLeniencyMult, configuration.overhaul.turbine::setThroughputEfficiencyLeniencyMult);
        addSettingFloat("Throughput Factor", configuration.overhaul.turbine::getThroughputFactor, configuration.overhaul.turbine::setThroughputFactor);
        addSettingRow();
        addSettingInt("Minimum Length", configuration.overhaul.turbine::getMinLength, configuration.overhaul.turbine::setMinLength).setTooltip("The minimum length of this multiblock");
        addSettingInt("Fluid Per Blade", configuration.overhaul.turbine::getFluidPerBlade, configuration.overhaul.turbine::setFluidPerBlade).setTooltip("The maximum fluid input per blade");
        addSettingFloat("Throughput Efficiency Leniency Threshold", configuration.overhaul.turbine::getThroughputEfficiencyLeniencyThreshold, configuration.overhaul.turbine::setThroughputEfficiencyLeniencyThreshold);
        addSettingFloat("Power Bonus", configuration.overhaul.turbine::getPowerBonus, configuration.overhaul.turbine::setPowerBonus);
        addList(() -> {return "Blocks ("+configuration.overhaul.turbine.blocks.size()+")";}, "Add Block", ()->{
            Block b = new Block("nuclearcraft:new_block");
            configuration.overhaul.turbine.blocks.add(b);
            Core.configuration.overhaul.turbine.allBlocks.add(b);
            gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
        }, (list)->{
            for(Block b : configuration.overhaul.turbine.blocks){
                list.add(new MenuComponentBlock(b, ()->{//edit
                    gui.open(new MenuBlockConfiguration(gui, this, configuration, b));
                }, ()->{//delete
                    configuration.overhaul.turbine.blocks.remove(b);
                    Core.configuration.overhaul.turbine.allBlocks.remove(b);
                    refresh();
                }));
            }
        });
        addList(() -> {return "Recipes ("+configuration.overhaul.turbine.recipes.size()+")";}, "Add Recipe", ()->{
            Recipe r = new Recipe("input_fluid", "output_fluid", 0, 0);
            configuration.overhaul.turbine.recipes.add(r);
            Core.configuration.overhaul.turbine.allRecipes.add(r);
            gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
        }, (list)->{
            for(Recipe r : configuration.overhaul.turbine.recipes){
                list.add(new MenuComponentRecipe(r, ()->{//edit
                    gui.open(new MenuRecipeConfiguration(gui, this, configuration, r));
                }, ()->{//delete
                    configuration.overhaul.turbine.recipes.remove(r);
                    Core.configuration.overhaul.turbine.allRecipes.remove(r);
                    refresh();
                }));
            }
        });
    }
}