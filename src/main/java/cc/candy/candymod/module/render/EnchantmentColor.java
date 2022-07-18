package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;

import java.awt.*;

public class EnchantmentColor extends Module {
    public Setting<Color> color = register(new Setting("Color" , new Color(255 , 255 , 255 , 50)));

    public static EnchantmentColor INSTANCE;

    public EnchantmentColor(){
        super("EnchantmentColor" , Categories.RENDER , false , false);
        INSTANCE = this;
    }
}
