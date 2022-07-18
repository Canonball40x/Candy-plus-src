package cc.candy.candymod.gui.clickguis.clickgui;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.CGui;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class CandyGUI extends CGui {

    private static List<Panel> panels = new ArrayList<>();
    private static Panel hubPanel = null;
    public static boolean isHovering = false;

    @Override
    public void initGui()
    {
        //load panels
        if(panels.size() == 0) {
            int x = 30;
            for (Module.Categories c : Module.Categories.values()) {
                if(c == Module.Categories.HUB) {
                    hubPanel = new Panel(c , 30 , 30  , true);
                    continue;
                }
                panels.add(new Panel(c, x, 20  , true));
                CandyMod.Info("Loaded module panel : " + c.name());
                x += 120;
            }
        }
    }

    @Override
    public void onGuiClosed(){
        if(HUDEditor.instance.isEnable){
            HUDEditor.instance.disable();
        }
    }

    @Override
    public void drawScreen(int mouseX , int mouseY , float partialTicks)
    {
        scroll();

        if(HUDEditor.instance.isEnable)
            hubPanel.onRender(mouseX , mouseY , partialTicks);
        else
            panels.forEach(p -> p.onRender(mouseX , mouseY , partialTicks));
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(HUDEditor.instance.isEnable) {
            hubPanel.mouseClicked(mouseX, mouseY, mouseButton);
            //post to hub
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClicked(mouseX , mouseY , mouseButton));
        }
        else{
            panels.forEach(p -> p.mouseClicked(mouseX , mouseY , mouseButton));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        if(HUDEditor.instance.isEnable){
            hubPanel.mouseReleased(mouseX , mouseY , state);
            //post to hub
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseReleased(mouseX , mouseY , state));
        }
        else{
            panels.forEach(p -> p.mouseReleased(mouseX , mouseY , state));
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        if(HUDEditor.instance.isEnable) {
            hubPanel.mouseClickMove(mouseX, mouseY, clickedMouseButton);
            //post to hub
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClickMove(mouseX , mouseY , clickedMouseButton , timeSinceLastClick));
        }
        else{
            panels.forEach(p -> p.mouseClickMove(mouseX , mouseY , clickedMouseButton));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if(HUDEditor.instance.isEnable)
            hubPanel.keyTyped(typedChar , keyCode);
        else
            panels.forEach(p -> p.keyTyped(typedChar, keyCode));

        try {
            super.keyTyped(typedChar, keyCode);
        }
        catch (Exception e) {}
    }

    public void scroll()
    {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if(HUDEditor.instance.isEnable)
                hubPanel.y -= 15;
            else
                panels.forEach(p -> p.y -= 15);
        } else if (dWheel > 0) {
            if(HUDEditor.instance.isEnable)
                hubPanel.y += 15;
            else
            panels.forEach(p -> p.y += 15);
        }
    }


}
