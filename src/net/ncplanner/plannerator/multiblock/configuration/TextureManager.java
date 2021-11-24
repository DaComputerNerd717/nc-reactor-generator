package net.ncplanner.plannerator.multiblock.configuration;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.MathUtil;
public class TextureManager{
    private static final HashMap<String, Image> imageMap = new HashMap<>();
    public static Image getImageRaw(String texture){
        if(imageMap.containsKey(texture))return imageMap.get(texture);
        try{
            if(new File("nbproject").exists()){
                imageMap.put(texture, ImageIO.read(new File("src/"+texture)));
                return imageMap.get(texture);
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals(texture)){
                        imageMap.put(texture, ImageIO.read(jar.getInputStream(file)));
                        return imageMap.get(texture);
                    }
                }
            }
            imageMap.put(texture, new Image(1, 1));
            throw new IllegalArgumentException("Cannot find file: "+texture);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            imageMap.put(texture, new Image(1, 1));
            return imageMap.get(texture);
        }
    }
    public static Image getImage(String texture){
        return getImageRaw("textures/"+texture+".png");
    }
    public static boolean SEPARATE_BRIGHT_TEXTURES = true;
    public static final float IMG_FAC = .003925f;
    public static final float IMG_POW = 2f;
    public static final float IMG_STRAIGHT_FAC = 1.5f;
    public static int convert(int c){
        if(SEPARATE_BRIGHT_TEXTURES){
            double f = IMG_FAC*MathUtil.pow(c, IMG_POW);
            float g = c/255f;
            double h = f*MathUtil.pow(g, IMG_STRAIGHT_FAC)+c*(1-MathUtil.pow(g, IMG_STRAIGHT_FAC));
            c = (int)h;
        }
        return c;
    }
    public static Color convert(Color color){
        return new Color(convert(color.getRed()), convert(color.getGreen()), convert(color.getBlue()), color.getAlpha());
    }
    public static Image convert(Image image){
        Image converted = new Image(image.getWidth(), image.getHeight());
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y));
                converted.setRGB(x, y, convert(col).getRGB());
            }
        }
        return converted;
    }
}