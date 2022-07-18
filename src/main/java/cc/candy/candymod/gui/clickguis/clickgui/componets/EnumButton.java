package cc.candy.candymod.gui.clickguis.clickgui.componets;

import cc.candy.candymod.gui.clickguis.clickgui.Component;
import cc.candy.candymod.module.Module;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class EnumButton extends Component {
    private Module module;
    private Setting<Enum> setting;

    public EnumButton(Module m , Setting<Enum> setting, int x  , int width , int height)
    {
        this.module = m;
        this.setting = setting;
        this.x = x;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onRender2D(int y)
    {
        this.visible = true;
        this.y = y;
        float centeredY = y + (this.height - RenderUtil.getStringHeight(1.0F)) / 2;
        RenderUtil.drawRect(x , y, width , height , enabledColor);
        RenderUtil.drawString(setting.name + " : " + setting.getValue().name() , x + 5 , centeredY , whiteColor , false , 1.0F);
        drawOutLine();
    }

    @Override
    public void mouseClicked(int x , int y , int button)
    {
        if(isMouseHovering(x , y) && button == MouseUtil.MOUSE_LEFT)
        {
            setting.increaseEnum();
        }
    }

}
