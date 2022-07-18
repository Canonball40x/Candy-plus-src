package cc.candy.candymod.gui.clickguis.clickguinew;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.module.render.ClickGUI;
import cc.candy.candymod.utils.ColorUtil;

import java.awt.*;

public class Component {
    public Module module;
    public float x , y , width , height;
    public Color color;
    public int color0 , hovering;

    public Component(){
        color = ((ClickGUI)CandyMod.m_module.getModuleWithClass(ClickGUI.class)).color.getValue();
        color0 = ColorUtil.toRGBA(30, 35, 30);
        hovering = ColorUtil.toRGBA(170, 170, 170, 100);
    }

    public float doRender(int mouseX , int mouseY , float x , float y) {
        return 0.0F;
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseReleased(int mouseX, int mouseY, int state) {
    }

    public void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton){
    }

    public void onKeyTyped(char typedChar, int keyCode) {
    }

    public float getCenter(float a , float b , float c){
        return a + (b - c) / 2;
    }

    public Boolean isMouseHovering(int mouseX , int mouseY)
    {
        if(x < mouseX && x + width > mouseX)
        {
            if(y < mouseY && y + height > mouseY)
                return true;
        }
        return false;
    }

    public Boolean isMouseHovering(float mouseX , float mouseY , float cx , float cy , float cw , float ch)
    {
        if(cx < mouseX && cx + cw > mouseX)
        {
            if(cy < mouseY && cy + ch > mouseY)
                return true;
        }

        return false;
    }
}
