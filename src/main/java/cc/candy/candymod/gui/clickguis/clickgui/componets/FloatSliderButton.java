package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class FloatSliderButton extends Component {
    private float value;
    private Module module;
    private Setting<Float> setting;
    private boolean changing;
    private float diff;

    public FloatSliderButton(Module module, Setting<Float> setting ,  int x, int width , int height)
    {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.width = width;
        this.height = height;
        this.changing = false;
        this.diff = setting.maxValue - setting.minValue;
        value = getValue();
    }

    @Override
    public void onRender2D(int y)
    {
        this.visible = setting.visible();
        if(visible) {
            this.y = y;

            //render base
            RenderUtil.drawRect(x, y, width, height, buttonColor);
            //render bar
            RenderUtil.drawRect(x, y, width * value, height, enabledColor);
            //render value
            float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
            RenderUtil.drawString(setting.name, x + 5, centeredY, whiteColor, false, 1.0F);
            RenderUtil.drawString(String.valueOf(setting.getValue())
                    , x + width - RenderUtil.getStringWidth(String.valueOf(setting.getValue()), 1.0F) - 3, centeredY, whiteColor, false, 1.0F);


            drawOutLine();
        }
    }

    //events
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_LEFT) {
            changing = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        changing = false;
    }

    @Override
    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {
        if(x - 10 < mouseX && x + width + 10 > mouseX && clickedMouseButton == MouseUtil.MOUSE_LEFT && changing)
        {
            //set bar
            value = ((float) mouseX - this.x) / ((float) this.width);

            //fix value
            if(value > 1.0F)
                value = 1.0F;
            if(value < 0.0F)
                value = 0.0F;

            //setting
            setting.setValue(Math.round((((setting.maxValue - setting.minValue) * value) + setting.minValue) * 10.0F) / 10.0F);
        }
    }

    private float getValue()
    {
        return setting.getValue() / ((setting.maxValue - setting.minValue) * 1.00F);
    }

}
