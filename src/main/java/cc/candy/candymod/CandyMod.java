package cc.candy.candymod;

import cc.candy.candymod.managers.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = CandyMod.MODID, name = CandyMod.NAME, version = CandyMod.VERSION)
public class CandyMod
{
    public static final String MODID = "candyplus";
    public static final String NAME = "Candy+";
    public static final String VERSION = "0.2.6";

    public static ModuleManager m_module = new ModuleManager();
    public static EventManager m_event = new EventManager();
    public static FontManager m_font = new FontManager();
    public static HoleManager m_hole = new HoleManager();
    public static RpcManager m_rpc = new RpcManager();
    public static RotateManager m_rotate = new RotateManager();
    public static NotificationManager m_notif = new NotificationManager();

    private static Logger logger;
    private static boolean savedConfig = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Info("floppa is cute but jumin is gay - 2021");
        Info("loading Candy...");

        Display.setTitle(NAME + " " + VERSION);
        //init managers
        m_event.load();
        m_module.load();
        m_font.load();
        m_hole.load();
        m_rpc.load();
        m_rotate.load();
        m_notif.load();

        Info("loading configs...");
        ConfigManager.loadConfigs();

        Info("successfully load Candy!");
    }

    public static void unload()
    {
        if(!savedConfig) {
            Info("saving configs...");
            ConfigManager.saveConfigs();
            Info("successfully save configs!");
            savedConfig = true;
        }
    }

    public static void Info(String msg)
    {
        if(logger == null) return;
        logger.info(msg);
    }

    public static void Log(Level level , String msg)
    {
        logger.log(level , msg);
    }
}
