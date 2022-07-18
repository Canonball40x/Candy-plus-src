package cc.candy.candymod.gui.clickguis.vapegui;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.module.render.ClickGUI;
import cc.candy.candymod.utils.ColorUtil;

public class Component {
    public float x , y , width , height;
    public int white , gray , panelColor , baseColor , mainColor;

    public Component(){
        white = ColorUtil.toRGBA(255,255,255);
        gray = ColorUtil.toRGBA(200 , 200 , 200);
        panelColor = ColorUtil.toRGBA(19,19,19);
        baseColor = ColorUtil.toRGBA(25,25,25);
        updateColor();
    }

    public void onRender(int mouseX , int mouseY){
        updateColor();
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseReleased(int mouseX, int mouseY, int state) {
    }

    public void onMouseClickMove(int mouseX, int mouseY, int clickedMouseButton){
    }

    public float doRender(float y , int mouseX , int mouseY){
        return 0.0F;
    }

    public float getCenter(float a , float b , float c){
        return a + (b - c) / 2;
    }

    public Boolean isMouseHovering(int mouseX , int mouseY)
    {
        if(x < mouseX && x + width > mouseX) return y < mouseY && y + height > mouseY;
        return false;
    }

    public Boolean isMouseHovering(float mouseX , float mouseY , float cx , float cy , float cw , float ch)
    {
        if(cx < mouseX && cx + cw > mouseX) return cy < mouseY && cy + ch > mouseY;

        return false;
    }

    public void updateColor(){
        mainColor = ColorUtil.toRGBA(((ClickGUI)CandyMod.m_module.getModuleWithClass(ClickGUI.class)).color.getValue());
    }
}
