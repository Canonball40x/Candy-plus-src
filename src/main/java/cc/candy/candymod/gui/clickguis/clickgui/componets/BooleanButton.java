package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class BooleanButton extends Component {
    public Module module;
    public Setting<Boolean> setting;

    public BooleanButton(Module module, Setting setting , int x , int width , int height)
    {
        this.module = module;
        this.setting = setting;
        this.x = x;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onRender2D(int y)
    {
        this.visible = setting.visible();
        if(visible)
        {
            this.y = y;

            //render
            RenderUtil.drawRect(x , y, width , height , setting.getValue() ? enabledColor : buttonColor);

            float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
            RenderUtil.drawString(setting.name , x + 5 , centeredY , whiteColor , false , 1.0f);

            if(setting.getValue())
                drawOutLine();
        }
    }

    //events
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(isMouseHovering(mouseX , mouseY) && mouseButton == MouseUtil.MOUSE_LEFT)
            setting.setValue(!setting.getValue());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {

    }

    @Override
    public void mouseClickMove(int mouseX , int mouseY , int clickedMouseButton)
    {

    }
}
