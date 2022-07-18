package cc.candy.candymod.module.render;

import cc.candy.candymod.gui.clickguis.CGui;
import cc.candy.candymod.gui.clickguis.clickgui.CandyGUI;
import cc.candy.candymod.gui.clickguis.clickguinew.CandyGUI2;
import cc.candy.candymod.gui.clickguis.vapegui.VapeGui;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGUI extends Module {
    private Setting<type> guiType = register(new Setting("Type" , type.New));
    public Setting<Color> color = register(new Setting("Color" , new Color(210, 0 , 130 , 255) , v -> guiType.getValue() != type.Old));
    public Setting<Boolean> outline = register(new Setting("Outline" , false , v -> guiType.getValue() == type.New));

    public enum type{
        Old ,
        New ,
        //Vape
    }

    public ClickGUI()
    {
        super("ClickGUI" , Categories.RENDER , Keyboard.KEY_Y , false , false);
    }

    @Override
    public void onEnable()
    {
        if(nullCheck())
            return;

        if(!(mc.currentScreen instanceof CGui))
        {
            //if(guiType.getValue() == type.Vape) mc.displayGuiScreen(new VapeGui());
            if(guiType.getValue() == type.New) mc.displayGuiScreen(new CandyGUI2());
            else mc.displayGuiScreen(new CandyGUI());
        }
    }

    @Override
    public void onUpdate()
    {
        if(!(mc.currentScreen instanceof CGui) && !HUDEditor.instance.isEnable) disable();
    }
}
