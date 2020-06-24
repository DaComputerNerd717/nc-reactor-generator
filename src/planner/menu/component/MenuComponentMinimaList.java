package planner.menu.component;
import java.awt.Color;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.components.MenuComponentList;
public class MenuComponentMinimaList extends MenuComponentList{
    private Color backgroundColor = new Color(.4f, .4f, .8f, 1f);
    public MenuComponentMinimaList(double x, double y, double width, double height, double scrollbarWidth){
        super(x, y, width, height, scrollbarWidth);
        color = new Color(.25f, .25f, .5f, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
    }
    @Override
    public void drawUpwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/2, y+height/4);
        GL11.glVertex2d(x+width/4, y+3*height/4);
        GL11.glVertex2d(x+3*width/4, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawDownwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/4);
        GL11.glVertex2d(x+3*width/4, y+height/4);
        GL11.glVertex2d(x+width/2, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawRightwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/4);
        GL11.glVertex2d(x+width/4, y+3*height/4);
        GL11.glVertex2d(x+3*width/4, y+height/2);
        GL11.glEnd();
    }
    @Override
    public void drawLeftwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/2);
        GL11.glVertex2d(x+3*width/4, y+height/4);
        GL11.glVertex2d(x+3*width/4, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawVerticalScrollbarBackground(double x, double y, double width, double height){
        GL11.glColor3f(backgroundColor.getRed()/255F, backgroundColor.getGreen()/255F, backgroundColor.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawVerticalScrollbarForeground(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarBackground(double x, double y, double width, double height){
        GL11.glColor3f(backgroundColor.getRed()/255F, backgroundColor.getGreen()/255F, backgroundColor.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarForeground(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawButton(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor3f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x+1, y);
        GL11.glVertex2d(x+1, y+height-1);
        GL11.glVertex2d(x+1, y+height-1);
        GL11.glVertex2d(x+width, y+height-1);
        GL11.glVertex2d(x+width, y+height-1);
        GL11.glVertex2d(x+width, y);
        GL11.glVertex2d(x+width, y);
        GL11.glVertex2d(x+1, y);
        GL11.glEnd();
    }
    @Override
    public void persistMouseEvent(int button, boolean pressed, float x, float y){
        if(button==-1&&Mouse.isButtonDown(0)){
            button = 0;
            pressed = true;
        }
        super.persistMouseEvent(button, pressed, x, y);
    }
    @Override
    public void renderBackground(){
        setScrollMagnitude(Math.min(width, height)/20);
        for(int i = 0; i<components.size(); i++){
            components.get(i).isSelected = getSelectedIndex()==i;
        }
        super.renderBackground();
    }
}