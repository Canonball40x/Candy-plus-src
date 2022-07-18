package cc.candy.candymod.gui.clickguis.vapegui;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.CGui;
import cc.candy.candymod.gui.clickguis.vapegui.components.Panel;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class VapeGui extends CGui {
    public static List<Panel> panelList = new ArrayList<>();
    public Panel hudPanel;

    @Override
    public void initGui(){
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

        if(!panelList.isEmpty()) return;
        int x = 50;
        for(Module.Categories category : Module.Categories.values()){
            if(category == Module.Categories.HUB){
                hudPanel = new Panel(category,100 , 10);
                continue;
            }
            panelList.add(new Panel(category , x , 10));
            x += 120;
        }
    }

    @Override
    public void drawScreen(int mouseX , int mouseY , float partialTicks){
        scroll();
        if(HUDEditor.instance.isEnable) hudPanel.onRender(mouseX , mouseY);
        else panelList.forEach(p -> p.onRender(mouseX , mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(HUDEditor.instance.isEnable){
            hudPanel.onMouseClicked(mouseX , mouseY , mouseButton);
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClicked(mouseX , mouseY , mouseButton));
        }
        else panelList.forEach(p -> p.onMouseClicked(mouseX , mouseY , mouseButton));
    }

    public void scroll()
    {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if(HUDEditor.instance.isEnable) hudPanel.y -= 15;
            else panelList.forEach(p -> p.y -= 15);
        } else if (dWheel > 0) {
            if(HUDEditor.instance.isEnable) hudPanel.y += 15;
            else panelList.forEach(p -> p.y += 15);
        }
    }
}
