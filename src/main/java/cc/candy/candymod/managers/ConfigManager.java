package cc.candy.candymod.managers;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.List;

public class ConfigManager {
    public static void saveConfigs()
    {
        String folder = "candy/";
        File dir = new File(folder);
        if(!dir.exists()) dir.mkdirs();
        //make categoryies folder
        for(Module.Categories category : Module.Categories.values())
        {
            File categoryDir = new File(folder + category.name().toLowerCase());
            if(!categoryDir.exists()) categoryDir.mkdirs();
        }

        List<Module> modules = CandyMod.m_module.modules;
        for(Module module : modules)
        {
            try
            {
                module.saveConfig();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void loadConfigs()
    {
        List<Module> modules = CandyMod.m_module.modules;
        for(Module module : modules)
        {
            try
            {
                module.loadConfig();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
