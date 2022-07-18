package cc.candy.candymod.gui.clickguis.clickguinew.item;

import cc.candy.candymod.gui.clickguis.clickguinew.Component;
import cc.candy.candymod.setting.Setting;
import cc.candy.candymod.utils.ColorUtil;
import cc.candy.candymod.utils.MouseUtil;
import cc.candy.candymod.utils.RenderUtil;

public class BooleanButton extends Component {
    public Setting<Boolean> setting;

    public BooleanButton(Setting<Boolean> setting , float x){
        this.setting = setting;
        this.x = x;
        this.width = 100;
        this.height = 16;
    }

    @Override
    public float doRender(int mouseX , int mouseY , float x , float y){
        if(setting.visible()){
            this.x = x;
            this.y = y;
            RenderUtil.drawRect(x, y, 100, 16, color0);
            if (setting.getValue())
                RenderUtil.drawRect(x, y, 100, 16, ColorUtil.toRGBA(color));
            if (isMouseHovering(mouseX, mouseY))
                RenderUtil.drawRect(x, y, 100, 16, hovering);

            String name = setting.name;
            float namey = getCenter(y, 16, RenderUtil.getStringHeight(1.0F));
            RenderUtil.drawString(name, x + 6, namey, ColorUtil.toRGBA(250, 250, 250), false, 1.0F);

            return 16.0F;
        }

        return 0.0F;
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == MouseUtil.MOUSE_LEFT && isMouseHovering(mouseX , mouseY)){
            setting.setValue(!setting.getValue());
        }
    }
}
