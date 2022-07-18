package cc.candy.candymod.gui.clickguis.clickguinew;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.clickguis.CGui;
import cc.candy.candymod.hud.Hud;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.HUDEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class CandyGUI2 extends CGui {
    public static List<Panel> panelList = new ArrayList<>();
    public static Panel hubPanel;

    @Override
    public void initGui(){
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));

        if(panelList.size() == 0){
            int x = 50;
            for(Module.Categories category : Module.Categories.values()){
                if(category == Module.Categories.HUB) {
                    hubPanel = new Panel(200 , 20 , category);
                    continue;
                }
                panelList.add(new Panel(x , 20 , category));
                x += 120;
            }
        }
    }

    @Override
    public void onGuiClosed(){
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
    }

    @Override
    public void drawScreen(int mouseX , int mouseY , float partialTicks){
        scroll();
        if(HUDEditor.instance.isEnable) hubPanel.onRender(mouseX , mouseY);
        else panelList.forEach(p -> p.onRender(mouseX , mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(HUDEditor.instance.isEnable){
            hubPanel.onMouseClicked(mouseX , mouseY , mouseButton);
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClicked(mouseX , mouseY , mouseButton));
        }
        else panelList.forEach(p -> p.onMouseClicked(mouseX , mouseY , mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(HUDEditor.instance.isEnable) {
            hubPanel.onMouseReleased(mouseX , mouseY , state);
            //post to hub
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseReleased(mouseX , mouseY , state));
        }
        else panelList.forEach(p -> p.onMouseReleased(mouseX , mouseY , state));
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(HUDEditor.instance.isEnable) {
            hubPanel.onMouseClickMove(mouseX , mouseY , clickedMouseButton);
            //post to hub
            CandyMod.m_module.modules
                    .stream().filter(m -> m instanceof Hud).forEach(m -> ((Hud)m).mouseClickMove(mouseX , mouseY , clickedMouseButton , timeSinceLastClick));
        }
        else panelList.forEach(p -> p.onMouseClickMove(mouseX , mouseY , clickedMouseButton));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(Keyboard.KEY_ESCAPE == keyCode){
            if(HUDEditor.instance.isEnable){
                HUDEditor.instance.disable();
                return;
            }
            this.mc.displayGuiScreen(null);
            return;
        }

        if(HUDEditor.instance.isEnable)
            hubPanel.keyTyped(typedChar , keyCode);
        else
        panelList.forEach(p -> p.keyTyped(typedChar , keyCode));
    }

    public void scroll()
    {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if(HUDEditor.instance.isEnable)
                hubPanel.y -= 15;
            else
                panelList.forEach(p -> p.y -= 15);
        } else if (dWheel > 0) {
            if(HUDEditor.instance.isEnable)
                hubPanel.y += 15;
            else
                panelList.forEach(p -> p.y += 15);
        }
    }
}
