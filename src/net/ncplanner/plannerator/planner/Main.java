package net.ncplanner.plannerator.planner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main{
    public static final String issueTrackerLink = "https://github.com/ThizThizzyDizzy/nc-reactor-generator/issues";
    private static final ArrayList<String[]> requiredLibraries = new ArrayList<>();
    //Download details
    private static int total;
    private static int current;
    //OS details
    public static final int OS_UNKNOWN = -1;
    public static final int OS_WINDOWS = 0;
    public static final int OS_MACOS = 1;
    public static final int OS_LINUX = 2;
    public static int os = OS_UNKNOWN;
    //other stuff
    public static boolean isBot = false;
    public static boolean headless = false;
    public static boolean novr = false;
    public static String discordBotToken;
    public static boolean benchmark = false;
    public static boolean justUpdated = false;
    private static void addRequiredLibrary(String url, String filename){
        requiredLibraries.add(new String[]{url,filename});
    }
    private static void addRequiredLibraries(){
        addRequiredLibrary("https://github.com/tomdodd4598/Dodd-Simple-Stack-Language/releases/download/4.2/dssl-4.2.jar", "dssl-4.2.jar");
        addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/joml-1.10.5.jar", "joml-1.10.5.jar");
        if(isBot){
            addRequiredLibrary("https://github.com/DV8FromTheWorld/JDA/releases/download/v4.3.0/JDA-4.3.0_277-withDependencies-min.jar", "JDA-4.3.0_277-withDependencies-min.jar");
        }
    }
    public static void main(String[] args){
        try{
            FOR:for(int i = 0; i<args.length; i++){
                switch(args[i]){
                    case "justUpdated":
                        justUpdated = true;
                        break;
                    case "headless":
                        headless = true;
                        break;
                    case "novr":
                        novr = true;
                        break;
                    case "benchmark":
                        benchmark = true;
                        break FOR;
                    case "maybediscord":
                        System.out.println("Bot or Planner? (B|P)\n> ");
                        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
                        String s = r.readLine();
                        if(s==null)s = "";
                        s = s.trim();
                        r.close();
                        if(s.equalsIgnoreCase("B")||s.equalsIgnoreCase("Bot")||s.equalsIgnoreCase("Discord"))args[i] = "discord";
                    case "discord":
                        if(args[i].equals("discord")){
                            isBot = true;
                            discordBotToken = args[i+1];
                        }
                        break;
                }
            }
            System.out.println("Initializing...");
            args = update(args);
            if(args==null){
                return;
            }
            Core.main(args);
        }catch(Exception ex){
            boolean saved = false;
            try{
                Core.autosave();
                saved = true;
            }catch(Exception e){}
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Exception on main thread! (Autosave "+(saved?"Successful":"Failed")+")", ex);
            generateCrashReport("Exception on main thread! (Autosave "+(saved?"Successful":"Failed")+") ", ex);
            System.exit(0);
        }
    }
    private static String getLibraryRoot(){
        return "libraries";
    }
    private static String[] update(String[] args) throws URISyntaxException, IOException, InterruptedException{
        ArrayList<String> theargs = new ArrayList<>(Arrays.asList(args));
        String osName = System.getProperty("os.name");
        if(osName==null)osName = "null";
        osName = osName.toLowerCase(Locale.ROOT);
        if(osName.contains("win"))os = OS_WINDOWS;
        if(osName.contains("mac"))os = OS_MACOS;
        if(osName.contains("nix")||osName.contains("nux")||osName.contains("aix"))os = OS_LINUX;
        if(args.length<1||!args[0].equals("Skip Dependencies")){
            if(os==OS_UNKNOWN){
                throw new IllegalArgumentException("Unknown OS: "+osName);
            }
            final int ARCH_UNKNOWN = -1;
            final int ARCH_X86 = 0;
            final int ARCH_X64 = 1;
            final int ARCH_ARM32 = 3;
            final int ARCH_ARM64 = 4;
            int arch = ARCH_UNKNOWN;
            String osArch = System.getProperty("os.arch");
            if(osArch==null)osArch = "null";
            osArch = osArch.toLowerCase(Locale.ROOT);
            if(osArch.equals("amd64"))arch = ARCH_X64;
            if(osArch.equals("x64"))arch = ARCH_X64;
            if(osArch.equals("x86"))arch = ARCH_X86;
            if(osArch.equals("arm32"))arch = ARCH_ARM32;
            if(osArch.equals("arm64"))arch = ARCH_ARM64;
            if(arch==ARCH_UNKNOWN){
                System.err.println("Unknown Architecture: "+osArch+"!\nAssuming x64 architecture...");
                arch = ARCH_X64;
            }
            if((arch==ARCH_ARM32||arch==ARCH_ARM64)&&!novr){
                System.err.println("OpenVR is not supported on arm! Excluding OpenVR libraries.");
                novr = true;
            }
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp.jar", "lwjgl-3.3.3-assimp.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw.jar", "lwjgl-3.3.3-glfw.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal.jar", "lwjgl-3.3.3-openal.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl.jar", "lwjgl-3.3.3-opengl.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb.jar", "lwjgl-3.3.3-stb.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3.jar", "lwjgl-3.3.3.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjglx-debug-1.0.0.jar", "lwjglx-debug-1.0.0.jar");
            if(!novr)addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openvr.jar", "lwjgl-3.3.3-openvr.jar");
            addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd.jar", "lwjgl-3.3.3-nfd.jar");
            switch(os){
                case OS_WINDOWS:
                    {
                        System.out.println("OS: Windows");
                        switch(arch){
                            case ARCH_X86:
                                System.out.println("OS Architecture: x86");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-windows-x86.jar", "lwjgl-3.3.3-assimp-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-windows-x86.jar", "lwjgl-3.3.3-glfw-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-windows-x86.jar", "lwjgl-3.3.3-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-windows-x86.jar", "lwjgl-3.3.3-openal-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-windows-x86.jar", "lwjgl-3.3.3-opengl-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-windows-x86.jar", "lwjgl-3.3.3-stb-natives-windows-x86.jar");
                                if(!novr)addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openvr-natives-windows-x86.jar", "lwjgl-3.3.3-openvr-natives-windows-x86.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-windows-x86.jar", "lwjgl-3.3.3-nfd-natives-windows-x86.jar");
                                break;
                            case ARCH_X64:
                                System.out.println("OS Architecture: x64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-windows.jar", "lwjgl-3.3.3-assimp-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-windows.jar", "lwjgl-3.3.3-glfw-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-windows.jar", "lwjgl-3.3.3-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-windows.jar", "lwjgl-3.3.3-openal-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-windows.jar", "lwjgl-3.3.3-opengl-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-windows.jar", "lwjgl-3.3.3-stb-natives-windows.jar");
                                if(!novr)addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openvr-natives-windows.jar", "lwjgl-3.3.3-openvr-natives-windows.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-windows.jar", "lwjgl-3.3.3-nfd-natives-windows.jar");
                                break;
                            case ARCH_ARM64:
                                System.out.println("OS Architecture: arm64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-windows-arm64.jar", "lwjgl-3.3.3-assimp-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-windows-arm64.jar", "lwjgl-3.3.3-glfw-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-windows-arm64.jar", "lwjgl-3.3.3-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-windows-arm64.jar", "lwjgl-3.3.3-openal-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-windows-arm64.jar", "lwjgl-3.3.3-opengl-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-windows-arm64.jar", "lwjgl-3.3.3-stb-natives-windows-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-windows-arm64.jar", "lwjgl-3.3.3-nfd-natives-windows-arm64.jar");
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported OS Architecture: "+osArch+" on "+osName);
                        }
                    }
                    break;
                case OS_MACOS:
                    {
                        System.out.println("OS: Mac OS");
                        switch(arch){
                            case ARCH_X64:
                                System.out.println("OS Architecture: x64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-macos.jar", "lwjgl-3.3.3-assimp-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-macos.jar", "lwjgl-3.3.3-glfw-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-macos.jar", "lwjgl-3.3.3-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-macos.jar", "lwjgl-3.3.3-openal-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-macos.jar", "lwjgl-3.3.3-opengl-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-macos.jar", "lwjgl-3.3.3-stb-natives-macos.jar");
                                if(!novr)addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openvr-natives-macos.jar", "lwjgl-3.3.3-openvr-natives-macos.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-macos.jar", "lwjgl-3.3.3-nfd-natives-macos.jar");
                                break;
                            case ARCH_ARM64:
                                System.out.println("OS Architecture: arm64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-macos-arm64.jar", "lwjgl-3.3.3-assimp-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-macos-arm64.jar", "lwjgl-3.3.3-glfw-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-macos-arm64.jar", "lwjgl-3.3.3-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-macos-arm64.jar", "lwjgl-3.3.3-openal-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-macos-arm64.jar", "lwjgl-3.3.3-opengl-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-macos-arm64.jar", "lwjgl-3.3.3-stb-natives-macos-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-macos-arm64.jar", "lwjgl-3.3.3-nfd-natives-macos-arm64.jar");
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported OS Architecture: "+osArch+" on "+osName);
                        }
                    }
                    break;
                case OS_LINUX:
                    {
                        System.out.println("OS: Linux");
                        switch(arch){
                            case ARCH_X64:
                                System.out.println("OS Architecture: x64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-linux.jar", "lwjgl-3.3.3-assimp-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-linux.jar", "lwjgl-3.3.3-glfw-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-linux.jar", "lwjgl-3.3.3-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-linux.jar", "lwjgl-3.3.3-openal-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-linux.jar", "lwjgl-3.3.3-opengl-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-linux.jar", "lwjgl-3.3.3-stb-natives-linux.jar");
                                if(!novr)addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openvr-natives-linux.jar", "lwjgl-3.3.3-openvr-natives-linux.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-linux.jar", "lwjgl-3.3.3-nfd-natives-linux.jar");
                                break;
                            case ARCH_ARM32:
                                System.out.println("OS Architecture: arm32");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-linux-arm32.jar", "lwjgl-3.3.3-assimp-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-linux-arm32.jar", "lwjgl-3.3.3-glfw-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-linux-arm32.jar", "lwjgl-3.3.3-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-linux-arm32.jar", "lwjgl-3.3.3-openal-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-linux-arm32.jar", "lwjgl-3.3.3-opengl-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-linux-arm32.jar", "lwjgl-3.3.3-stb-natives-linux-arm32.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-linux-arm32.jar", "lwjgl-3.3.3-nfd-natives-linux-arm32.jar");
                                break;
                            case ARCH_ARM64:
                                System.out.println("OS Architecture: arm64");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-assimp-natives-linux-arm64.jar", "lwjgl-3.3.3-assimp-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-glfw-natives-linux-arm64.jar", "lwjgl-3.3.3-glfw-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-natives-linux-arm64.jar", "lwjgl-3.3.3-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-openal-natives-linux-arm64.jar", "lwjgl-3.3.3-openal-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-opengl-natives-linux-arm64.jar", "lwjgl-3.3.3-opengl-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-stb-natives-linux-arm64.jar", "lwjgl-3.3.3-stb-natives-linux-arm64.jar");
                                addRequiredLibrary("https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/lwjgl-3.3.3-nfd-natives-linux-arm64.jar", "lwjgl-3.3.3-nfd-natives-linux-arm64.jar");
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported OS Architecture: "+osArch+" on "+osName);
                        }
                    }
                    break;
            }
            addRequiredLibraries();
            total = requiredLibraries.size();
            File[] requiredLibs = new File[requiredLibraries.size()];
            int n = 0;
            System.out.println("Checking Libraries...");
            int failed = 0;
            for(String[] lib : requiredLibraries){
                String url = lib[0];
                String filename = lib[1];
                requiredLibs[n] = downloadFile(url, new File(getLibraryRoot()+File.separatorChar+filename));
                System.out.println("Checking... "+MathUtil.round(100d*current/total)+"% ("+current+"/"+total+")");
                if(requiredLibs[n]==null){
                    failed++;
                    System.err.println("Failed to download library: "+filename+"!");
                }
                n++;
            }
            if(failed>0)throw new RuntimeException("Failed to download "+failed+" librar"+(failed==1?"y":"ies")+"!");
            System.out.println("Libraries OK");
            String[] additionalClasspathElements = new String[requiredLibs.length];
            for(int i = 0; i<requiredLibs.length; i++){
                additionalClasspathElements[i] = requiredLibs[i].getAbsolutePath();
            }
            theargs.add(0, "Skip Dependencies");
            final Process p = restart(new String[0], theargs.toArray(new String[theargs.size()]), additionalClasspathElements, Main.class);
            final int[] finished = {0};
            new Thread("System.out transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.out.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            new Thread("System.err transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.err.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            Thread inTransfer = new Thread("System.in transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    PrintWriter out = new PrintWriter(p.getOutputStream());
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            out.println(line);
                            out.flush();
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }
            };
            inTransfer.setDaemon(true);
            inTransfer.start();
            return null;
        }
        theargs.remove(0);
        return theargs.toArray(new String[theargs.size()]);
    }
    public static File downloadFile(String link, File destinationFile){
        current++;
        if(destinationFile.exists()||link==null){
            return destinationFile;
        }
        System.out.println("Downloading "+destinationFile.getName()+"...");
        if(destinationFile.getParentFile()!=null)destinationFile.getParentFile().mkdirs();
        try {
            URL url = new URL(link);
            int fileSize;
            URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).setRequestMethod("HEAD");
                int code = ((HttpURLConnection)connection).getResponseCode();
                if (code / 100 == 3) {
                    return null;
                }
            }
            fileSize = connection.getContentLength();
            byte[] buffer = new byte[65535];
            int unsuccessfulAttempts = 0;
            int maxUnsuccessfulAttempts = 3;
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                URLConnection urlconnection = url.openConnection();
                if ((urlconnection instanceof HttpURLConnection)) {
                    urlconnection.setRequestProperty("Cache-Control", "no-cache");
                    urlconnection.connect();
                }
                String targetFile = destinationFile.getName();
                FileOutputStream fos;
                int downloadedFileSize;
                try (InputStream inputstream=getRemoteInputStream(targetFile, urlconnection)) {
                    fos=new FileOutputStream(destinationFile);
                    downloadedFileSize=0;
                    int read;
                    while ((read = inputstream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedFileSize += read;
                    }
                }
                fos.close();
                if (((urlconnection instanceof HttpURLConnection)) && 
                    ((downloadedFileSize != fileSize) && (fileSize > 0))){
                    unsuccessfulAttempts++;
                    if (unsuccessfulAttempts < maxUnsuccessfulAttempts){
                        downloadFile = true;
                    }else{
                        throw new Exception("failed to download "+targetFile);
                    }
                }
            }
            return destinationFile;
        }catch (Exception ex){
            return null;
        }
    }
    public static InputStream getRemoteInputStream(String currentFile, final URLConnection urlconnection) throws Exception {
        final InputStream[] is = new InputStream[1];
        for (int j = 0; (j < 3) && (is[0] == null); j++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        is[0] = urlconnection.getInputStream();
                    }catch (IOException localIOException){}
                }
            };
            t.setName("FileDownloadStreamThread");
            t.start();
            int iterationCount = 0;
            while ((is[0] == null) && (iterationCount++ < 5)){
                try {
                    t.join(1000L);
                } catch (InterruptedException localInterruptedException) {
                }
            }
            if (is[0] != null){
                continue;
            }
            try {
                t.interrupt();
                t.join();
            } catch (InterruptedException localInterruptedException1) {
            }
        }
        if (is[0] == null) {
            throw new Exception("Unable to download "+currentFile);
        }
        return is[0];
    }
    public static InputStream getRemoteInputStream(String link){
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).setRequestMethod("HEAD");
                int code = ((HttpURLConnection)connection).getResponseCode();
                if (code / 100 == 3) {
                    return null;
                }
            }
            URLConnection urlconnection = url.openConnection();
            if ((urlconnection instanceof HttpURLConnection)) {
                urlconnection.setRequestProperty("Cache-Control", "no-cache");
                urlconnection.connect();
            }
            return getRemoteInputStream(null, urlconnection);
        }catch (Exception ex){
            return null;
        }
    }
    /**
     * Restarts the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param additionalFiles Any additional files to include in the classpath
     * @param mainClass The program's main class.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process restart(String[] vmArgs, String[] applicationArgs, String[] additionalFiles, Class<?> mainClass) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        if(os==OS_MACOS)params.add("-XstartOnFirstThread");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-classpath");
        String filepath = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String separator = System.getProperty("path.separator");
        for(String str : additionalFiles){
            filepath+=separator+str;
        }
        params.add(filepath);
        params.add(mainClass.getName());
        params.addAll(Arrays.asList(applicationArgs));
        System.out.println(params.toString());
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
    /**
     * Starts the requested Java application the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param file The program file.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process startJava(String[] vmArgs, String[] applicationArgs, File file) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        if(os==OS_MACOS)params.add("-XstartOnFirstThread");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-jar");
        params.add(file.getAbsolutePath());
        params.addAll(Arrays.asList(applicationArgs));
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
    public static void generateCrashReport(String message, Throwable ex){
        GregorianCalendar calendar = new GregorianCalendar();
        File file = new File("crash-reports"+File.separatorChar+"crash-"+calendar.getTime().toString().replace(":", "-")+".txt");
        if(!file.getParentFile().exists())file.getParentFile().mkdirs();
        int i = 1;
        while(file.exists()){
            file = new File("crash-reports"+File.separatorChar+"crash-"+calendar.getTime().toString().replace(":", "-")+"_"+i+".txt");
            i++;
        }
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))){
            writer.write("Planner version "+VersionManager.currentVersion+"\n");
            writer.write("OS: "+System.getProperty("os.name")+" ("+System.getProperty("os.arch")+")\n\n");
            writer.write(message+"\n");
            if(ex!=null){
                writer.write(ex.getClass().getName()+": "+ex.getMessage()+"\n");
                for(StackTraceElement e : ex.getStackTrace()){
                    writer.write(" at "+e.toString()+"\n");
                }
                Throwable throwable = ex;
                while(throwable.getCause()!=null){
                    throwable = throwable.getCause();
                    writer.write("Caused by "+throwable.getClass().getName()+": "+throwable.getMessage()+"\n");
                    for(StackTraceElement e : throwable.getStackTrace()){
                        writer.write(" at "+e.toString()+"\n");
                    }
                }
            }
            writer.write("\n"+Core.getCrashReportData()+"\n");
            writer.write("Threads: \n");
            Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
            for(Thread t : threads.keySet()){
                writer.write(t.getName()+": "+t.getState().toString()+(t.isDaemon()?" [D]":"")+"\n");
                for(StackTraceElement e : threads.get(t)){
                    writer.write("  "+e.toString()+"\n");
                }
            }
        }catch(IOException e){}
    }
}