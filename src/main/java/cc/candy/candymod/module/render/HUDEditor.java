package cc.candy.candymod.module.render;

import cc.candy.candymod.module.Module;

public class HUDEditor extends Module {
    public static HUDEditor instance = null;

    public HUDEditor(){
        super("HUDEditor" , Categories.RENDER , false , false);
        instance = this;
    }
}
