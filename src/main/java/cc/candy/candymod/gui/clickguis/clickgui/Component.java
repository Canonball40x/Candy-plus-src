package cc.candy.candymod.gui.clickguis.clickgui;

import cc.candy.candymod.CandyMod;
import cc.candy.candymod.gui.font.CFontRenderer;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.RenderUtil;
import cc.candy.candymod.utils.Util;

public class Component implements Util {
    public int x,y,width,height;
    public int  defaultColor , enabledColor , whiteColor , buttonColor , outlineColor;
    public CFontRenderer fontRenderer;
    public boolean visible;

    public Component()
    {
        enabledColor = ColorUtil.toRGBA(230 , 90 , 100 , 255);
        defaultColor = ColorUtil.toRGBA(25 , 25 , 25 , 255);
        whiteColor = ColorUtil.toRGBA(255 , 255 , 255 , 255);
        buttonColor = ColorUtil.toRGBA(35 , 35 , 35 , 255);
        outlineColor = ColorUtil.toRGBA(210 , 70 , 80 , 255);
        fontRenderer = CandyMod.m_font.fontRenderer;
    }

    public void onRender2D(int y)
    {

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

    public Boolean isMouseHovering(int mouseX , int mouseY , int cx , int cy , int cw , int ch)
    {
        if(cx < mouseX && cx + cw > mouseX)
        {
            if(cy < mouseY && cy + ch > mouseY)
                return true;
        }

        return false;
    }

    //events
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

    }

    public void mouseReleased(int mouseX, int mouseY, int state)
    {

    }

    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {

    }

    public void onKeyTyped(char typedChar, int keyCode)
    {

    }

    public void drawOutLine()
    {
        //x
        RenderUtil.drawLine(x , y , x + width , y , 2 , outlineColor);
        RenderUtil.drawLine(x , y + height , x + width , y + height , 2 , outlineColor);
        //y
        RenderUtil.drawLine(x , y , x , y + height , 2 ,outlineColor);
        RenderUtil.drawLine(x + width , y , x + width , y + height , 2 ,outlineColor);
    }

}
