package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.gui.clickguis.clickgui.Panel;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

import java.awt.*;

public class ColorButton extends Component {

    private Module module;
    private Setting<Color> setting;
    private boolean isOpening = false;
    private boolean redChanging , greenChanging , blueChanging , alphaChanging = false;

    private float rLx , gLx , bLx , aLx = 0;

    public ColorButton(Module module, Setting<Color> setting , int x , int width , int height)
    {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.width = width;
        this.height = height;

        rLx = (setting.getValue().getRed() / 255.0F);
        gLx = (setting.getValue().getGreen() / 255.0F);
        bLx = (setting.getValue().getBlue() / 255.0F);
        aLx = (setting.getValue().getAlpha() / 255.0F);
    }

    @Override
    public void onRender2D(int y)
    {
        this.visible = setting.visible();
        if(visible)
        {
            this.y = y;
            //render
            RenderUtil.drawRect(x , y, width , height , enabledColor);

            float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
            float rcy1 = y + (this.height - 11) / 2.0F;
            float rcy2 = y + (this.height - 9) / 2.0F;

            RenderUtil.drawRect(x + width - 21 , rcy1 , 12 , 12 , outlineColor);
            RenderUtil.drawRect(x + width - 20 , rcy2 , 10 , 10 , ColorUtil.toRGBA(new Color(setting.getValue().getRed() , setting.getValue().getGreen() , setting.getValue().getBlue() , setting.getValue().getAlpha())));
            RenderUtil.drawString(setting.name , x + 5 , centeredY , whiteColor , false , 1.0F);

            if(isOpening) {
                float colorFieldHeight = 93;

                Panel.Cy += colorFieldHeight;

                float colorFieldY = y + height;
                float colorFieldHeightY = colorFieldY + colorFieldHeight;
                RenderUtil.drawRect(x , colorFieldY , width , colorFieldHeight , defaultColor);

                int gray = ColorUtil.toRGBA(50, 50, 50);
                RenderUtil.drawString("Red" , x + 5 , colorFieldY + 5 , whiteColor , false , 1.0F);
                RenderUtil.drawRect(x + 10 , colorFieldY + 15 , width - 20 , 5 , gray);
                RenderUtil.drawRect( x + 10 + ((width - 20) * rLx) , colorFieldY + 13 , 3 , 9 , ColorUtil.toRGBA(255 , 255 , 255));

                RenderUtil.drawString("Green" , x + 5 , colorFieldY + 27 , whiteColor , false , 1.0F);
                RenderUtil.drawRect(x + 10 , colorFieldY + 37 , width - 20 , 5 , gray);
                RenderUtil.drawRect( x + 10 + ((width - 20) * gLx) , colorFieldY + 35 , 3 , 9 , ColorUtil.toRGBA(255 , 255 , 255));

                RenderUtil.drawString("Blue" , x + 5 , colorFieldY + 49 , whiteColor , false , 1.0F);
                RenderUtil.drawRect(x + 10 , colorFieldY + 59 , width - 20 , 5 , gray);
                RenderUtil.drawRect( x + 10 + ((width - 20) * bLx) , colorFieldY + 57 , 3 , 9 , ColorUtil.toRGBA(255 , 255 , 255));

                RenderUtil.drawString("Alpha" , x + 5 , colorFieldY + 71 , whiteColor , false , 1.0F);
                RenderUtil.drawRect(x + 10 , colorFieldY + 81 , width - 20 , 5 , gray);
                RenderUtil.drawRect( x + 10 + ((width - 20) * aLx) , colorFieldY + 79 , 3 , 9 , ColorUtil.toRGBA(255 , 255 , 255));

                //x
                RenderUtil.drawLine(x , y , x + width , y , 2 , outlineColor);
                RenderUtil.drawLine(x , colorFieldY , x + width , colorFieldY , 2 , outlineColor);
                //y
                RenderUtil.drawLine(x , y , x , colorFieldHeightY , 2 ,outlineColor);
                RenderUtil.drawLine(x + width , y , x + width , colorFieldHeightY , 2 ,outlineColor);
            }
            else
            {
                drawOutLine();
            }
        }
    }

    //events
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        float colorFieldHeight = 70;
        float colorFieldY = y + height;
        float colorFieldHeightY = colorFieldY + colorFieldHeight;

        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_RIGHT)
            isOpening = !isOpening;

        if(!isOpening) return;

        if(isMouseHovering(mouseX , mouseY , x + 8 ,  (int)(colorFieldY + 7) , width - 12 , 15))
            redChanging = true;

        if(isMouseHovering(mouseX , mouseY , x + 8 ,  (int)(colorFieldY + 29) , width - 12 , 15))
            greenChanging = true;

        if(isMouseHovering(mouseX , mouseY , x + 8 ,  (int)(colorFieldY + 51) , width - 12 , 15))
            blueChanging = true;

        if(isMouseHovering(mouseX , mouseY , x + 8 ,  (int)(colorFieldY + 73) , width - 12 , 15))
            alphaChanging = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        redChanging = false;
        greenChanging = false;
        blueChanging = false;
        alphaChanging = false;
    }

    @Override
    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {
        if(!isOpening) return;
        float colorFieldHeight = 70;
        float colorFieldY = y + height;
        float colorFieldHeightY = colorFieldY + colorFieldHeight;

        if(x + 8 < mouseX && x + width - 12 > mouseX && clickedMouseButton == MouseUtil.MOUSE_LEFT && redChanging)
        {
            //set bar
            rLx = (((float) mouseX - (this.x + 10)) / ((float) width - 20));

            //fix value
            if(rLx > 1.0F)
                rLx = 1.0F;
            if(rLx < 0.0F)
                rLx = 0.0F;

            //setting
            setting.setValue(new Color((int)(Math.round(255 * (rLx) * 10.0F) / 10.0F) , setting.getValue().getGreen() , setting.getValue().getBlue() , setting.getValue().getAlpha()));
        }


        if(x + 8 < mouseX && x + width - 12 > mouseX && clickedMouseButton == MouseUtil.MOUSE_LEFT && greenChanging)
        {
            //set bar
            gLx = (((float) mouseX - (this.x + 10)) / ((float) width - 20));

            //fix value
            if(gLx > 1.0F)
                gLx = 1.0F;
            if(gLx < 0.0F)
                gLx = 0.0F;

            //setting
            setting.setValue(new Color(setting.getValue().getRed() , (int)(Math.round(255 * (gLx) * 10.0F) / 10.0F) , setting.getValue().getBlue() , setting.getValue().getAlpha()));
        }

        if(x + 8 < mouseX && x + width - 12 > mouseX && clickedMouseButton == MouseUtil.MOUSE_LEFT && blueChanging)
        {
            //set bar
            bLx = (((float) mouseX - (this.x + 10)) / ((float) width - 20));

            //fix value
            if(bLx > 1.0F)
                bLx = 1.0F;
            if(bLx < 0.0F)
                bLx = 0.0F;

            //setting
            setting.setValue(new Color(setting.getValue().getRed() , setting.getValue().getGreen() , (int)(Math.round(255 * (bLx) * 10.0F) / 10.0F) ,setting.getValue().getAlpha()));
        }

        if(x + 8 < mouseX && x + width - 12 > mouseX && clickedMouseButton == MouseUtil.MOUSE_LEFT && alphaChanging)
        {
            //set bar
            aLx = (((float) mouseX - (this.x + 10)) / ((float) width - 20));

            //fix value
            if(aLx > 1.0F)
                aLx = 1.0F;
            if(aLx < 0.0F)
                aLx = 0.0F;

            //setting
            setting.setValue(new Color(setting.getValue().getRed() , setting.getValue().getGreen() , setting.getValue().getBlue() ,  (int)(Math.round(255 * (aLx) * 10.0F) / 10.0F)));
        }

    }

}
