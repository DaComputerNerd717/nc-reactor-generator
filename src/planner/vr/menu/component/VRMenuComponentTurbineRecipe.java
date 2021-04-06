package planner.vr.menu.component;
import java.awt.Color;
import multiblock.action.SetTurbineRecipeAction;
import multiblock.configuration.overhaul.turbine.Recipe;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentTurbineRecipe extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Recipe recipe;
    private float textInset = 0;
    private double textOffset = .001f;//1mm
    public VRMenuComponentTurbineRecipe(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, Recipe recipe){
        super(x, y, z, width, height, depth, 0, 0, 0);
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Color col = Core.theme.getEditorListBorderColor();
        if(!isDeviceOver.isEmpty()){
            col = col.brighter();
        }
        Core.applyColor(col);
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getTextColor());
        if(((OverhaulTurbine)editor.getMultiblock()).recipe.equals(recipe)){
            VRCore.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
        drawText(recipe.getInputDisplayName());
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = ((height-textInset*2)*scale)-.005;
        GL11.glPushMatrix();
        GL11.glTranslated(0, height/2, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        Renderer2D.drawCenteredText(0, -textHeight/2, width, textHeight/2, text);
        GL11.glPopMatrix();
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                editor.getMultiblock().action(new SetTurbineRecipeAction(editor, recipe), true);
            }
        }
    }
    @Override    
    public String getTooltip(int device){
        return "Expansion Coefficient: "+recipe.coefficient+"\n"
             + "Energy Density (RF/mb): "+recipe.power;
    }
}