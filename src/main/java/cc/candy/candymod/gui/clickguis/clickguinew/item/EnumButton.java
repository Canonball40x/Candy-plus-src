package cc.candy.candymod.gui.clickguis.clickguinew.item;

import cc.candy.candymod.gui.clickguis.clickguinew.Component;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class EnumButton extends Component {
    public Setting<Enum> setting;

    public EnumButton(Setting<Enum> setting , float x){
        this.setting = setting;
        this.x = x;
        this.width = 100;
        this.height = 16;
    }

    @Override
    public float doRender(int mouseX , int mouseY , float x , float y) {
        this.x = x;
        this.y = y;
        RenderUtil.drawRect(x, y, 100, 16, color0);

        if(isMouseHovering(mouseX , mouseY))
            RenderUtil.drawRect(x, y, 100, 16, hovering);

        float fonty = getCenter(y, 16, RenderUtil.getStringHeight(1.0F));
        RenderUtil.drawString(setting.name + " : " + setting.getValue().name() , x + 6, fonty, ColorUtil.toRGBA(250, 250, 250), false, 1.0F);
        return 16.0F;
    }

    @Override
    public void onMouseClicked(int x , int y , int button)
    {
        if(isMouseHovering(x , y) && button == MouseUtil.MOUSE_LEFT) setting.increaseEnum();
    }
}
