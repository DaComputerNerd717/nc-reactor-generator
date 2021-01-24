package planner.vr.menu.component;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.MSRAllShieldsAction;
import multiblock.action.MSRShieldAction;
import multiblock.action.MSRSourceAction;
import multiblock.action.SFRAllShieldsAction;
import multiblock.action.SFRShieldAction;
import multiblock.action.SFRSourceAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.editor.tool.EditorTool;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.opengl.ImageStash;
public class VRMenuComponentEditorGrid extends VRMenuComponent{
    private final VRMenuEdit editor;
    private final Multiblock multiblock;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .75f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    private long lastTick;
    private final HashMap<Integer, int[]> deviceover = new HashMap<>();
    private final double blockSize;
    public VRMenuComponentEditorGrid(double x, double y, double z, double size, VRMenuEdit editor, Multiblock multiblock){
        super(x, y, z, 0, 0, 0, 0, 0, 0);
        blockSize = size/Math.max(multiblock.getX(), Math.max(multiblock.getY(), multiblock.getZ()));
        width = multiblock.getX()*blockSize;
        height = multiblock.getY()*blockSize;
        depth = multiblock.getZ()*blockSize;
        this.x-=width/2;
        this.y-=height/2;
        this.z-=depth/2;
        this.editor = editor;
        this.multiblock = multiblock;
    }
    @Override
    public void tick(){
        for(int i = 0; i<gui.buttonsWereDown.size(); i++){
            ArrayList<Integer> lst = gui.buttonsWereDown.get(i);
            EditorTool tool = editor.getSelectedTool(i);
            if(tool!=null){
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Trigger))tool.mouseReset(0);
                if(!lst.contains(VR.EVRButtonId_k_EButton_SteamVR_Touchpad))tool.mouseReset(1);
            }
        }
        resonatingTick++;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
        lastTick = System.nanoTime();
    }
    @Override
    public void render(TrackedDevicePose.Buffer tdpb){
        long millisSinceLastTick = (System.nanoTime()-lastTick)/1_000_000;
        float tick = resonatingTick+(Math.max(0, Math.min(1, millisSinceLastTick/50)));
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*tick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render(tdpb);
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        synchronized(deviceover){
            Core.applyColor(Core.theme.getEditorListBorderColor());
            drawCubeOutline(-blockSize/32,-blockSize/32,-blockSize/32,width+blockSize/32,height+blockSize/32,depth+blockSize/32,blockSize/24);
            for(int id : deviceover.keySet()){
                int[] mouseover = deviceover.get(id);
                if(!isDeviceOver.contains(id))mouseover = null;
                if(mouseover!=null){
                    if(mouseover[0]<0||mouseover[1]<0||mouseover[2]<0||mouseover[0]>=multiblock.getX()||mouseover[1]>=multiblock.getY()||mouseover[2]>=multiblock.getZ())mouseover = null;
                }
                if(mouseover==null)deviceover.remove(id);
                else deviceover.put(id, mouseover);
            }
        }
        Core.applyColor(Core.theme.getTextColor());
//        for(int y = 0; y<=multiblock.getY(); y++){
//            for(int z = 0; z<=multiblock.getZ(); z++){
//                double border = blockSize/64d;
//                double Y = y*blockSize;
//                double Z = z*blockSize;
//                VRCore.drawFlatCube(0, Y-border, Z-border, width, Y+border, Z+border);
//            }
//        }
//        for(int x = 0; x<=multiblock.getX(); x++){
//            for(int z = 0; z<=multiblock.getZ(); z++){
//                double border = blockSize/64d;
//                double X = x*blockSize;
//                double Z = z*blockSize;
//                VRCore.drawFlatCube(X-border, 0, Z-border, X+border, height, Z+border);
//            }
//        }
//        for(int x = 0; x<=multiblock.getX(); x++){
//            for(int y = 0; y<=multiblock.getY(); y++){
//                double border = blockSize/64d;
//                double X = x*blockSize;
//                double Y = y*blockSize;
//                VRCore.drawFlatCube(X-border, Y-border, 0, X+border, Y+border, depth);
//            }
//        }
        for(int x = 0; x<multiblock.getX(); x++){//solid stuff
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    Block block = multiblock.getBlock(x, y, z);
                    int xx = x;
                    int yy = y;
                    int zz = z;
                    double X = x*blockSize;
                    double Y = y*blockSize;
                    double Z = z*blockSize;
                    double border = blockSize/16;
                    if(block!=null){
                        block.render(X, Y, Z, blockSize, blockSize, blockSize, true, multiblock, (t) -> {
                            Block b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                            return b==null||b.isCasing();
                        });
                    }
                    //TODO VR: draw selection box
                    //TODO VR: draw suggestions
                }
            }
        }
        for(int x = 0; x<multiblock.getX(); x++){//transparent stuff
            for(int y = 0; y<multiblock.getY(); y++){
                for(int z = 0; z<multiblock.getZ(); z++){
                    Block block = multiblock.getBlock(x, y, z);
                    int xx = x;
                    int yy = y;
                    int zz = z;
                    double X = x*blockSize;
                    double Y = y*blockSize;
                    double Z = z*blockSize;
                    double border = blockSize/16;
                    if(block!=null){
                        //TODO VR: draw same fuel markers
//                            if((multiblock instanceof OverhaulMSR&&((multiblock.overhaul.fissionmsr.Block)block).fuel==editor.getSelectedOverMSRFuel())||(multiblock instanceof OverhaulSFR&&((multiblock.overhaul.fissionsfr.Block)block).fuel==editor.getSelectedOverSFRFuel())){
//                                Core.applyColor(Core.theme.getSelectionColor(), resonatingAlpha);
//                                Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
//                            }
                    }
                    if(multiblock instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multiblock).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                        Core.applyWhite();
                        VRCore.drawCube(X, Y, Z, X+blockSize, Y+blockSize, Z+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"), (t) -> {
                            Block b = multiblock.getBlock(xx+t.x, yy+t.y, zz+t.z);
                            return b==null&&((OverhaulFusionReactor)multiblock).getLocationCategory(xx+t.x, yy+t.y, zz+t.z)!=OverhaulFusionReactor.LocationCategory.PLASMA;
                        });
                    }
                    //TODO VR: draw quick-replace ghost blocks
                }
            }
        }
        for(int i = 0; i<editor.getCursorCount(); i++){
            editor.getSelectedTool(i).drawVRGhosts(0, 0, 0, width, height, depth, blockSize, (editor.getSelectedBlock(i)==null?0:Core.getTexture(editor.getSelectedBlock(i).getTexture())));
        }
        synchronized(deviceover){
            for(int id : deviceover.keySet()){
                if(id==VR.k_unTrackedDeviceIndex_Hmd)continue;//don't do mouseover for headset
                int[] mouseover = deviceover.get(id);
                if(mouseover!=null){
                    double X = mouseover[0]*blockSize;
                    double Y = mouseover[1]*blockSize;
                    double Z = mouseover[2]*blockSize;
                    double border = blockSize/16;
                    Core.applyColor(Core.theme.getEditorListBorderColor());
                    drawCubeOutline(X-border/2, Y-border/2, Z-border/2, X+blockSize+border/2, Y+blockSize+border/2, Z+blockSize+border/2, border);
                }
            }
        }
    }
    @Override
    public void onDeviceMoved(int device, Matrix4f matrix){
        super.onDeviceMoved(device, matrix);
        Vector3f pos = matrix.getTranslation(new Vector3f());
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        synchronized(deviceover){
            deviceover.put(device, new int[]{(int)(x/blockSize),(int)(y/blockSize),(int)(z/blockSize)});
        }
        for(int i : gui.buttonsWereDown.get(device)){
            deviceDragged(device, matrix, i);
        }
        if(Double.isNaN(x)||Double.isNaN(y)||Double.isNaN(z))return;
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int)(x/blockSize)));
        int blockY = Math.max(0, Math.min(multiblock.getY()-1, (int)(y/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int)(z/blockSize)));
        editor.getSelectedTool(device).mouseMoved(this, blockX, blockY, blockZ);
    }
    @Override
    public void onDeviceMovedElsewhere(int device, Matrix4f matrix){
        super.onDeviceMovedElsewhere(device, matrix);
        synchronized(deviceover){
            if(deviceover.containsKey(device))editor.getSelectedTool(device).mouseMovedElsewhere(this);
            deviceover.remove(device);
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        int mButton = -1;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger)mButton = 0;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Touchpad)mButton = 1;
        int[] mouseover;
        synchronized(deviceover){
            if(!deviceover.containsKey(device))return;
            mouseover = deviceover.get(device);
        }
        int blockX = mouseover[0];
        int blockY = mouseover[1];
        int blockZ = mouseover[2];
        if(pressed){
            if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulSFR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ)).isFuelCell()&&!((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ)).fuel.selfPriming){
                multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionSFR.allSources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionSFR.allSources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionSFR.allSources.size()-1;
                    multiblock.action(new SFRSourceAction(b, index==-1?null:Core.configuration.overhaul.fissionSFR.allSources.get(index)), true);
                }
            }else if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulMSR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ)).isFuelVessel()&&!((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ)).fuel.selfPriming){
                multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionMSR.allSources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionMSR.allSources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionMSR.allSources.size()-1;
                    multiblock.action(new MSRSourceAction(b, index==-1?null:Core.configuration.overhaul.fissionMSR.allSources.get(index)), true);
                }
            }else if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulSFR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                multiblock.overhaul.fissionsfr.Block b = (multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    if(editor.isControlPressed(device))multiblock.action(new SFRAllShieldsAction(!b.closed), true);
                    else multiblock.action(new SFRShieldAction(b), true);
                }
            }else if(editor.getSelectedTool(device).isEditTool()&&multiblock instanceof OverhaulMSR&&editor.isShiftPressed(device)&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ))!=null&&((multiblock.overhaul.fissionmsr.Block)multiblock.getBlock(blockX, blockY, blockZ)).template.shield){
                multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) multiblock.getBlock(blockX, blockY, blockZ);
                if(b!=null){
                    if(editor.isControlPressed(device))multiblock.action(new MSRAllShieldsAction(!b.closed), true);
                    else multiblock.action(new MSRShieldAction(b), true);
                }
            }else{
                //TODO VR: PICK BLOCK
                editor.getSelectedTool(device).mousePressed(this, blockX, blockY, blockZ, mButton);
            }
        }else{
            editor.getSelectedTool(device).mouseReleased(this, blockX, blockY, blockZ, mButton);
        }
    }
    private void deviceDragged(int device, Matrix4f matrix, int button){
        int mButton = -1;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger)mButton = 0;
        if(button==VR.EVRButtonId_k_EButton_SteamVR_Touchpad)mButton = 1;
        if(mButton==-1)return;
        Vector3f pos = matrix.getTranslation(new Vector3f());
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockY = Math.max(0, Math.min(multiblock.getY()-1, (int) (y/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (z/blockSize)));
        editor.getSelectedTool(device).mouseDragged(this, blockX, blockY, blockZ, mButton);
    }
    public boolean isSelected(int id, int x, int y, int z){
        return editor.isSelected(id, x, y, z);
    }
    private void drawCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness){
        //111 to XYZ
        VRCore.drawCube(x1, y1, z1, x2, y1+thickness, z1+thickness, 0);
        VRCore.drawCube(x1, y1, z1, x1+thickness, y2, z1+thickness, 0);
        VRCore.drawCube(x1, y1, z1, x1+thickness, y1+thickness, z2, 0);
        //X2 to YZ
        VRCore.drawCube(x2-thickness, y1, z1, x2, y2, z1+thickness, 0);
        VRCore.drawCube(x2-thickness, y1, z1, x2, y1+thickness, z2, 0);
        //Y2 to XZ
        VRCore.drawCube(x1, y2-thickness, z1, x2, y2, z1+thickness, 0);
        VRCore.drawCube(x1, y2-thickness, z1, x1+thickness, y2, z2, 0);
        //Z2 to XY
        VRCore.drawCube(x1, y1, z2-thickness, x2, y1+thickness, z2, 0);
        VRCore.drawCube(x1, y1, z2-thickness, x1+thickness, y2, z2, 0);
        //XYZ to 222
        VRCore.drawCube(x1, y2-thickness, z2-thickness, x2, y2, z2, 0);
        VRCore.drawCube(x2-thickness, y1, z2-thickness, x2, y2, z2, 0);
        VRCore.drawCube(x2-thickness, y2-thickness, z1, x2, y2, z2, 0);
    }
}