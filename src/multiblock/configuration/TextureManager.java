package multiblock.configuration;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import planner.Main;
public class TextureManager{
    public static BufferedImage getImage(String texture){
        try{
            if(new File("nbproject").exists()){
                return ImageIO.read(new File("src/textures/"+texture+".png"));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals("textures/"+texture+".png")){
                        return ImageIO.read(jar.getInputStream(file));
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find file: "+texture);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
    }
    public static boolean SEPARATE_BRIGHT_TEXTURES = true;
    public static final float IMG_FAC = .003925f;
    public static final float IMG_POW = 2f;
    public static final float IMG_STRAIGHT_FAC = 1.5f;
    public static int convert(int c){
        if(SEPARATE_BRIGHT_TEXTURES){
            double f = IMG_FAC*Math.pow(c, IMG_POW);
            float g = c/255f;
            double h = f*Math.pow(g, IMG_STRAIGHT_FAC)+c*(1-Math.pow(g, IMG_STRAIGHT_FAC));
            c = (int)h;
        }
        return c;
    }
    public static Color convert(Color color){
        return new Color(convert(color.getRed()), convert(color.getGreen()), convert(color.getBlue()), color.getAlpha());
    }
    public static BufferedImage convert(BufferedImage image){
        BufferedImage converted = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y), true);
                converted.setRGB(x, y, convert(col).getRGB());
            }
        }
        return converted;
    }
}