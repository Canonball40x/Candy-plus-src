package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import net.minecraft.client.settings.GameSettings;

public class CustomFov extends Module {
    public Setting<Float> cfov = register(new Setting("Fov" , 0.0F , 180.0F , 1.0F));

    public CustomFov()
    {
        super("CustomFov" , Categories.RENDER , false , false);
    }

    @Override
    public void onUpdate(){
        mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV , cfov.getValue());
    }
}
